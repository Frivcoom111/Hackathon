import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware, requireCompany, requireCompanyAdmin } from "../../shared/middlewares/auth.middlewares";
import { AddressController } from "./address.controller";
import { AddressRepository } from "./address.repository";
import { AddressService } from "./address.service";

const router = Router();

const repository = new AddressRepository(prisma);
const service = new AddressService(repository);
const controller = new AddressController(service);

router.use(authMiddleware);

// Endereço próprio (Student ou CompanyMember). Um por dono.
router.post("/me", controller.createSelf.bind(controller));
router.get("/me", controller.getSelf.bind(controller));
router.put("/me", controller.updateSelf.bind(controller));
router.delete("/me", controller.deleteSelf.bind(controller));

// Endereço da empresa: qualquer membro lê; só ADMIN escreve.
router.post("/company", requireCompany, requireCompanyAdmin, controller.createCompany.bind(controller));
router.get("/company", requireCompany, controller.getCompany.bind(controller));
router.put("/company", requireCompany, requireCompanyAdmin, controller.updateCompany.bind(controller));
router.delete("/company", requireCompany, requireCompanyAdmin, controller.deleteCompany.bind(controller));

export default router;
