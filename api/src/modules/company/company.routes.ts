import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware, requireCompany, requireCompanyAdmin } from "../../shared/middlewares/auth.middlewares";
import { CompanyController } from "./company.controller";
import { CompanyRepository } from "./company.repository";
import { CompanyService } from "./company.service";

const router = Router();

const repository = new CompanyRepository(prisma);
const service = new CompanyService(repository);
const controller = new CompanyController(service);

// Toda rota exige COMPANY autenticada com MFA verificada.
router.use(authMiddleware, requireCompany);

// ─── Perfil ───────────────────────────────────────────────────────────────────
router.get("/profile", controller.getProfile);
router.patch("/profile", requireCompanyAdmin, controller.updateProfile);

// ─── Membros (somente ADMIN da empresa) ─────────────────────────────────────────
router.get("/members", requireCompanyAdmin, controller.listMembers);
router.patch("/members/:memberId/role", requireCompanyAdmin, controller.changeMemberRole);
router.post("/members/:memberId/totp/reset", requireCompanyAdmin, controller.resetMemberTotp);

// ─── Vagas ──────────────────────────────────────────────────────────────────────
router.get("/jobs", controller.listJobs);
router.post("/jobs", controller.createJob);
router.get("/jobs/:jobId", controller.getJob);
router.patch("/jobs/:jobId", controller.updateJob);
router.patch("/jobs/:jobId/status", controller.changeJobStatus);

// ─── Candidaturas ────────────────────────────────────────────────────────────────
router.get("/jobs/:jobId/applications", controller.listApplications);
router.patch("/jobs/:jobId/applications/:id/status", controller.changeApplicationStatus);

export default router;
