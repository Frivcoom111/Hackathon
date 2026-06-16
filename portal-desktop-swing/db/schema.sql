-- ─────────────────────────────────────────────────────────────────────────────
-- Portal de Estágios UniALFA — Schema completo (MySQL 8.0)
-- Cria as 11 tabelas do sistema. Rode uma vez em um banco vazio.
-- ─────────────────────────────────────────────────────────────────────────────

SET FOREIGN_KEY_CHECKS = 0;

-- ─── Address ──────────────────────────────────────────────────────────────────
CREATE TABLE `Address` (
  `id`         VARCHAR(36)  NOT NULL,
  `street`     VARCHAR(191) NOT NULL,
  `number`     VARCHAR(20)  NOT NULL,
  `complement` VARCHAR(191) NULL,
  `district`   VARCHAR(191) NOT NULL,
  `city`       VARCHAR(191) NOT NULL,
  `state`      VARCHAR(2)   NOT NULL,
  `zipCode`    VARCHAR(10)  NOT NULL,
  `createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── User ─────────────────────────────────────────────────────────────────────
CREATE TABLE `User` (
  `id`          VARCHAR(36)  NOT NULL,
  `email`       VARCHAR(191) NOT NULL,
  `password`    VARCHAR(191) NOT NULL,
  `role`        ENUM('ADMIN', 'COMPANY', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
  `isActive`    TINYINT(1)   NOT NULL DEFAULT 1,
  `totpSecret`  VARCHAR(191) NULL,
  `totpEnabled` TINYINT(1)   NOT NULL DEFAULT 0,
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `User_email_key` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Course ───────────────────────────────────────────────────────────────────
CREATE TABLE `Course` (
  `id`        VARCHAR(36)  NOT NULL,
  `name`      VARCHAR(191) NOT NULL,
  `code`      VARCHAR(50)  NULL,
  `periods`   INT          NOT NULL,
  `isActive`  TINYINT(1)   NOT NULL DEFAULT 1,
  `createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `Course_name_key` (`name`),
  UNIQUE KEY `Course_code_key` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Student ──────────────────────────────────────────────────────────────────
CREATE TABLE `Student` (
  `id`         VARCHAR(36)  NOT NULL,
  `userId`     VARCHAR(36)  NOT NULL,
  `addressId`  VARCHAR(36)  NULL,
  `name`       VARCHAR(191) NOT NULL,
  `ra`         VARCHAR(191) NOT NULL,
  `cpf`        VARCHAR(14)  NOT NULL,
  `phone`      VARCHAR(20)  NULL,
  `isEligible` TINYINT(1)   NOT NULL DEFAULT 1,
  `resumePath` VARCHAR(191) NULL,
  `createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `Student_userId_key`    (`userId`),
  UNIQUE KEY `Student_ra_key`        (`ra`),
  UNIQUE KEY `Student_cpf_key`       (`cpf`),
  UNIQUE KEY `Student_addressId_key` (`addressId`),
  CONSTRAINT `Student_userId_fkey`
    FOREIGN KEY (`userId`)    REFERENCES `User`(`id`)    ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `Student_addressId_fkey`
    FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Certificate ──────────────────────────────────────────────────────────────
CREATE TABLE `Certificate` (
  `id`          VARCHAR(36)  NOT NULL,
  `studentId`   VARCHAR(36)  NOT NULL,
  `name`        VARCHAR(191) NOT NULL,
  `institution` VARCHAR(191) NULL,
  `issuedAt`    DATETIME(3)  NOT NULL,
  `filePath`    VARCHAR(191) NULL,
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  KEY `Certificate_studentId_idx` (`studentId`),
  CONSTRAINT `Certificate_studentId_fkey`
    FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── StudentCourse ────────────────────────────────────────────────────────────
CREATE TABLE `StudentCourse` (
  `id`         VARCHAR(36)  NOT NULL,
  `studentId`  VARCHAR(36)  NOT NULL,
  `courseId`   VARCHAR(36)  NOT NULL,
  `status`     ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
  `startedAt`  DATETIME(3)  NOT NULL,
  `finishedAt` DATETIME(3)  NULL,
  `createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `StudentCourse_studentId_courseId_key` (`studentId`, `courseId`),
  KEY `StudentCourse_courseId_idx` (`courseId`),
  CONSTRAINT `StudentCourse_studentId_fkey`
    FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `StudentCourse_courseId_fkey`
    FOREIGN KEY (`courseId`)  REFERENCES `Course`(`id`)  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Company ──────────────────────────────────────────────────────────────────
CREATE TABLE `Company` (
  `id`          VARCHAR(36)  NOT NULL,
  `addressId`   VARCHAR(36)  NULL,
  `name`        VARCHAR(191) NOT NULL,
  `cnpj`        VARCHAR(18)  NOT NULL,
  `description` LONGTEXT     NULL,
  `phone`       VARCHAR(20)  NULL,
  `status`      ENUM('PENDING', 'ANALYSING', 'APPROVED', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `Company_cnpj_key`      (`cnpj`),
  UNIQUE KEY `Company_addressId_key` (`addressId`),
  CONSTRAINT `Company_addressId_fkey`
    FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── CompanyMember ────────────────────────────────────────────────────────────
CREATE TABLE `CompanyMember` (
  `id`        VARCHAR(36)  NOT NULL,
  `companyId` VARCHAR(36)  NOT NULL,
  `userId`    VARCHAR(36)  NOT NULL,
  `addressId` VARCHAR(36)  NULL,
  `role`      ENUM('ADMIN', 'RECRUITER') NOT NULL DEFAULT 'RECRUITER',
  `name`      VARCHAR(191) NOT NULL,
  `cpf`       VARCHAR(14)  NOT NULL,
  `phone`     VARCHAR(20)  NULL,
  `createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `CompanyMember_userId_key`           (`userId`),
  UNIQUE KEY `CompanyMember_cpf_key`              (`cpf`),
  UNIQUE KEY `CompanyMember_addressId_key`        (`addressId`),
  UNIQUE KEY `CompanyMember_companyId_userId_key` (`companyId`, `userId`),
  KEY `CompanyMember_companyId_idx` (`companyId`),
  CONSTRAINT `CompanyMember_companyId_fkey`
    FOREIGN KEY (`companyId`) REFERENCES `Company`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `CompanyMember_userId_fkey`
    FOREIGN KEY (`userId`)    REFERENCES `User`(`id`)    ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `CompanyMember_addressId_fkey`
    FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Job ──────────────────────────────────────────────────────────────────────
CREATE TABLE `Job` (
  `id`           VARCHAR(36)  NOT NULL,
  `companyId`    VARCHAR(36)  NOT NULL,
  `courseId`     VARCHAR(36)  NULL,
  `title`        VARCHAR(191) NOT NULL,
  `description`  LONGTEXT     NOT NULL,
  `area`         VARCHAR(191) NOT NULL,
  `requirements` LONGTEXT     NULL,
  `salary`       DOUBLE       NULL,
  `location`     VARCHAR(191) NOT NULL,
  `modality`     ENUM('PRESENCIAL', 'REMOTE', 'HYBRID') NOT NULL DEFAULT 'PRESENCIAL',
  `status`       ENUM('ACTIVE', 'PAUSED', 'CLOSED')     NOT NULL DEFAULT 'ACTIVE',
  `deletedAt`    DATETIME(3)  NULL,
  `createdAt`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  KEY `Job_companyId_idx` (`companyId`),
  KEY `Job_courseId_idx`  (`courseId`),
  CONSTRAINT `Job_companyId_fkey`
    FOREIGN KEY (`companyId`) REFERENCES `Company`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `Job_courseId_fkey`
    FOREIGN KEY (`courseId`)  REFERENCES `Course`(`id`)  ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Application ──────────────────────────────────────────────────────────────
CREATE TABLE `Application` (
  `id`          VARCHAR(36)  NOT NULL,
  `studentId`   VARCHAR(36)  NOT NULL,
  `jobId`       VARCHAR(36)  NOT NULL,
  `status`      ENUM('PENDING', 'ANALYSING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
  `resumePath`  VARCHAR(191) NULL,
  `coverLetter` LONGTEXT     NULL,
  `deletedAt`   DATETIME(3)  NULL,
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `Application_studentId_jobId_key` (`studentId`, `jobId`),
  KEY `Application_jobId_idx` (`jobId`),
  CONSTRAINT `Application_studentId_fkey`
    FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Application_jobId_fkey`
    FOREIGN KEY (`jobId`)     REFERENCES `Job`(`id`)     ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Notification ─────────────────────────────────────────────────────────────
CREATE TABLE `Notification` (
  `id`        VARCHAR(36)  NOT NULL,
  `userId`    VARCHAR(36)  NOT NULL,
  `title`     VARCHAR(191) NOT NULL,
  `message`   LONGTEXT     NOT NULL,
  `type`      VARCHAR(191) NOT NULL,
  `isRead`    TINYINT(1)   NOT NULL DEFAULT 0,
  `createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  KEY `Notification_userId_idx` (`userId`),
  CONSTRAINT `Notification_userId_fkey`
    FOREIGN KEY (`userId`) REFERENCES `User`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
