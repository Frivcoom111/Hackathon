import { ConflictError, NotFoundError, UnauthorizedError } from "../../shared/errors/AppError";
import type { PaginationQuery } from "../../shared/schemas/common.schema";
import { compareHash, generateHash } from "../../shared/utils/bcryptUtils";
import type { PaginationMeta } from "../../shared/utils/response";
import type { UserRepository } from "./users.repository";
import {
  type ChangePasswordInput,
  type CreateUserInput,
  type UpdateRoleInput,
  type UpdateStatusInput,
  type UpdateUserInput,
  type UserResponse,
  userResponseSchema,
} from "./users.schema";

// ─── Service ──────────────────────────────────────────────────────────────────

export class UserService {
  constructor(private readonly userRepository: UserRepository) {}

  async findAll(query: PaginationQuery): Promise<{ data: UserResponse[]; meta: PaginationMeta }> {
    const { page, limit } = query;
    const skip = (page - 1) * limit;

    const { data, total } = await this.userRepository.findAll(skip, limit);

    const meta: PaginationMeta = {
      page,
      limit,
      total,
      totalPages: Math.ceil(total / limit),
    };

    return { data, meta };
  }

  async findById(id: string): Promise<UserResponse> {
    const user = await this.userRepository.findById(id);

    if (!user) throw new NotFoundError("Usuário não encontrado.");

    return userResponseSchema.parse(user);
  }

  async create(data: CreateUserInput): Promise<UserResponse> {
    const passwordHash = await generateHash(data.password);

    try {
      const user = await this.userRepository.create({
        ...data,
        password: passwordHash,
      });

      return userResponseSchema.parse(user);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("E-mail já está em uso.");
      }
      throw error;
    }
  }

  async update(id: string, data: UpdateUserInput): Promise<UserResponse> {
    const exists = await this.userRepository.findById(id);
    if (!exists) throw new NotFoundError("Usuário não encontrado.");

    try {
      const user = await this.userRepository.update(id, data);

      if (!user) throw new NotFoundError("Usuário não encontrado.");

      return userResponseSchema.parse(user);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("E-mail já está em uso.");
      }
      throw error;
    }
  }

  async delete(id: string): Promise<void> {
    const exists = await this.userRepository.findById(id);

    if (!exists) throw new NotFoundError("Usuário não encontrado.");

    await this.userRepository.delete(id);
  }

  async changePassword(id: string, data: ChangePasswordInput): Promise<void> {
    const user = await this.userRepository.findById(id);
    if (!user) throw new NotFoundError("Usuário não encontrado.");

    const isMatch = await compareHash(data.currentPassword, user.password);
    if (!isMatch) throw new UnauthorizedError("Senha atual incorreta.");

    const newHash = await generateHash(data.newPassword);

    await this.userRepository.changePassword(id, newHash);
  }

  async changeRole(id: string, data: UpdateRoleInput): Promise<UserResponse> {
    const exists = await this.userRepository.findById(id);
    if (!exists) throw new NotFoundError("Usuário não encontrado.");

    const user = await this.userRepository.changeRole(id, data);
    if (!user) throw new NotFoundError("Usuário não encontrado.");

    return userResponseSchema.parse(user);
  }

  async updateStatus(id: string, data: UpdateStatusInput): Promise<UserResponse> {
    const exists = await this.userRepository.findById(id);
    if (!exists) throw new NotFoundError("Usuário não encontrado.");

    const user = await this.userRepository.updateStatus(id, data);
    if (!user) throw new NotFoundError("Usuário não encontrado.");

    return userResponseSchema.parse(user);
  }
}
