import { ConflictError, NotFoundError } from "../../shared/errors/AppError";
import { generateHash } from "../../shared/utils/bcryptUtils";
import type { AuthRepository } from "./auth.repository";
import type { RegisterStudentInput, StudentResponse } from "./auth.schema";

export class AuthService {
  constructor(private readonly authRepository: AuthRepository) {}

  async registerStudent(input: RegisterStudentInput): Promise<StudentResponse> {
    const password = await generateHash(input.password);

    try {
      return await this.authRepository.createStudent({ ...input, password });
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
}
