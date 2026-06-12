import { generateSecret, generateURI, verify as verifyTotp } from "otplib";
import qrcode from "qrcode";
import type { Role } from "../../generated/prisma/enums";
import {
  BadRequestError,
  ConflictError,
  ForbiddenError,
  NotFoundError,
  UnauthorizedError,
} from "../../shared/errors/AppError";
import { compareHash, generateHash } from "../../shared/utils/bcryptUtils";
import { generateToken } from "../../shared/utils/generateToken";
import type { AuthRepository } from "./auth.repository";
import type {
  CompanyResponse,
  LoginInput,
  RegisterCompanyInput,
  RegisterStudentInput,
  StudentResponse,
} from "./auth.schema";

const TOTP_ISSUER = "Portal UniALFA";

// Resultado do login: autenticado direto (STUDENT/ADMIN) ou etapa de TOTP (COMPANY).
type LoginResult =
  | { type: "AUTHENTICATED"; token: string }
  | { type: "TOTP_SETUP"; tempToken: string; qrCode: string; requiresSetup: true }
  | { type: "TOTP_REQUIRED"; tempToken: string; requiresVerification: true };

export class AuthService {
  constructor(private readonly authRepository: AuthRepository) {}

  async registerStudent(data: RegisterStudentInput): Promise<StudentResponse> {
    const password = await generateHash(data.password);

    try {
      return await this.authRepository.createStudent({ ...data, password });
    } catch (error) {
      const code = (error as { code?: string }).code;

      if (code === "P2002") {
        throw new ConflictError("E-mail, RA ou CPF já cadastrado.");
      }

      if (code === "P2025") {
        throw new NotFoundError("Curso não encontrado.");
      }

      throw error;
    }
  }

  async registerCompany(data: RegisterCompanyInput): Promise<CompanyResponse> {
    const password = await generateHash(data.password);

    try {
      return await this.authRepository.createCompany({ ...data, password });
    } catch (error) {
      const code = (error as { code?: string }).code;

      if (code === "P2002") {
        throw new ConflictError("E-mail, CNPJ ou CPF já cadastrado.");
      }

      throw error;
    }
  }

  // ─── Login ──────────────────────────────────────────────────────────────────

  // Mensagem sempre genérica ("Credenciais inválidas.") para não revelar se o e-mail existe.
  async login(data: LoginInput): Promise<LoginResult> {
    const user = await this.authRepository.findUserByEmail(data.email);
    if (!user) {
      throw new UnauthorizedError("Credenciais inválidas.");
    }

    const isMatch = await compareHash(data.password, user.password);
    if (!isMatch) {
      throw new UnauthorizedError("Credenciais inválidas.");
    }

    // STUDENT e ADMIN não usam TOTP: token completo direto.
    if (user.role !== "COMPANY") {
      if (!user.isActive) {
        throw new ForbiddenError("Conta desativada.");
      }

      const token = generateToken({
        sub: user.id,
        email: user.email,
        role: user.role,
        mfaVerified: true,
      });
      return { type: "AUTHENTICATED", token };
    }

    // COMPANY: valida aprovação da empresa antes de iniciar o fluxo TOTP.
    const member = await this.authRepository.findCompanyMemberByUserId(user.id);
    if (!member) {
      throw new UnauthorizedError("Credenciais inválidas.");
    }
    if (member.company.status !== "APPROVED") {
      throw new ForbiddenError("Empresa aguardando aprovação ou bloqueada.");
    }
    if (!user.isActive) {
      throw new ForbiddenError("Conta desativada.");
    }

    const tempToken = generateToken({ sub: user.id, email: user.email, role: user.role, mfaVerified: false }, "5m");

    // Primeiro acesso: gera secret e QR para o membro configurar o app autenticador.
    if (!user.totpEnabled) {
      const secret = generateSecret();
      await this.authRepository.saveTotpSecret(user.id, secret);
      const uri = generateURI({ issuer: TOTP_ISSUER, label: user.email, secret });
      const qrCode = await qrcode.toDataURL(uri);
      return { type: "TOTP_SETUP", tempToken, qrCode, requiresSetup: true };
    }

    return { type: "TOTP_REQUIRED", tempToken, requiresVerification: true };
  }

  // ─── TOTP ───────────────────────────────────────────────────────────────────

  async totpSetup(userId: string, email: string): Promise<{ qrCode: string }> {
    const user = await this.authRepository.findUserTotp(userId);
    if (!user) {
      throw new NotFoundError("Usuário não encontrado.");
    }
    if (user.totpEnabled) {
      throw new BadRequestError("TOTP já configurado.");
    }

    // Reaproveita o secret gerado no login; só cria um novo se ainda não existir.
    let secret = user.totpSecret;
    if (!secret) {
      secret = generateSecret();
      await this.authRepository.saveTotpSecret(userId, secret);
    }

    const uri = generateURI({ issuer: TOTP_ISSUER, label: email, secret });
    const qrCode = await qrcode.toDataURL(uri);
    return { qrCode };
  }

  async totpSetupConfirm(userId: string, code: string): Promise<{ token: string }> {
    const email = await this.assertTotpCode(userId, code);
    await this.authRepository.enableTotp(userId);
    return { token: await this.issueCompanyToken(userId, email) };
  }

  async totpVerify(userId: string, code: string): Promise<{ token: string }> {
    const email = await this.assertTotpCode(userId, code);
    return { token: await this.issueCompanyToken(userId, email) };
  }

  // Valida o código TOTP contra o secret salvo; retorna o e-mail para emitir o token.
  private async assertTotpCode(userId: string, code: string): Promise<string> {
    const user = await this.authRepository.findUserTotp(userId);
    if (!user?.totpSecret) {
      throw new BadRequestError("TOTP não iniciado.");
    }
    const { valid } = await verifyTotp({ token: code, secret: user.totpSecret });
    if (!valid) {
      throw new UnauthorizedError("Código inválido.");
    }
    return user.email;
  }

  // Token final do fluxo COMPANY, com mfaVerified e a role do membro embutidos.
  private async issueCompanyToken(userId: string, email: string): Promise<string> {
    const member = await this.authRepository.findCompanyMemberByUserId(userId);
    return generateToken({
      sub: userId,
      email,
      role: "COMPANY",
      mfaVerified: true,
      companyMemberRole: member?.role,
    });
  }

  // ─── Perfil autenticado ──────────────────────────────────────────────────────

  async getMe(userId: string, role: Role) {
    const profile =
      role === "STUDENT"
        ? await this.authRepository.findStudentProfile(userId)
        : role === "COMPANY"
          ? await this.authRepository.findCompanyProfile(userId)
          : await this.authRepository.findUserById(userId);

    if (!profile) {
      throw new NotFoundError("Perfil não encontrado.");
    }
    return profile;
  }
}
