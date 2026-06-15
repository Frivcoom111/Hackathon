import type { Request, Response } from "express";
import { requireUser } from "../../shared/utils/requireUser";
import { response } from "../../shared/utils/response";
import { addressSchema } from "./address.schema";
import type { AddressService } from "./address.service";

export class AddressController {
  constructor(private readonly addressService: AddressService) {}

  // ─── Endereço próprio ───────────────────────────────────────────────────────

  async getSelf(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const address = await this.addressService.getSelf(user.id, user.role);
    res.status(200).json(response.success(address));
  }

  async createSelf(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = addressSchema.parse(req.body);
    const address = await this.addressService.createSelf(user.id, user.role, data);
    res.status(201).json(response.success(address, "Endereço cadastrado com sucesso."));
  }

  async updateSelf(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = addressSchema.parse(req.body);
    const address = await this.addressService.updateSelf(user.id, user.role, data);
    res.status(200).json(response.success(address, "Endereço atualizado com sucesso."));
  }

  async deleteSelf(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    await this.addressService.deleteSelf(user.id, user.role);
    res.status(204).send();
  }

  // ─── Endereço da empresa ────────────────────────────────────────────────────

  async getCompany(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const address = await this.addressService.getCompany(user.id);
    res.status(200).json(response.success(address));
  }

  async createCompany(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = addressSchema.parse(req.body);
    const address = await this.addressService.createCompany(user.id, data);
    res.status(201).json(response.success(address, "Endereço cadastrado com sucesso."));
  }

  async updateCompany(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = addressSchema.parse(req.body);
    const address = await this.addressService.updateCompany(user.id, data);
    res.status(200).json(response.success(address, "Endereço atualizado com sucesso."));
  }

  async deleteCompany(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    await this.addressService.deleteCompany(user.id);
    res.status(204).send();
  }
}
