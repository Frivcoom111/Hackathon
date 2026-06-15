<?php
require_once __DIR__ . '/../../classes/Vaga.php';
require_once __DIR__ . '/../../api.php';
require_once __DIR__ . '/_empresa_menu.php';

$token = empresa_exigir_login();
$vagaId = trim($_GET['vaga_id'] ?? '');
$erro = '';
$vaga = null;

if ($vagaId !== '') {
    $data = api_get('/company/jobs/' . urlencode($vagaId), $token);
    if (!empty($data['data'])) {
        $vaga = new Vaga($data['data']);
    }
}

$modoEdicao = $vaga !== null;
$coursesData = api_get('/courses');
$cursos = api_items($coursesData, 'courses');

if (empty($cursos)) {
    $cursos = demo_courses();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $payload = [
        'title' => trim($_POST['titulo'] ?? ''),
        'description' => trim($_POST['descricao'] ?? ''),
        'area' => trim($_POST['area'] ?? ''),
        'requirements' => trim($_POST['requisitos'] ?? '') ?: null,
        'salary' => $_POST['salario'] !== '' ? (float)$_POST['salario'] : null,
        'location' => trim($_POST['localizacao'] ?? ''),
        'modality' => $_POST['modalidade'] ?? 'PRESENCIAL',
        'status' => $_POST['status'] ?? 'ACTIVE',
        'courseId' => trim($_POST['curso_id'] ?? '') ?: null,
    ];

    $data = $modoEdicao
        ? api_patch_json('/company/jobs/' . urlencode($vagaId), $payload, $token)
        : api_post_json('/company/jobs', $payload, $token);

    if ($data['success'] ?? false) {
        header('Location: ' . BASE . 'index.php?page=empresa-dashboard');
        exit;
    }

    $erro = $data['message'] ?? 'Erro ao salvar a vaga. Tente novamente.';
}

$val = [
    'titulo' => $vaga ? $vaga->getTitulo() : ($_POST['titulo'] ?? ''),
    'descricao' => $vaga ? $vaga->getDescricao() : ($_POST['descricao'] ?? ''),
    'area' => $vaga ? $vaga->getArea() : ($_POST['area'] ?? ''),
    'requisitos' => $vaga ? $vaga->getRequisitos() : ($_POST['requisitos'] ?? ''),
    'salario' => $vaga ? $vaga->getSalario() : ($_POST['salario'] ?? ''),
    'localizacao' => $vaga ? $vaga->getLocalizacao() : ($_POST['localizacao'] ?? ''),
    'modalidade' => $vaga ? $vaga->getModalidade() : ($_POST['modalidade'] ?? 'PRESENCIAL'),
    'status' => $vaga ? $vaga->getStatus() : ($_POST['status'] ?? 'ACTIVE'),
    'curso_id' => $vaga ? $vaga->getCursoId() : ($_POST['curso_id'] ?? ''),
];
?>

<section class="empresa-area-page">
  <div class="container py-4 py-lg-5">
    <?php empresa_menu($modoEdicao ? 'vagas' : 'nova-vaga'); ?>

    <div class="empresa-page-heading">
      <div>
        <span class="empresa-eyebrow">Vagas da empresa</span>
        <h1><?= $modoEdicao ? 'Editar vaga' : 'Publicar nova vaga' ?></h1>
        <p>
          <?= $modoEdicao
              ? 'Atualize as informacoes para manter os alunos bem orientados.'
              : 'Cadastre uma oportunidade clara para atrair candidatos compativeis.' ?>
        </p>
      </div>

      <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i> Voltar ao painel
      </a>
    </div>

    <?php if ($erro): ?>
      <div class="alert alert-danger empresa-alert"><?= htmlspecialchars($erro) ?></div>
    <?php endif; ?>

    <div class="empresa-form-layout">
      <form class="empresa-form-panel" method="POST"
            action="<?= BASE ?>index.php?page=empresa-vaga-form<?= $vagaId ? '&vaga_id=' . urlencode($vagaId) : '' ?>">
        <div class="empresa-form-section">
          <h2>Dados principais</h2>
          <div class="row g-3">
            <div class="col-12">
              <label class="form-label">Titulo da vaga</label>
              <input type="text" name="titulo" class="form-control"
                     placeholder="Ex: Estagio em Desenvolvimento Web" required
                     value="<?= htmlspecialchars($val['titulo']) ?>">
            </div>

            <div class="col-md-6">
              <label class="form-label">Area</label>
              <input type="text" name="area" class="form-control"
                     placeholder="Ex: Tecnologia" required
                     value="<?= htmlspecialchars($val['area']) ?>">
            </div>

            <div class="col-md-6">
              <label class="form-label">Curso relacionado</label>
              <select name="curso_id" class="form-select">
                <option value="">Qualquer curso</option>
                <?php foreach ($cursos as $curso): ?>
                  <?php $cursoId = (string)($curso['id'] ?? ''); ?>
                  <option value="<?= htmlspecialchars($cursoId) ?>" <?= $val['curso_id'] === $cursoId ? 'selected' : '' ?>>
                    <?= htmlspecialchars($curso['name'] ?? 'Curso') ?>
                  </option>
                <?php endforeach; ?>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label">Modalidade</label>
              <select name="modalidade" class="form-select">
                <option value="PRESENCIAL" <?= $val['modalidade'] === 'PRESENCIAL' ? 'selected' : '' ?>>Presencial</option>
                <option value="REMOTE" <?= $val['modalidade'] === 'REMOTE' ? 'selected' : '' ?>>Remoto</option>
                <option value="HYBRID" <?= $val['modalidade'] === 'HYBRID' ? 'selected' : '' ?>>Hibrido</option>
              </select>
            </div>

            <div class="col-md-6">
              <label class="form-label">Status</label>
              <select name="status" class="form-select">
                <option value="ACTIVE" <?= $val['status'] === 'ACTIVE' ? 'selected' : '' ?>>Ativa</option>
                <option value="PAUSED" <?= $val['status'] === 'PAUSED' ? 'selected' : '' ?>>Pausada</option>
                <option value="CLOSED" <?= $val['status'] === 'CLOSED' ? 'selected' : '' ?>>Encerrada</option>
              </select>
            </div>

            <div class="col-md-8">
              <label class="form-label">Localizacao</label>
              <input type="text" name="localizacao" class="form-control"
                     placeholder="Ex: Umuarama, PR" required
                     value="<?= htmlspecialchars($val['localizacao']) ?>">
            </div>

            <div class="col-md-4">
              <label class="form-label">Bolsa (R$)</label>
              <input type="number" name="salario" class="form-control"
                     placeholder="Ex: 900" min="0" step="0.01"
                     value="<?= $val['salario'] !== '' && $val['salario'] !== null ? htmlspecialchars((string)$val['salario']) : '' ?>">
            </div>
          </div>
        </div>

        <div class="empresa-form-section">
          <h2>Conteudo da oportunidade</h2>
          <div class="row g-3">
            <div class="col-12">
              <label class="form-label">Descricao</label>
              <textarea name="descricao" class="form-control" rows="5"
                        placeholder="Explique as atividades, rotina e aprendizados da vaga" required><?= htmlspecialchars($val['descricao']) ?></textarea>
            </div>

            <div class="col-12">
              <label class="form-label">Requisitos</label>
              <textarea name="requisitos" class="form-control" rows="4"
                        placeholder="Ex: HTML, CSS, comunicacao, organizacao, disponibilidade no periodo da tarde"><?= htmlspecialchars($val['requisitos'] ?? '') ?></textarea>
            </div>
          </div>
        </div>

        <div class="empresa-form-actions">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-check2-circle me-1"></i>
            <?= $modoEdicao ? 'Salvar alteracoes' : 'Publicar vaga' ?>
          </button>
          <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-outline-secondary">Cancelar</a>
        </div>
      </form>

      <aside class="empresa-side">
        <div class="empresa-panel">
          <span class="empresa-eyebrow">Boa vaga</span>
          <h2>O que preencher</h2>
          <p>Quanto mais objetiva for a vaga, mais facil fica para o aluno entender se combina com a oportunidade.</p>

          <ul class="empresa-check-list">
            <li><i class="bi bi-check2"></i> Explique atividades do dia a dia.</li>
            <li><i class="bi bi-check2"></i> Informe local, modalidade e bolsa.</li>
            <li><i class="bi bi-check2"></i> Evite requisitos muito genericos.</li>
            <li><i class="bi bi-check2"></i> Mantenha o status atualizado.</li>
          </ul>
        </div>
      </aside>
    </div>
  </div>
</section>

