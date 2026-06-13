import type { PrismaClient } from "../../generated/prisma/client";
import type { IRepository, Paginated } from "../../interface/repository.interface";
import type {
  CreateUserInput,
  UpdateRoleInput,
  UpdateStatusInput,
  UpdateUserInput,
  UserResponse,
} from "./users.schema";

export type UserWithPassword = UserResponse & { password: string };

// ─── Selects ──────────────────────────────────────────────────────────────────

const userSelect = {
  id: true,
  email: true,
  role: true,
  isActive: true,
  createdAt: true,
} as const;

const userWithPasswordSelect = {
  id: true,
  email: true,
  password: true,
  role: true,
  isActive: true,
  createdAt: true,
} as const;

// ─── Repository ───────────────────────────────────────────────────────────────

export class UserRepository implements IRepository<UserResponse, CreateUserInput, UpdateUserInput> {
  constructor(private readonly repository: PrismaClient) {}

  async findAll(skip: number, take: number): Promise<Paginated<UserResponse>> {
    const [data, total] = await this.repository.$transaction([
      this.repository.user.findMany({
        select: userSelect,
        skip,
        take,
        orderBy: { createdAt: "desc" },
      }),
      this.repository.user.count(),
    ]);

    return { data, total };
  }

  async findById(id: string): Promise<UserWithPassword | null> {
    return this.repository.user.findUnique({
      where: { id },
      select: userWithPasswordSelect,
    });
  }

  async create(data: CreateUserInput): Promise<UserResponse> {
    return this.repository.user.create({
      data,
      select: userSelect,
    });
  }

  async update(id: string, data: UpdateUserInput): Promise<UserResponse | null> {
    return this.repository.user.update({
      where: { id },
      data,
      select: userSelect,
    });
  }

  async delete(id: string): Promise<void> {
    await this.repository.user.update({
      where: { id },
      data: { isActive: false },
    });
  }

  async changePassword(id: string, newPasswordHash: string): Promise<void> {
    await this.repository.user.update({
      where: { id },
      data: { password: newPasswordHash },
    });
  }

  async changeRole(id: string, data: UpdateRoleInput): Promise<UserResponse | null> {
    return this.repository.user.update({
      where: { id },
      data,
      select: userSelect,
    });
  }

  async updateStatus(id: string, data: UpdateStatusInput): Promise<UserResponse | null> {
    return this.repository.user.update({
      where: { id },
      data,
      select: userSelect,
    });
  }
}
