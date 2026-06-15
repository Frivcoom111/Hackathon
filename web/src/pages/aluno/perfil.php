<?php
require_once __DIR__ . '/../../classes/Aluno.php';
require_once __DIR__ . '/../../classes/Candidatura.php';

\App\Auth\Guard::requireStudent($api->jwt());

function perfilRedirect(): never
{
    header('Location: ' . BASE . 'index.php?page=perfil');
    exit;
}

function salvarMensagemPerfil(array $resp, string $sucessoPadrao, string $erroPadrao): void
{
    if ($resp['success'] ?? false) {
        $_SESSION['perfil_sucesso'] = $resp['message'] ?? $sucessoPadrao;
        return;
    }

    $_SESSION['perfil_erro'] = $resp['message'] ?? $erroPadrao;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? '';

    if ($acao === 'perfil') {
        $dados = [
            'name'  => trim($_POST['name'] ?? ''),
            'email' => trim($_POST['email'] ?? ''),
            'phone' => preg_replace('/\D/', '', $_POST['phone'] ?? ''),
        ];

        salvarMensagemPerfil(
            $api->estudante()->atualizarPerfil($dados),
            'Perfil atualizado com sucesso.',
            'Nao foi possivel atualizar o perfil.'
        );
        perfilRedirect();
    }

    if ($acao === 'endereco') {
        $dados = [
            'street'     => trim($_POST['street'] ?? ''),
            'number'     => trim($_POST['number'] ?? ''),
            'complement' => trim($_POST['complement'] ?? ''),
            'district'   => trim($_POST['district'] ?? ''),
            'city'       => trim($_POST['city'] ?? ''),
            'state'      => strtoupper(trim($_POST['state'] ?? '')),
            'zipCode'    => preg_replace('/\D/', '', $_POST['zipCode'] ?? ''),
        ];

        salvarMensagemPerfil(
            $api->estudante()->salvarEndereco($dados, !empty($_POST['endereco_existente'])),
            'Endereco atualizado com sucesso.',
            'Nao foi possivel atualizar o endereco.'
        );
        perfilRedirect();
    }

    if ($acao === 'cancelar') {
        $id = $_POST['candidatura_id'] ?? '';
        if ($id !== '') {
            salvarMensagemPerfil(
                $api->candidaturas()->cancelar($id),
                'Candidatura cancelada.',
                'Nao foi possivel cancelar a candidatura.'
            );
        }
        perfilRedirect();
    }
}

$perfilResp = $api->estudante()->perfil();
$perfilData = $perfilResp['data'] ?? null;
$aluno = $perfilData ? new Aluno($perfilData) : null;

$candidaturas = [];
$respCandy = $api->candidaturas()->minhas();
foreach ($respCandy['data'] ?? [] as $item) {
    $candidaturas[] = new Candidatura($item);
}

$sucesso = $_SESSION['perfil_sucesso'] ?? '';
$erro = $_SESSION['perfil_erro'] ?? '';
unset($_SESSION['perfil_sucesso'], $_SESSION['perfil_erro']);

$user = $perfilData['user'] ?? [];
$cursos = $perfilData['courses'] ?? [];
$cursoAtual = $cursos[0] ?? [];
$cursoNome = $cursoAtual['course']['name'] ?? 'Curso nao informado';
$cursoStatus = $cursoAtual['status'] ?? 'ACTIVE';
$endereco = $perfilData['address'] ?? [];
$cidade = $endereco['city'] ?? 'Umuarama';
$estado = $endereco['state'] ?? 'PR';
$email = $user['email'] ?? '';
$telefone = $perfilData['phone'] ?? '';

$statusLabels = [
    'ACTIVE' => 'Em andamento',
    'COMPLETED' => 'Finalizado',
    'CANCELLED' => 'Nao finalizado',
];
?>

<section class="profile-page">
  <div class="container py-4 py-lg-5">
    <?php if ($sucesso): ?>
      <div class="alert alert-success alert-dismissible fade show" role="alert">
        <?= htmlspecialchars($sucesso) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
      </div>
    <?php endif; ?>

    <?php if ($erro): ?>
      <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <?= htmlspecialchars($erro) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
      </div>
    <?php endif; ?>

    <?php if (!$aluno): ?>
      <div class="profile-empty">
        <i class="bi bi-person-x"></i>
        <h2>Não foi possivel carregar seu perfil</h2>
        <p>Entre novamente para atualizar seus dados.</p>
        <a href="<?= BASE ?>index.php?page=login" class="btn btn-primary">Fazer login</a>
      </div>
    <?php else: ?>
      <article class="profile-card">
        <div class="profile-cover">
          <img src="<?= BASE ?>assets/images/site/login.png" alt="Portal de Estagios UniALFA">
        </div>

        <div class="profile-main">
          <div class="profile-avatar" aria-hidden="true">
            <?= htmlspecialchars(strtoupper(substr($aluno->getNome(), 0, 1))) ?>
          </div>

          <div class="profile-info">
            <h1><?= htmlspecialchars($aluno->getNome()) ?></h1>
            <p class="profile-headline">Aluno de <?= htmlspecialchars($cursoNome) ?></p>
            <p class="profile-location">
              <?= htmlspecialchars($cidade) ?>, <?= htmlspecialchars($estado) ?> ·
              <button type="button" data-bs-toggle="modal" data-bs-target="#modalContato">Dados de contato</button>
            </p>

            <div class="profile-actions">
              <button type="button" class="btn btn-primary btn-sm" data-bs-toggle="modal" data-bs-target="#modalPerfil">
                Editar perfil
              </button>
              <button type="button" class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#modalEndereco">
                Editar endereco
              </button>
              <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-secondary btn-sm">Buscar vagas</a>
            </div>
          </div>
        </div>
      </article>

      <div class="profile-grid">
        <section class="profile-panel">
          <h2>Sobre</h2>
          <p>
            Perfil academico vinculado ao Portal de Estagios UniALFA. Aqui o aluno acompanha
            suas informacoes principais, curso e candidaturas realizadas.
          </p>
        </section>

        <section class="profile-panel">
          <h2>Formacao</h2>
          <div class="profile-education">
            <div class="profile-education-icon"><i class="bi bi-mortarboard"></i></div>
            <div>
              <strong><?= htmlspecialchars($cursoNome) ?></strong>
              <span><?= htmlspecialchars($statusLabels[$cursoStatus] ?? $cursoStatus) ?></span>
              <?php if (!empty($cursoAtual['startedAt'])): ?>
                <small>Inicio: <?= date('d/m/Y', strtotime($cursoAtual['startedAt'])) ?></small>
              <?php endif; ?>
              <?php if (!empty($cursoAtual['finishedAt'])): ?>
                <small>Conclusao: <?= date('d/m/Y', strtotime($cursoAtual['finishedAt'])) ?></small>
              <?php endif; ?>
            </div>
          </div>
        </section>

        <section class="profile-panel">
          <div class="d-flex align-items-center justify-content-between gap-3 mb-3">
            <h2 class="mb-0">Minhas candidaturas</h2>
            <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Ver vagas</a>
          </div>

          <?php if (empty($candidaturas)): ?>
            <div class="profile-empty-inline">
              <i class="bi bi-briefcase"></i>
              <p>Voce ainda nao se candidatou a nenhuma vaga.</p>
            </div>
          <?php else: ?>
            <div class="profile-applications">
              <?php foreach ($candidaturas as $candidatura): ?>
                <div class="profile-application">
                  <div>
                    <span class="badge <?= $candidatura->getStatusBadgeClass() ?>">
                      <?= $candidatura->getStatusLabel() ?>
                    </span>
                    <p>Candidatura enviada em <?= date('d/m/Y', strtotime($candidatura->getCriadoEm())) ?></p>
                  </div>

                  <?php if ($candidatura->isPendente() || $candidatura->getStatus() === 'ANALYSING'): ?>
                    <form method="POST" action="<?= BASE ?>index.php?page=perfil">
                      <input type="hidden" name="acao" value="cancelar">
                      <input type="hidden" name="candidatura_id" value="<?= htmlspecialchars($candidatura->getId()) ?>">
                      <button type="submit" class="btn btn-outline-danger btn-sm"
                              onclick="return confirm('Cancelar esta candidatura?')">
                        Cancelar
                      </button>
                    </form>
                  <?php endif; ?>
                </div>
              <?php endforeach; ?>
            </div>
          <?php endif; ?>
        </section>
      </div>

      <div class="modal fade" id="modalContato" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Dados de contato</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body">
              <p><strong>E-mail:</strong> <?= htmlspecialchars($email ?: 'Nao informado') ?></p>
              <p><strong>Telefone:</strong> <?= htmlspecialchars($telefone ?: 'Nao informado') ?></p>
              <p><strong>RA:</strong> <?= htmlspecialchars($aluno->getRa()) ?></p>
              <p><strong>CPF:</strong> <?= $aluno->getCpfFormatado() ?></p>
            </div>
          </div>
        </div>
      </div>

      <div class="modal fade" id="modalPerfil" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil">
            <input type="hidden" name="acao" value="perfil">
            <div class="modal-header">
              <h5 class="modal-title">Editar perfil</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body row g-3">
              <div class="col-12">
                <label class="form-label">Nome</label>
                <input type="text" name="name" class="form-control" value="<?= htmlspecialchars($aluno->getNome()) ?>" required>
              </div>
              <div class="col-12">
                <label class="form-label">E-mail</label>
                <input type="email" name="email" class="form-control" value="<?= htmlspecialchars($email) ?>" required>
              </div>
              <div class="col-12">
                <label class="form-label">Telefone</label>
                <input type="text" name="phone" class="form-control" value="<?= htmlspecialchars($telefone) ?>">
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
              <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
          </form>
        </div>
      </div>

      <div class="modal fade" id="modalEndereco" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil">
            <input type="hidden" name="acao" value="endereco">
            <input type="hidden" name="endereco_existente" value="<?= !empty($endereco) ? '1' : '' ?>">
            <div class="modal-header">
              <h5 class="modal-title">Editar endereco</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
            </div>
            <div class="modal-body row g-3">
              <div class="col-md-8">
                <label class="form-label">Rua</label>
                <input type="text" name="street" class="form-control" value="<?= htmlspecialchars($endereco['street'] ?? '') ?>" required>
              </div>
              <div class="col-md-4">
                <label class="form-label">Numero</label>
                <input type="text" name="number" class="form-control" value="<?= htmlspecialchars($endereco['number'] ?? '') ?>" required>
              </div>
              <div class="col-md-6">
                <label class="form-label">Complemento</label>
                <input type="text" name="complement" class="form-control" value="<?= htmlspecialchars($endereco['complement'] ?? '') ?>">
              </div>
              <div class="col-md-6">
                <label class="form-label">Bairro</label>
                <input type="text" name="district" class="form-control" value="<?= htmlspecialchars($endereco['district'] ?? '') ?>" required>
              </div>
              <div class="col-md-5">
                <label class="form-label">Cidade</label>
                <input type="text" name="city" class="form-control" value="<?= htmlspecialchars($cidade) ?>" required>
              </div>
              <div class="col-md-3">
                <label class="form-label">UF</label>
                <input type="text" name="state" class="form-control" value="<?= htmlspecialchars($estado) ?>" maxlength="2" required>
              </div>
              <div class="col-md-4">
                <label class="form-label">CEP</label>
                <input type="text" name="zipCode" class="form-control" value="<?= htmlspecialchars($endereco['zipCode'] ?? '') ?>" required>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
              <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
          </form>
        </div>
      </div>
    <?php endif; ?>
  </div>
</section>
