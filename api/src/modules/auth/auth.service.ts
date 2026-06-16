import { generateSecret, generateURI, verify } from "otplib";
import QRCode from "qrcode";
import type { Role } from "../../generated/prisma/enums";
import { ConflictError, NotFoundError, UnauthorizedError } from "../../shared/errors/AppError";
import { compareHash, generateHash } from "../../shared/utils/bcryptUtils";
import { generateToken } from "../../shared/utils/generateToken";
import type { AuthRepository } from "./auth.repository";
import type { LoginInput, RegisterCompanyInput, RegisterStudentInput, TotpCodeInput } from "./auth.schema";

export class AuthService {
  constructor(private readonly authRepository: AuthRepository) {}

  async login(data: LoginInput) {
    const user = await this.authRepository.findUserForLogin(data.email);

    if (!user || !user.isActive) {
      throw new UnauthorizedError("E-mail ou senha invalidos.");
    }

    const passwordOk = await compareHash(data.password, user.password);
    if (!passwordOk) {
      throw new UnauthorizedError("E-mail ou senha invalidos.");
    }

    if (user.role === "STUDENT") {
      return {
        type: "AUTHENTICATED",
        token: this.makeToken(user, true),
        user: this.publicUser(user),
      };
    }

    const tempToken = this.makeToken(user, false);

    // Primeiro acesso (sem TOTP ativo): gera/salva o secret e ja devolve o QR
    // na propria resposta do login. O front confirma em POST /totp/setup/confirm.
    if (!user.totpEnabled || !user.totpSecret) {
      const secret = user.totpSecret ?? generateSecret();

      if (!user.totpSecret) {
        await this.authRepository.saveTotpSecret(user.id, secret);
      }

      const otpauth = generateURI({
        issuer: "Portal Estagios UniALFA",
        label: user.email,
        secret,
      });
      const qrCode = await QRCode.toDataURL(otpauth);

      return {
        type: "TOTP_SETUP",
        tempToken,
        qrCode,
        otpauth,
        user: this.publicUser(user),
      };
    }

    // Acessos seguintes: TOTP ja ativo, basta validar o codigo em /totp/verify.
    return {
      type: "TOTP_REQUIRED",
      tempToken,
      user: this.publicUser(user),
    };
  }

  async confirmTotp(userId: string, data: TotpCodeInput) {
    const user = await this.findUserForTotp(userId);
    await this.checkTotpCode(user.totpSecret, data.code);

    await this.authRepository.enableTotp(user.id);

    return {
      type: "AUTHENTICATED",
      token: this.makeToken(user, true),
      user: this.publicUser(user),
    };
  }

  async verifyTotp(userId: string, data: TotpCodeInput) {
    const user = await this.findUserForTotp(userId);

    if (!user.totpEnabled) {
      throw new UnauthorizedError("Authenticator ainda nao foi configurado.");
    }

    await this.checkTotpCode(user.totpSecret, data.code);

    return {
      type: "AUTHENTICATED",
      token: this.makeToken(user, true),
      user: this.publicUser(user),
    };
  }

  async registerStudent(data: RegisterStudentInput) {
    await this.ensureCourseExists(data.courseId);
    const password = await generateHash(data.password);

    try {
      return await this.authRepository.createStudent({ ...data, password });
    } catch (error) {
      this.handleUniqueError(error);
      throw error;
    }
  }

  async registerCompany(data: RegisterCompanyInput) {
    const password = await generateHash(data.password);

    try {
      return await this.authRepository.createCompany({ ...data, password });
    } catch (error) {
      this.handleUniqueError(error);
      throw error;
    }
  }

  async me(userId: string, role: Role) {
    if (role === "STUDENT") {
      const profile = await this.authRepository.findStudentProfile(userId);
      if (!profile) throw new NotFoundError("Perfil nao encontrado.");
      return profile;
    }

    if (role === "COMPANY") {
      const profile = await this.authRepository.findCompanyProfile(userId);
      if (!profile) throw new NotFoundError("Perfil nao encontrado.");
      return profile;
    }

    const profile = await this.authRepository.findUserById(userId);
    if (!profile) throw new NotFoundError("Perfil nao encontrado.");
    return profile;
  }

  private async ensureCourseExists(courseId: string) {
    const course = await this.authRepository.findCourseById(courseId);
    if (!course) throw new NotFoundError("Curso nao encontrado.");
  }

  private async findUserForTotp(userId: string) {
    const user = await this.authRepository.findUserForTotp(userId);

    if (!user || !user.isActive) {
      throw new UnauthorizedError("Usuario nao autorizado.");
    }

    if (user.role === "STUDENT") {
      throw new UnauthorizedError("Authenticator e usado apenas para empresa ou administrador.");
    }

    return user;
  }

  private async checkTotpCode(secret: string | null, code: string) {
    const result = secret ? await verify({ secret, token: code }) : null;

    if (!result?.valid) {
      throw new UnauthorizedError("Codigo do Authenticator invalido.");
    }
  }

  private makeToken(
    user: {
      id: string;
      email: string;
      role: Role;
      companyMember?: { role?: "ADMIN" | "RECRUITER" } | null;
    },
    mfaVerified: boolean,
  ) {
    return generateToken({
      sub: user.id,
      email: user.email,
      role: user.role,
      mfaVerified,
      companyMemberRole: user.companyMember?.role,
    });
  }

  private publicUser(user: {
    id: string;
    email: string;
    role: Role;
    student?: { name: string } | null;
    companyMember?: { name: string; company?: { name: string } | null } | null;
  }) {
    return {
      id: user.id,
      email: user.email,
      role: user.role,
      name: user.student?.name ?? user.companyMember?.name ?? user.companyMember?.company?.name ?? "Usuario",
    };
  }

  private handleUniqueError(error: unknown) {
    if ((error as { code?: string }).code === "P2002") {
      throw new ConflictError("Ja existe um cadastro com alguns destes dados.");
    }
  }
}
