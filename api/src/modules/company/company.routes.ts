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

// ─── Perfil da empresa ──────────────────────────────────────────────────────────
router.get("/profile", controller.getProfile.bind(controller));
router.patch("/profile", requireCompanyAdmin, controller.updateProfile.bind(controller));

// ─── Dados próprios do membro autenticado ────────────────────────────────────────
router.patch("/me", controller.updateMe.bind(controller));
router.patch("/me/password", controller.changeMyPassword.bind(controller));

// ─── Membros (somente ADMIN da empresa) ─────────────────────────────────────────
router.get("/members", requireCompanyAdmin, controller.listMembers.bind(controller));
router.post("/members", requireCompanyAdmin, controller.createMember.bind(controller));
router.patch("/members/:memberId", requireCompanyAdmin, controller.updateMember.bind(controller));
router.delete("/members/:memberId", requireCompanyAdmin, controller.deleteMember.bind(controller));
router.post("/members/:memberId/totp/reset", requireCompanyAdmin, controller.resetMemberTotp.bind(controller));

// ─── Vagas ──────────────────────────────────────────────────────────────────────
router.get("/jobs", controller.listJobs.bind(controller));
router.post("/jobs", controller.createJob.bind(controller));
router.get("/jobs/:jobId", controller.getJob.bind(controller));
router.patch("/jobs/:jobId", controller.updateJob.bind(controller));
router.patch("/jobs/:jobId/status", controller.changeJobStatus.bind(controller));

// ─── Candidaturas ────────────────────────────────────────────────────────────────
router.get("/jobs/:jobId/applications", controller.listApplications.bind(controller));
router.patch("/jobs/:jobId/applications/:id/status", controller.changeApplicationStatus.bind(controller));

export default router;
