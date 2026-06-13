/*
  Warnings:

  - Made the column `description` on table `Company` required. This step will fail if there are existing NULL values in that column.
  - Made the column `phone` on table `Company` required. This step will fail if there are existing NULL values in that column.

*/
-- AlterTable
ALTER TABLE `Company` MODIFY `description` TEXT NOT NULL,
    MODIFY `phone` VARCHAR(11) NOT NULL;
