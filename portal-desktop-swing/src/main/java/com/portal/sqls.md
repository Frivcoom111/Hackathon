# SQLs de Teste — Portal UniALFA

---

## Empresas de teste (todos os status)

Cole no banco para testar os filtros no JFrame.
> CNPJs armazenados sem formatação (14 dígitos). A máscara é responsabilidade da UI.

```sql
-- PENDING
INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_Tech Solutions', '12345678000190', 'Empresa de desenvolvimento de software e consultoria em TI.', '62991011111', 'PENDING', NOW(3), NOW(3));

INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_Inova Sistemas', '98765432000110', 'Solucoes inovadoras para automacao industrial e ERP.', '62992022222', 'PENDING', NOW(3), NOW(3));

-- ANALYSING
INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_DataBridge Corp', '11222333000144', 'Integracao de dados e business intelligence.', '11933033333', 'ANALYSING', NOW(3), NOW(3));

INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_CodeFactory ME', '44555666000177', 'Fabrica de software sob demanda para medias empresas.', '11944044444', 'ANALYSING', NOW(3), NOW(3));

-- APPROVED
INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_NexoBank Tecnologia', '77888999000100', 'Fintech especializada em solucoes de pagamento digital.', '21955055555', 'APPROVED', NOW(3), NOW(3));

INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_GreenCloud Servicos', '33444555000188', 'Infraestrutura cloud e DevOps para startups.', '21966066666', 'APPROVED', NOW(3), NOW(3));

-- BLOCKED
INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_FastJob Recrutamento', '66777888000122', 'Agencia de recrutamento e selecao de talentos.', '41977077777', 'BLOCKED', NOW(3), NOW(3));

INSERT INTO `Company` (id, name, cnpj, description, phone, status, createdAt, updatedAt)
VALUES (UUID(), 'TESTE_MegaStore Comercio', '22333444000155', 'Rede de varejo com mais de 50 unidades no Brasil.', '41988088888', 'BLOCKED', NOW(3), NOW(3));
```

---

## Limpar empresas de teste

```sql
DELETE FROM `Company` WHERE name LIKE 'TESTE_%';
```
