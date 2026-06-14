# SQLs de Teste — Portal UniALFA

---

## Criar usuário ADMIN

Senha: `Perigoso@2019`

```sql
INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt)
VALUES (UUID(), 'admin@unialfa.com', '$2a$10$D7N/DPh0qb8hEHUB4hRdvuHgI2LJ36zjI3Ik9Y/7NYueyg9.3iqJy', 'ADMIN', 1, NOW(3), NOW(3));
```

---

IDs fixos por prefixo para facilitar limpeza:
- `aaaa...` → User (alunos)
- `bbbb...` → Student
- `cccc...` → Company (aprovada, usada como FK das vagas)
- `dddd...` → Job
- `eeee...` → Application

Senha de todos os alunos de teste: `AlunoRandom@10`

---

## 1. Users (alunos)

```sql
INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt)
VALUES ('aaaaaaaa-0000-0000-0000-000000000001', 'joao.silva@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt)
VALUES ('aaaaaaaa-0000-0000-0000-000000000002', 'maria.souza@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt)
VALUES ('aaaaaaaa-0000-0000-0000-000000000003', 'carlos.lima@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt)
VALUES ('aaaaaaaa-0000-0000-0000-000000000004', 'ana.costa@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt)
VALUES ('aaaaaaaa-0000-0000-0000-000000000005', 'pedro.alves@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3));
```

---

## 2. Students

```sql
INSERT IGNORE INTO `Student` (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt)
VALUES ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'Joao Silva', '20240001', '11122233344', '62991110001', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `Student` (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt)
VALUES ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002', 'Maria Souza', '20240002', '22233344455', '62992220002', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `Student` (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt)
VALUES ('bbbbbbbb-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000003', 'Carlos Lima', '20240003', '33344455566', '62993330003', 0, NOW(3), NOW(3));

INSERT IGNORE INTO `Student` (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt)
VALUES ('bbbbbbbb-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000004', 'Ana Costa', '20240004', '44455566677', '62994440004', 1, NOW(3), NOW(3));

INSERT IGNORE INTO `Student` (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt)
VALUES ('bbbbbbbb-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000005', 'Pedro Alves', '20240005', '55566677788', '62995550005', 1, NOW(3), NOW(3));
```

---

## 3. Company (aprovada — FK das vagas)

```sql
INSERT IGNORE INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES ('cccccccc-0000-0000-0000-000000000001', 'TESTE_NexoBank Tecnologia', '77888999000100', 'Fintech especializada em solucoes de pagamento digital.', '21955055555', 'APPROVED', NOW(3), NOW(3));
```

---

## 4. Jobs (vagas — todas as modalidades e status)

```sql
INSERT IGNORE INTO `Job` (id, companyId, title, area, location, modality, status, salary, createdAt, updatedAt)
VALUES ('dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'Dev Backend Java', 'Tecnologia', 'Goiania - GO', 'PRESENCIAL', 'ACTIVE', 2500.00, NOW(3), NOW(3));

INSERT IGNORE INTO `Job` (id, companyId, title, area, location, modality, status, salary, createdAt, updatedAt)
VALUES ('dddddddd-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'UX Designer Jr', 'Design', 'Remoto', 'REMOTE', 'ACTIVE', 2000.00, NOW(3), NOW(3));

INSERT IGNORE INTO `Job` (id, companyId, title, area, location, modality, status, salary, createdAt, updatedAt)
VALUES ('dddddddd-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000001', 'Analista de Dados', 'Dados', 'Sao Paulo - SP', 'HYBRID', 'ACTIVE', 3200.00, NOW(3), NOW(3));

INSERT IGNORE INTO `Job` (id, companyId, title, area, location, modality, status, salary, createdAt, updatedAt)
VALUES ('dddddddd-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000001', 'DevOps Jr', 'Infraestrutura', 'Remoto', 'REMOTE', 'PAUSED', 2800.00, NOW(3), NOW(3));

INSERT IGNORE INTO `Job` (id, companyId, title, area, location, modality, status, salary, createdAt, updatedAt)
VALUES ('dddddddd-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000001', 'Frontend React', 'Tecnologia', 'Brasilia - DF', 'HYBRID', 'CLOSED', 2200.00, NOW(3), NOW(3));
```

---

## 5. Applications (candidaturas — todos os status)

```sql
INSERT IGNORE INTO `Application` (id, studentId, jobId, status, createdAt, updatedAt)
VALUES ('eeeeeeee-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000001', 'PENDING', NOW(3), NOW(3));

INSERT IGNORE INTO `Application` (id, studentId, jobId, status, createdAt, updatedAt)
VALUES ('eeeeeeee-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000001', 'APPROVED', NOW(3), NOW(3));

INSERT IGNORE INTO `Application` (id, studentId, jobId, status, createdAt, updatedAt)
VALUES ('eeeeeeee-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000003', 'dddddddd-0000-0000-0000-000000000002', 'ANALYSING', NOW(3), NOW(3));

INSERT IGNORE INTO `Application` (id, studentId, jobId, status, createdAt, updatedAt)
VALUES ('eeeeeeee-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000004', 'dddddddd-0000-0000-0000-000000000003', 'REJECTED', NOW(3), NOW(3));

INSERT IGNORE INTO `Application` (id, studentId, jobId, status, createdAt, updatedAt)
VALUES ('eeeeeeee-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000005', 'dddddddd-0000-0000-0000-000000000004', 'CANCELLED', NOW(3), NOW(3));
```

---

## Limpar tudo de teste

```sql
DELETE FROM `Application` WHERE id LIKE 'eeeeeeee-0000-0000-0000-%';
DELETE FROM `Job`         WHERE id LIKE 'dddddddd-0000-0000-0000-%';
DELETE FROM `Student`     WHERE id LIKE 'bbbbbbbb-0000-0000-0000-%';
DELETE FROM `User`        WHERE id LIKE 'aaaaaaaa-0000-0000-0000-%';
DELETE FROM `Company`     WHERE id LIKE 'cccccccc-0000-0000-0000-%';
```
