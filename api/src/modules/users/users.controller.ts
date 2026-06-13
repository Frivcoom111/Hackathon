import type { Request, Response } from "express";
import { idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import { response } from "../../shared/utils/response";
import {
  changePasswordSchema,
  createUserSchema,
  updateRoleSchema,
  updateStatusSchema,
  updateUserSchema,
} from "./users.schema";
import type { UserService } from "./users.service";

export class UserController {
  constructor(private readonly userService: UserService) {}

  async findAll(req: Request, res: Response): Promise<void> {
    const query = paginationQuerySchema.parse(req.query);

    const { data, meta } = await this.userService.findAll(query);

    res.status(200).json(response.paginated(data, meta));
  }

  async findById(req: Request, res: Response): Promise<void> {
    const { id } = idParamsSchema.parse(req.params);

    const user = await this.userService.findById(id);

    res.status(200).json(response.success(user));
  }

  async create(req: Request, res: Response): Promise<void> {
    const data = createUserSchema.parse(req.body);

    const user = await this.userService.create(data);

    res.status(201).json(response.success(user, "Usuário criado com sucesso."));
  }

  async update(req: Request, res: Response): Promise<void> {
    const { id } = idParamsSchema.parse(req.params);
    const data = updateUserSchema.parse(req.body);

    const user = await this.userService.update(id, data);

    res.status(200).json(response.success(user, "Usuário atualizado com sucesso."));
  }

  async delete(req: Request, res: Response): Promise<void> {
    const { id } = idParamsSchema.parse(req.params);

    await this.userService.delete(id);

    res.status(204).send();
  }

  async changePassword(req: Request, res: Response): Promise<void> {
    const { id } = idParamsSchema.parse(req.params);
    const data = changePasswordSchema.parse(req.body);

    await this.userService.changePassword(id, data);

    res.status(200).json(response.success(null, "Senha alterada com sucesso."));
  }

  async changeRole(req: Request, res: Response): Promise<void> {
    const { id } = idParamsSchema.parse(req.params);
    const data = updateRoleSchema.parse(req.body);

    const user = await this.userService.changeRole(id, data);

    res.status(200).json(response.success(user, "Cargo atualizado com sucesso."));
  }

  async updateStatus(req: Request, res: Response): Promise<void> {
    const { id } = idParamsSchema.parse(req.params);
    const data = updateStatusSchema.parse(req.body);

    const user = await this.userService.updateStatus(id, data);

    res.status(200).json(response.success(user, "Status atualizado com sucesso."));
  }
}
