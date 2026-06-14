<?php
require_once __DIR__ . '/../../classes/Vaga.php';

// Handle candidatura
$msgCandidatura  = '';
$erroCandidatura = '';
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['vaga_id'])) {
    if (empty($_SESSION['token'])) {
        header('Location: ' . BASE . 'index.php?page=login');
        exit;
    }
    $vagaIdApply = trim($_POST['vaga_id']);
    $ch = curl_init(API_URL . '/jobs/' . $vagaIdApply . '/apply');
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Authorization: Bearer ' . $_SESSION['token'],
        'Content-Type: application/json',
    ]);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode([]));
    $resp = curl_exec($ch);
    $data = json_decode($resp, true);
    if ($data['success'] ?? false) {
        $msgCandidatura = 'Candidatura enviada com sucesso!';
    } else {
        $erroCandidatura = $data['message'] ?? 'Erro ao se candidatar. Tente novamente.';
    }
}

// Lê os filtros enviados pelo formulário (método GET)
$busca    = trim($_GET['busca']  ?? '');
$cidade   = trim($_GET['cidade'] ?? '');
$area     = trim($_GET['area']   ?? '');
$bolsaMin = (int)($_GET['bolsa'] ?? 0);
$modalidades = $_GET['modalidade'] ?? ['PRESENCIAL', 'REMOTE', 'HYBRID'];

// Chama a API via cURL para buscar todas as vagas ativas
$ch = curl_init(API_URL . '/jobs?status=ACTIVE');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$resp = curl_exec($ch);

// Converte o JSON e aplica os filtros no PHP
$vagas = [];
if ($resp) {
    $data = json_decode($resp, true);
    foreach ($data['jobs'] ?? [] as $item) {
        $vaga = new Vaga($item);

        // Filtra por palavra-chave no título
        if ($busca && stripos($vaga->getTitulo(), $busca) === false) continue;

        // Filtra por cidade na localização
        if ($cidade && stripos($vaga->getLocalizacao(), $cidade) === false) continue;

        // Filtra por área
        if ($area && stripos($vaga->getArea(), $area) === false) continue;

        // Filtra por bolsa mínima (ignora vagas sem salário se filtro estiver ativo)
        if ($bolsaMin > 0 && ($vaga->getSalario() ?? 0) < $bolsaMin) continue;

        // Filtra por modalidade (checkboxes)
        if (!in_array($vaga->getModalidade(), (array)$modalidades)) continue;

        // Guarda o nome da empresa que vem junto na resposta da API
        $vaga->nomeEmpresa = $item['company']['name'] ?? 'Empresa';
        $vaga->descricaoCompleta = $item['description'] ?? '';

        $vagas[] = $vaga;
    }
}
?>

<!-- TOPO COM CAMPO DE BUSCA -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Vagas de Estágio</h1>
    <p class="vagas-hero-sub">Encontre a oportunidade ideal para o seu perfil</p>

    <!-- Formulário de busca — envia via GET para recarregar a página com filtros -->
    <form class="vagas-busca" method="GET" action="<?= BASE ?>index.php">
      <input type="hidden" name="page" value="vagas">
      <div class="vagas-busca-inner">
        <div class="vagas-busca-campo">
          <i class="bi bi-search"></i>
          <input type="text" name="busca" placeholder="Cargo ou palavra-chave"
                 class="form-control" value="<?= htmlspecialchars($busca) ?>">
        </div>
        <div class="vagas-busca-campo">
          <i class="bi bi-geo-alt"></i>
          <input type="text" name="cidade" placeholder="Cidade"
                 class="form-control" value="<?= htmlspecialchars($cidade) ?>">
        </div>
        <button type="submit" class="btn btn-warning fw-semibold px-4">Buscar</button>
      </div>
    </form>
  </div>
</section>

<?php if ($msgCandidatura): ?>
<div class="container mt-3">
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    <?= htmlspecialchars($msgCandidatura) ?>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
</div>
<?php endif; ?>
<?php if ($erroCandidatura): ?>
<div class="container mt-3">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <?= htmlspecialchars($erroCandidatura) ?>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
</div>
<?php endif; ?>

<!-- CONTEÚDO: FILTROS + LISTA -->
<section class="vagas-lista-section">
  <div class="container">
    <div class="vagas-lista-layout">

      <!-- SIDEBAR DE FILTROS — envia via GET junto com a busca -->
      <aside class="vagas-filtros">
        <form method="GET" action="<?= BASE ?>index.php" id="form-filtros">
          <input type="hidden" name="page" value="vagas">
          <input type="hidden" name="busca"  value="<?= htmlspecialchars($busca) ?>">
          <input type="hidden" name="cidade" value="<?= htmlspecialchars($cidade) ?>">

          <h6 class="filtros-titulo">Filtrar por</h6>

          <div class="filtro-grupo">
            <label class="filtro-label">Modalidade</label>
            <?php
            // Cada checkbox mantém o estado se o filtro já foi aplicado
            $mods = ['PRESENCIAL' => 'Presencial', 'REMOTE' => 'Remoto', 'HYBRID' => 'Híbrido'];
            foreach ($mods as $val => $label):
                $checked = in_array($val, (array)$modalidades) ? 'checked' : '';
            ?>
            <div class="form-check">
              <input class="form-check-input" type="checkbox"
                     name="modalidade[]" value="<?= $val ?>" <?= $checked ?>>
              <label class="form-check-label"><?= $label ?></label>
            </div>
            <?php endforeach; ?>
          </div>

          <div class="filtro-grupo">
            <label class="filtro-label">Área</label>
            <select class="form-select form-select-sm" name="area">
              <option value="">Todas as áreas</option>
              <?php
              $areas = ['ti' => 'Tecnologia', 'admin' => 'Administração', 'marketing' => 'Marketing',
                        'direito' => 'Direito', 'saude' => 'Saúde', 'educacao' => 'Educação'];
              foreach ($areas as $val => $label):
              ?>
              <option value="<?= $val ?>" <?= $area === $val ? 'selected' : '' ?>><?= $label ?></option>
              <?php endforeach; ?>
            </select>
          </div>

          <div class="filtro-grupo">
            <label class="filtro-label">Bolsa mínima</label>
            <select class="form-select form-select-sm" name="bolsa">
              <?php
              $opcoes = [0 => 'Qualquer valor', 500 => 'A partir de R$ 500',
                         800 => 'A partir de R$ 800', 1000 => 'A partir de R$ 1.000',
                         1500 => 'A partir de R$ 1.500'];
              foreach ($opcoes as $val => $label):
              ?>
              <option value="<?= $val ?>" <?= $bolsaMin === $val ? 'selected' : '' ?>><?= $label ?></option>
              <?php endforeach; ?>
            </select>
          </div>

          <button type="submit" class="btn btn-primary btn-sm w-100 mt-2">Aplicar filtros</button>
          <!-- Limpar filtros: volta para a página sem parâmetros -->
          <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-link btn-sm w-100 mt-1 text-secondary">
            Limpar filtros
          </a>
        </form>
      </aside>

      <!-- LISTA DE VAGAS -->
      <div class="vagas-lista-conteudo">

        <div class="vagas-lista-header">
          <p class="vagas-total">
            Mostrando <strong><?= count($vagas) ?></strong>
            vaga<?= count($vagas) !== 1 ? 's' : '' ?>
          </p>
        </div>

        <?php if (empty($vagas)): ?>
          <!-- Sem resultados -->
          <div class="vagas-vazio">
            <i class="bi bi-search"></i>
            <p>Nenhuma vaga encontrada com os filtros selecionados.</p>
            <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">
              Limpar filtros
            </a>
          </div>

        <?php else: ?>
          <div class="row g-4">
            <?php foreach ($vagas as $vaga): ?>
              <div class="col-lg-6 col-12">
                <div class="vaga-card">
                  <div class="vaga-card-top">
                    <div class="empresa-logo">
                      <div class="empresa-logo-placeholder" style="display:flex;">
                        <i class="bi bi-building"></i>
                      </div>
                    </div>
                    <span class="vaga-badge">Estágio</span>
                  </div>
                  <h5 class="vaga-titulo"><?= htmlspecialchars($vaga->getTitulo()) ?></h5>
                  <p class="vaga-empresa"><?= htmlspecialchars($vaga->nomeEmpresa) ?></p>
                  <div class="vaga-infos">
                    <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                    <span><i class="bi bi-laptop"></i> <?= htmlspecialchars($vaga->getArea()) ?></span>
                    <span><i class="bi bi-building"></i> <?= $vaga->getModalidadeLabel() ?></span>
                  </div>
                  <div class="vaga-footer">
                    <span class="vaga-bolsa"><?= $vaga->getSalarioFormatado() ?></span>
                    <!-- Passa os dados da vaga para o modal via data attributes -->
                    <button class="btn btn-primary btn-sm" onclick="abrirModal(this)"
                      data-id="<?= htmlspecialchars($vaga->getId()) ?>"
                      data-titulo="<?= htmlspecialchars($vaga->getTitulo()) ?>"
                      data-empresa="<?= htmlspecialchars($vaga->nomeEmpresa) ?>"
                      data-local="<?= htmlspecialchars($vaga->getLocalizacao()) ?>"
                      data-area="<?= htmlspecialchars($vaga->getArea()) ?>"
                      data-modalidade="<?= $vaga->getModalidadeLabel() ?>"
                      data-salario="<?= $vaga->getSalarioFormatado() ?>"
                      data-descricao="<?= htmlspecialchars($vaga->getDescricao()) ?>"
                      data-requisitos="<?= htmlspecialchars($vaga->getRequisitos() ?? '') ?>">
                      Ver vaga
                    </button>
                  </div>
                </div>
              </div>
            <?php endforeach; ?>
          </div>
        <?php endif; ?>

      </div>
    </div>
  </div>
</section>

<!-- MODAL: detalhes da vaga (preenchido via data attributes — sem chamada à API) -->
<div class="modal fade" id="modal-vaga" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content vaga-modal-content">
      <div class="modal-header border-0 pb-0">
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body pt-0 px-4 pb-4">

        <div class="vaga-modal-top">
          <div class="empresa-logo empresa-logo-lg">
            <div class="empresa-logo-placeholder" style="display:flex;">
              <i class="bi bi-building fs-4"></i>
            </div>
          </div>
          <div>
            <h4 class="mb-1" id="modal-titulo"></h4>
            <p class="vaga-empresa mb-0" id="modal-empresa"></p>
          </div>
          <span class="vaga-badge ms-auto align-self-start" id="modal-modalidade"></span>
        </div>

        <div class="vaga-modal-infos" id="modal-infos"></div>
        <hr>

        <h6 class="fw-semibold mb-2">Sobre a vaga</h6>
        <p class="descricao" id="modal-descricao"></p>

        <div id="modal-requisitos-bloco" style="display:none;">
          <h6 class="fw-semibold mb-2 mt-3">Requisitos</h6>
          <p class="descricao" id="modal-requisitos"></p>
        </div>

        <div class="d-flex gap-2 mt-4">
          <form method="POST" action="<?= BASE ?>index.php?page=vagas" id="form-candidatura">
            <input type="hidden" name="vaga_id" id="modal-vaga-id" value="">
            <button type="submit" class="btn btn-primary px-4">Candidatar-se</button>
          </form>
          <button class="btn btn-outline-secondary" data-bs-dismiss="modal">Fechar</button>
        </div>

      </div>
    </div>
  </div>
</div>

<script>
// Preenche o modal com os dados do card clicado (vindo de data attributes — sem API)
function abrirModal(btn) {
  document.getElementById('modal-vaga-id').value         = btn.dataset.id;
  document.getElementById('modal-titulo').textContent    = btn.dataset.titulo;
  document.getElementById('modal-empresa').textContent   = btn.dataset.empresa;
  document.getElementById('modal-modalidade').textContent = btn.dataset.modalidade;
  document.getElementById('modal-descricao').textContent = btn.dataset.descricao;

  document.getElementById('modal-infos').innerHTML = `
    <span><i class="bi bi-geo-alt"></i> ${btn.dataset.local}</span>
    <span><i class="bi bi-currency-dollar"></i> ${btn.dataset.salario}</span>
    <span><i class="bi bi-laptop"></i> ${btn.dataset.area}</span>
  `;

  const requisitos = btn.dataset.requisitos;
  if (requisitos) {
    document.getElementById('modal-requisitos').textContent      = requisitos;
    document.getElementById('modal-requisitos-bloco').style.display = 'block';
  } else {
    document.getElementById('modal-requisitos-bloco').style.display = 'none';
  }

  new bootstrap.Modal(document.getElementById('modal-vaga')).show();
}
</script>
