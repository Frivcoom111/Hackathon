<?php
require_once __DIR__ . '/../../classes/Vaga.php';

if (empty($_SESSION['token'])) {
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

$token  = $_SESSION['token'];
$vagaId = trim($_GET['vaga_id'] ?? '');
$erro   = '';
$sucesso = '';

// ── Se vier vaga_id, carrega os dados para edição ─────────────────────────────
$vaga = null;
if ($vagaId) {
    $ch = curl_init(API_URL . '/company/jobs/' . $vagaId);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $token]);
    $resp = curl_exec($ch);

    $data = json_decode($resp, true);
    if (!empty($data['data'])) {
        $vaga = new Vaga($data['data']);
    }
}

$modoEdicao = $vaga !== null;

// ── Processa o envio do formulário ────────────────────────────────────────────
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $payload = [
        'title'        => trim($_POST['titulo']    ?? ''),
        'description'  => trim($_POST['descricao'] ?? ''),
        'area'         => trim($_POST['area']       ?? ''),
        'requirements' => trim($_POST['requisitos'] ?? '') ?: null,
        'salary'       => $_POST['salario'] !== '' ? (float)$_POST['salario'] : null,
        'location'     => trim($_POST['localizacao'] ?? ''),
        'modality'     => $_POST['modalidade'] ?? 'PRESENCIAL',
    ];

    if ($modoEdicao) {
        // Edição: PATCH /company/jobs/:id
        $ch = curl_init(API_URL . '/company/jobs/' . $vagaId);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PATCH');
    } else {
        // Criação: POST /company/jobs
        $ch = curl_init(API_URL . '/company/jobs');
        curl_setopt($ch, CURLOPT_POST, true);
    }

    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Authorization: Bearer ' . $token,
        'Content-Type: application/json',
    ]);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
    $resp = curl_exec($ch);

    $data = json_decode($resp, true);

    if ($data['success'] ?? false) {
        header('Location: ' . BASE . 'index.php?page=empresa-dashboard');
        exit;
    } else {
        $erro = $data['message'] ?? 'Erro ao salvar a vaga. Tente novamente.';
    }
}

// Valores atuais (para preencher o formulário no modo edição)
$val = [
    'titulo'      => $vaga ? $vaga->getTitulo()      : ($_POST['titulo']      ?? ''),
    'descricao'   => $vaga ? $vaga->getDescricao()   : ($_POST['descricao']   ?? ''),
    'area'        => $vaga ? $vaga->getArea()         : ($_POST['area']        ?? ''),
    'requisitos'  => $vaga ? $vaga->getRequisitos()  : ($_POST['requisitos']  ?? ''),
    'salario'     => $vaga ? $vaga->getSalario()     : ($_POST['salario']     ?? ''),
    'localizacao' => $vaga ? $vaga->getLocalizacao() : ($_POST['localizacao'] ?? ''),
    'modalidade'  => $vaga ? $vaga->getModalidade()  : ($_POST['modalidade']  ?? 'PRESENCIAL'),
];
?>

<!-- TOPO -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo"><?= $modoEdicao ? 'Editar Vaga' : 'Nova Vaga' ?></h1>
    <p class="vagas-hero-sub">
      <?= $modoEdicao ? 'Atualize as informações da vaga' : 'Preencha os dados para publicar uma nova vaga' ?>
    </p>
  </div>
</section>

<section class="vagas-lista-section">
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-lg-7">

        <?php if ($erro): ?>
          <div class="alert alert-danger py-2"><?= htmlspecialchars($erro) ?></div>
        <?php endif; ?>

        <div class="vaga-card p-4">
          <form method="POST"
                action="<?= BASE ?>index.php?page=empresa-vaga-form<?= $vagaId ? '&vaga_id=' . htmlspecialchars($vagaId) : '' ?>">
            <div class="row g-3">

              <div class="col-12">
                <label class="form-label">Título da vaga</label>
                <input type="text" name="titulo" class="form-control"
                       placeholder="Ex: Estágio em Desenvolvimento Web" required
                       value="<?= htmlspecialchars($val['titulo']) ?>">
              </div>

              <div class="col-md-6">
                <label class="form-label">Área</label>
                <input type="text" name="area" class="form-control"
                       placeholder="Ex: Tecnologia, Marketing..." required
                       value="<?= htmlspecialchars($val['area']) ?>">
              </div>

              <div class="col-md-6">
                <label class="form-label">Modalidade</label>
                <select name="modalidade" class="form-select">
                  <option value="PRESENCIAL" <?= $val['modalidade'] === 'PRESENCIAL' ? 'selected' : '' ?>>Presencial</option>
                  <option value="REMOTE"     <?= $val['modalidade'] === 'REMOTE'     ? 'selected' : '' ?>>Remoto</option>
                  <option value="HYBRID"     <?= $val['modalidade'] === 'HYBRID'     ? 'selected' : '' ?>>Híbrido</option>
                </select>
              </div>

              <div class="col-md-8">
                <label class="form-label">Localização</label>
                <input type="text" name="localizacao" class="form-control"
                       placeholder="Ex: Umuarama - PR" required
                       value="<?= htmlspecialchars($val['localizacao']) ?>">
              </div>

              <div class="col-md-4">
                <label class="form-label">Bolsa <span class="text-muted small">(R$)</span></label>
                <input type="number" name="salario" class="form-control"
                       placeholder="Ex: 800" min="0" step="0.01"
                       value="<?= $val['salario'] !== '' && $val['salario'] !== null ? htmlspecialchars($val['salario']) : '' ?>">
              </div>

              <div class="col-12">
                <label class="form-label">Descrição</label>
                <textarea name="descricao" class="form-control" rows="4"
                          placeholder="Descreva as atividades e responsabilidades da vaga" required><?= htmlspecialchars($val['descricao']) ?></textarea>
              </div>

              <div class="col-12">
                <label class="form-label">Requisitos <span class="text-muted small">(opcional)</span></label>
                <textarea name="requisitos" class="form-control" rows="3"
                          placeholder="Liste os requisitos desejados"><?= htmlspecialchars($val['requisitos'] ?? '') ?></textarea>
              </div>

              <div class="col-12 d-flex gap-2 pt-2">
                <button type="submit" class="btn btn-primary">
                  <?= $modoEdicao ? 'Salvar alterações' : 'Publicar vaga' ?>
                </button>
                <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-outline-secondary">
                  Cancelar
                </a>
              </div>

            </div>
          </form>
        </div>

      </div>
    </div>
  </div>
</section>
