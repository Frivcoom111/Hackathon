-- ─────────────────────────────────────────────────────────────────────────────
-- Portal de Estágios UniALFA — Dados de teste (seed)
-- Rode depois do schema.sql. Use INSERT IGNORE, então pode rodar mais de uma vez.
--
-- Senha de TODOS os usuários abaixo (já em hash bcrypt):
--   admin@unialfa.com .......... Perigoso@2019
--   *@aluno.unialfa.edu.br ..... AlunoRandom@10
--
-- Prefixos de id para facilitar a limpeza:
--   aaaaaaaa- = User (alunos)   bbbbbbbb- = Student   cccccccc-0000-0000 = Company
--   cccccccc-0000-0001 = Address  dddddddd- = Job       eeeeeeee- = Application
-- ─────────────────────────────────────────────────────────────────────────────

-- ─── Usuário ADMIN (acesso ao Back Office) ─────────────────────────────────────
INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt) VALUES
  (UUID(), 'admin@unialfa.com', '$2a$10$D7N/DPh0qb8hEHUB4hRdvuHgI2LJ36zjI3Ik9Y/7NYueyg9.3iqJy', 'ADMIN', 1, NOW(3), NOW(3));

-- ─── Users (alunos de teste) ────────────────────────────────────────────────────
INSERT IGNORE INTO `User` (id, email, password, role, isActive, createdAt, updatedAt) VALUES
  ('aaaaaaaa-0000-0000-0000-000000000001', 'joao.silva@aluno.unialfa.edu.br',  '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3)),
  ('aaaaaaaa-0000-0000-0000-000000000002', 'maria.souza@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3)),
  ('aaaaaaaa-0000-0000-0000-000000000003', 'carlos.lima@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3)),
  ('aaaaaaaa-0000-0000-0000-000000000004', 'ana.costa@aluno.unialfa.edu.br',   '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3)),
  ('aaaaaaaa-0000-0000-0000-000000000005', 'pedro.alves@aluno.unialfa.edu.br', '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu', 'STUDENT', 1, NOW(3), NOW(3));

-- ─── Students (Carlos Lima fica isEligible = 0 para testar aluno inapto) ─────────
INSERT IGNORE INTO `Student` (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt) VALUES
  ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001', 'João Silva',  '20240001', '11122233344', '62991110001', 1, NOW(3), NOW(3)),
  ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002', 'Maria Souza', '20240002', '22233344455', '62992220002', 1, NOW(3), NOW(3)),
  ('bbbbbbbb-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000003', 'Carlos Lima', '20240003', '33344455566', '62993330003', 0, NOW(3), NOW(3)),
  ('bbbbbbbb-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000004', 'Ana Costa',   '20240004', '44455566677', '62994440004', 1, NOW(3), NOW(3)),
  ('bbbbbbbb-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000005', 'Pedro Alves', '20240005', '55566677788', '62995550005', 1, NOW(3), NOW(3));

-- ─── Endereços das empresas ─────────────────────────────────────────────────────
INSERT IGNORE INTO `Address` (id, street, number, complement, district, city, state, zipCode, createdAt, updatedAt) VALUES
  ('cccccccc-0000-0001-0000-000000000001', 'Av. Brigadeiro Faria Lima', '3477', '14º andar',        'Itaim Bibi', 'São Paulo',      'SP', '04538133', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000002', 'Setor Comercial Sul Qd 2',  '100',  'Bloco B, Sala 312', 'Asa Sul',    'Brasília',       'DF', '70300902', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000003', 'R. Marechal Floriano',      '228',  'Conjunto 501',      'Centro',     'Curitiba',       'PR', '80010120', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000004', 'R. da Bahia',               '1200', 'Sala 80',           'Centro',     'Belo Horizonte', 'MG', '30160011', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000005', 'Av. Ipiranga',              '6681', 'Prédio 32',         'Partenon',   'Porto Alegre',   'RS', '90619900', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000006', 'Av. Boa Viagem',            '3344', 'Sala 1502',         'Boa Viagem', 'Recife',         'PE', '51020000', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000007', 'R. Chile',                  '512',  'Loja 3',            'Comércio',   'Salvador',       'BA', '40020000', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000008', 'Av. Beira-Mar Norte',       '740',  'Conjunto 9',        'Centro',     'Florianópolis',  'SC', '88015700', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000009', 'Av. Santos Dumont',         '1789', 'Andar 6',           'Aldeota',    'Fortaleza',      'CE', '60150160', NOW(3), NOW(3)),
  ('cccccccc-0000-0001-0000-000000000010', 'Av. Eduardo Ribeiro',       '620',  'Sala 204',          'Centro',     'Manaus',         'AM', '69010001', NOW(3), NOW(3));

-- ─── Companies (cobre todos os status: PENDING, ANALYSING, APPROVED, BLOCKED) ────
INSERT IGNORE INTO `Company` (id, name, cnpj, description, phone, status, addressId, createdAt, updatedAt) VALUES
  ('cccccccc-0000-0000-0000-000000000001', 'TechVision Soluções Digitais', '10111222000131', 'Empresa de tecnologia especializada em desenvolvimento de software, plataformas digitais e transformação digital.', '11940010001', 'APPROVED',  'cccccccc-0000-0001-0000-000000000001', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000002', 'Meridian Gestão e Consultoria', '20222333000142', 'Consultoria de gestão empresarial, recursos humanos e estratégia organizacional com atuação nacional.', '61930020002', 'APPROVED',  'cccccccc-0000-0001-0000-000000000002', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000003', 'HealthData Analytics', '30333444000153', 'Startup de saúde digital que desenvolve soluções de análise de dados clínicos e gestão de prontuários.', '41920030003', 'APPROVED',  'cccccccc-0000-0001-0000-000000000003', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000004', 'DataForge Tecnologia', '40444555000164', 'Engenharia de dados e plataformas de big data, recém-cadastrada e aguardando avaliação.', '31940040004', 'PENDING',   'cccccccc-0000-0001-0000-000000000004', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000005', 'Verdex Agronegócios', '50555666000175', 'Agtech focada em monitoramento de lavouras por sensoriamento remoto.', '51950050005', 'PENDING',   'cccccccc-0000-0001-0000-000000000005', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000006', 'NauticPay Pagamentos', '60666777000186', 'Instituição de pagamentos digitais para o setor portuário. Cadastro em análise.', '81960060006', 'ANALYSING', 'cccccccc-0000-0001-0000-000000000006', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000007', 'EducaMais Edtech', '70777888000197', 'Plataforma de educação a distância com trilhas de aprendizagem personalizadas.', '71970070007', 'ANALYSING', 'cccccccc-0000-0001-0000-000000000007', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000008', 'UrbanLog Logística', '80888999000108', 'Operadora de logística urbana e entregas de última milha com frota elétrica.', '48980080008', 'APPROVED',  'cccccccc-0000-0001-0000-000000000008', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000009', 'FinSecure Crédito', '90999000000119', 'Fintech de crédito pessoal bloqueada por pendências cadastrais.', '85990090009', 'BLOCKED',   'cccccccc-0000-0001-0000-000000000009', NOW(3), NOW(3)),
  ('cccccccc-0000-0000-0000-000000000010', 'QuickFood Delivery', '11000111000120', 'Marketplace de delivery de alimentos bloqueado após denúncias.', '92900100010', 'BLOCKED',   'cccccccc-0000-0001-0000-000000000010', NOW(3), NOW(3));

-- ─── Jobs (cobre modalidades PRESENCIAL/REMOTE/HYBRID e status ACTIVE/PAUSED/CLOSED)
-- Obs.: description é NOT NULL no schema, então preenchemos uma descrição curta.
INSERT IGNORE INTO `Job` (id, companyId, title, description, area, location, modality, status, salary, createdAt, updatedAt) VALUES
  ('dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'Desenvolvedor Java Sênior',           'Vaga de estágio em desenvolvimento Java.', 'Tecnologia',       'Umuarama - PR', 'PRESENCIAL', 'ACTIVE', 1500.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000001', 'Engenheiro de Software Full Stack',   'Vaga de estágio full stack.',              'Tecnologia',       'Umuarama - PR', 'HYBRID',     'ACTIVE', 1800.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000003', 'cccccccc-0000-0000-0000-000000000001', 'UX/UI Designer Pleno',                'Vaga de estágio em design de produto.',    'Design',           'Remoto',        'REMOTE',     'ACTIVE', 1200.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000004', 'cccccccc-0000-0000-0000-000000000001', 'DevOps / Cloud Engineer',             'Vaga de estágio em infraestrutura cloud.', 'Infraestrutura',   'Remoto',        'REMOTE',     'PAUSED', 1600.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000005', 'cccccccc-0000-0000-0000-000000000001', 'Desenvolvedor Mobile React Native',   'Vaga de estágio mobile.',                  'Tecnologia',       'Remoto',        'REMOTE',     'CLOSED', 1400.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000006', 'cccccccc-0000-0000-0000-000000000002', 'Analista de RH e Recrutamento',       'Vaga de estágio em recursos humanos.',     'Recursos Humanos', 'Umuarama - PR', 'PRESENCIAL', 'ACTIVE',  900.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000007', 'cccccccc-0000-0000-0000-000000000002', 'Gestor de Projetos Ágeis (Scrum)',    'Vaga de estágio em gestão de projetos.',   'Gestão',           'Umuarama - PR', 'HYBRID',     'ACTIVE', 1200.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000008', 'cccccccc-0000-0000-0000-000000000002', 'Consultor de Transformação Digital',  'Vaga de estágio em consultoria.',          'Consultoria',      'Umuarama - PR', 'HYBRID',     'PAUSED', 1400.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000009', 'cccccccc-0000-0000-0000-000000000003', 'Cientista de Dados Júnior',           'Vaga de estágio em ciência de dados.',     'Dados',            'Remoto',        'REMOTE',     'ACTIVE', 1000.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000010', 'cccccccc-0000-0000-0000-000000000003', 'Analista de Business Intelligence',   'Vaga de estágio em BI.',                   'Dados',            'Umuarama - PR', 'HYBRID',     'ACTIVE', 1300.00, NOW(3), NOW(3)),
  ('dddddddd-0000-0000-0000-000000000011', 'cccccccc-0000-0000-0000-000000000003', 'Analista de Segurança da Informação', 'Vaga de estágio em segurança.',            'Segurança',        'Umuarama - PR', 'PRESENCIAL', 'CLOSED', 1600.00, NOW(3), NOW(3));

-- ─── Applications (cobre todos os status: PENDING/ANALYSING/APPROVED/REJECTED/CANCELLED)
INSERT IGNORE INTO `Application` (id, studentId, jobId, status, createdAt, updatedAt) VALUES
  ('eeeeeeee-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000002', 'ANALYSING', NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000007', 'APPROVED',  NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000003', 'dddddddd-0000-0000-0000-000000000006', 'REJECTED',  NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000004', 'dddddddd-0000-0000-0000-000000000006', 'PENDING',   NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000004', 'dddddddd-0000-0000-0000-000000000010', 'ANALYSING', NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000006', 'bbbbbbbb-0000-0000-0000-000000000005', 'dddddddd-0000-0000-0000-000000000004', 'CANCELLED', NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000007', 'bbbbbbbb-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000001', 'APPROVED',  NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000008', 'bbbbbbbb-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000003', 'PENDING',   NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000009', 'bbbbbbbb-0000-0000-0000-000000000003', 'dddddddd-0000-0000-0000-000000000009', 'CANCELLED', NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000010', 'bbbbbbbb-0000-0000-0000-000000000004', 'dddddddd-0000-0000-0000-000000000007', 'APPROVED',  NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000011', 'bbbbbbbb-0000-0000-0000-000000000005', 'dddddddd-0000-0000-0000-000000000001', 'REJECTED',  NOW(3), NOW(3)),
  ('eeeeeeee-0000-0000-0000-000000000012', 'bbbbbbbb-0000-0000-0000-000000000005', 'dddddddd-0000-0000-0000-000000000010', 'ANALYSING', NOW(3), NOW(3));
