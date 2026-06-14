/*
  Warnings:

  - You are about to drop the column `addressId` on the `CompanyMember` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE `CompanyMember` DROP FOREIGN KEY `CompanyMember_addressId_fkey`;

-- DropIndex
DROP INDEX `CompanyMember_addressId_key` ON `CompanyMember`;

-- AlterTable
ALTER TABLE `CompanyMember` DROP COLUMN `addressId`;
