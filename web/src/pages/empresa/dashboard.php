<?php
require_once __DIR__ . '/../../classes/Empresa.php';
require_once __DIR__ . '/../../classes/Vaga.php';

\App\Auth\Guard::requireCompany($api->jwt());

// Só o ADMIN da empresa pode editar perfil e gerenciar membros.
$ehAdminEmpresa = $api->jwt()->isCompanyAdmin();

$erro    = '';
$sucesso = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? '';

    // Alterar status da vaga — liberado a qualquer membro da empresa.
    if ($acao === 'status_vaga' && !empty($_POST['vaga_id'])) {
        $status = in_array($_POST['status'] ?? '', ['ACTIVE', 'PAUSED', 'CLOSED'], true)
            ? $_POST['status'] : 'ACTIVE';
        $resp = $api->companhia()->alterarStatusVaga(trim($_POST['vaga_id']), $status);
        $sucesso = ($resp['success'] ?? false) ? 'Status da vaga atualizado!' : '';
        $erro    = ($resp['success'] ?? false) ? '' : ($resp['message'] ?? 'Erro ao alterar o status da vaga.');

    // Editar perfil da empresa — somente ADMIN.
    } elseif ($acao === 'editar_empresa' && $ehAdminEmpresa) {
        $dados = [
            'name'        => trim($_POST['nome']      ?? ''),
            'description' => trim($_POST['descricao'] ?? ''),
            'phone'       => preg_replace('/\D/', '', $_POST['phone'] ?? ''),
        ];
        $resp = $api->companhia()->atualizarPerfil($dados);
        $sucesso = ($resp['success'] ?? false) ? 'Dados da empresa atualizados!' : '';
        $erro    = ($resp['success'] ?? false) ? '' : ($resp['message'] ?? 'Erro ao atualizar a empresa.');
    }
}

$empresa = null;
$resp    = $api->companhia()->perfil();
if (!empty($resp['data'])) {
    $empresa = new Empresa($resp['data']);
}

$vagas     = [];
$respVagas = $api->companhia()->vagas();
foreach ($respVagas['data'] ?? [] as $item) {
    $vagas[] = new Vaga($item);
}

function statusVagaLabel(string $status): string {
    return match($status) {
        'ACTIVE' => 'Ativa',
        'PAUSED' => 'Pausada',
        'CLOSED' => 'Encerrada',
        default  => $status,
    };
}

function statusVagaBadge(string $status): string {
    return match($status) {
        'ACTIVE' => 'badge bg-success',
        'PAUSED' => 'badge bg-warning text-dark',
        'CLOSED' => 'badge bg-secondary',
        default  => 'badge bg-light text-dark',
    };
}
?>

<!-- TOPO -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Painel da Empresa</h1>
    <p class="vagas-hero-sub">Gerencie suas vagas e acompanhe candidatos</p>
  </div>
</section>

<section class="vagas-lista-section">
  <div class="container py-4">

    <?php if ($sucesso): ?>
      <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
        <?= htmlspecialchars($sucesso) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
      </div>
    <?php endif; ?>
    <?php if ($erro): ?>
      <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
        <?= htmlspecialchars($erro) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
      </div>
    <?php endif; ?>

    <?php if (!$empresa): ?>
      <div class="text-center py-5">
        <i class="bi bi-building-x fs-2 text-secondary"></i>
        <p class="text-secondary mt-2">Não foi possível carregar os dados da empresa.</p>
        <a href="<?= BASE ?>index.php?page=login" class="btn btn-outline-primary btn-sm">Fazer login novamente</a>
      </div>

    <?php else: ?>

      <!-- INFO DA EMPRESA -->
      <div class="row g-4 mb-5">
        <div class="col-lg-4">
          <div class="vaga-card">
            <div class="vaga-card-top">
              <div class="empresa-logo">
                <div class="empresa-logo-placeholder" style="display:flex;">
                  <i class="bi bi-building fs-4"></i>
                </div>
              </div>
            </div>
            <h5 class="vaga-titulo"><?= htmlspecialchars($empresa->getNome()) ?></h5>
            <div class="vaga-infos">
              <span><i class="bi bi-upc-scan"></i> CNPJ: <?= $empresa->getCnpjFormatado() ?></span>
              <?php if ($empresa->getTelefone()): ?>
                <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($empresa->getTelefone()) ?></span>
              <?php endif; ?>
              <?php if ($empresa->getDescricao()): ?>
                <span><i class="bi bi-info-circle"></i> <?= htmlspecialchars($empresa->getDescricao()) ?></span>
              <?php endif; ?>
            </div>

            <?php if ($ehAdminEmpresa): ?>
            <div class="vaga-footer flex-wrap gap-2">
              <button type="button" class="btn btn-outline-primary btn-sm"
                      data-bs-toggle="modal" data-bs-target="#modal-editar-empresa">
                <i class="bi bi-pencil me-1"></i> Editar dados
              </button>
              <a href="<?= BASE ?>index.php?page=empresa-membros" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-people me-1"></i> Membros
              </a>
            </div>
            <?php endif; ?>
          </div>
        </div>
      </div>

      <?php if ($ehAdminEmpresa): ?>
      <!-- MODAL: editar dados da empresa (somente ADMIN) -->
      <div class="modal fade" id="modal-editar-empresa" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <form method="POST" action="<?= BASE ?>index.php?page=empresa-dashboard">
              <input type="hidden" name="acao" value="editar_empresa">
              <div class="modal-header">
                <h5 class="modal-title">Editar dados da empresa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
              </div>
              <div class="modal-body">
                <div class="mb-3">
                  <label class="form-label" for="emp-nome">Nome da empresa</label>
                  <input type="text" name="nome" id="emp-nome" class="form-control"
                         value="<?= htmlspecialchars($empresa->getNome()) ?>" required>
                </div>
                <div class="mb-3">
                  <label class="form-label" for="emp-phone">Telefone</label>
                  <input type="text" name="phone" id="emp-phone" class="form-control"
                         value="<?= htmlspecialchars($empresa->getTelefone() ?? '') ?>" placeholder="(44) 99999-9999">
                </div>
                <div class="mb-1">
                  <label class="form-label" for="emp-descricao">Descrição</label>
                  <textarea name="descricao" id="emp-descricao" class="form-control" rows="3"><?= htmlspecialchars($empresa->getDescricao() ?? '') ?></textarea>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar alterações</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <?php endif; ?>

      <!-- VAGAS DA EMPRESA -->
      <div class="d-flex align-items-center justify-content-between mb-3">
        <h4 class="fw-semibold mb-0">Minhas Vagas</h4>
        <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-primary btn-sm">
          <i class="bi bi-plus-lg me-1"></i> Nova vaga
        </a>
      </div>

      <?php if (empty($vagas)): ?>
        <div class="vagas-vazio">
          <i class="bi bi-briefcase"></i>
          <p>Você ainda não cadastrou nenhuma vaga.</p>
          <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-outline-primary btn-sm">Criar primeira vaga</a>
        </div>

      <?php else: ?>
        <div class="row g-4">
          <?php foreach ($vagas as $vaga): ?>
            <div class="col-lg-6 col-12">
              <div class="vaga-card">
                <div class="vaga-card-top">
                  <span class="<?= statusVagaBadge($vaga->getStatus()) ?>">
                    <?= statusVagaLabel($vaga->getStatus()) ?>
                  </span>
                  <span class="vaga-badge ms-auto"><?= $vaga->getModalidadeLabel() ?></span>
                </div>

                <h5 class="vaga-titulo"><?= htmlspecialchars($vaga->getTitulo()) ?></h5>

                <div class="vaga-infos">
                  <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                  <span><i class="bi bi-laptop"></i> <?= htmlspecialchars($vaga->getArea()) ?></span>
                </div>

                <div class="vaga-footer flex-wrap gap-2">
                  <span class="vaga-bolsa"><?= $vaga->getSalarioFormatado() ?></span>
                  <div class="d-flex gap-2 flex-wrap">
                    <a href="<?= BASE ?>index.php?page=empresa-candidatos&vaga_id=<?= $vaga->getId() ?>"
                       class="btn btn-outline-primary btn-sm">
                      <i class="bi bi-people"></i> Candidatos
                    </a>
                    <a href="<?= BASE ?>index.php?page=empresa-vaga-form&vaga_id=<?= $vaga->getId() ?>"
                       class="btn btn-outline-secondary btn-sm">
                      <i class="bi bi-pencil"></i> Editar
                    </a>
                  </div>
                </div>

                <!-- Alterar status da vaga -->
                <form method="POST" action="<?= BASE ?>index.php?page=empresa-dashboard"
                      class="d-flex gap-2 align-items-center mt-2">
                  <input type="hidden" name="acao" value="status_vaga">
                  <input type="hidden" name="vaga_id" value="<?= htmlspecialchars($vaga->getId()) ?>">
                  <label class="form-label small mb-0 text-secondary" for="status-<?= $vaga->getId() ?>">Status:</label>
                  <select name="status" id="status-<?= $vaga->getId() ?>" class="form-select form-select-sm" style="width:auto;">
                    <?php foreach (['ACTIVE' => 'Ativa', 'PAUSED' => 'Pausada', 'CLOSED' => 'Encerrada'] as $val => $label): ?>
                      <option value="<?= $val ?>" <?= $vaga->getStatus() === $val ? 'selected' : '' ?>><?= $label ?></option>
                    <?php endforeach; ?>
                  </select>
                  <button type="submit" class="btn btn-outline-secondary btn-sm">Aplicar</button>
                </form>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      <?php endif; ?>

    <?php endif; ?>

  </div>
</section>
