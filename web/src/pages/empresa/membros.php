<?php
\App\Auth\Guard::requireCompanyAdmin($api->jwt());

$erro    = '';
$sucesso = '';

// Usuário logado: usado para impedir que o admin se remova/altere a si mesmo.
$meuUserId = $api->jwt()->getPayload()['sub'] ?? '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? '';

    if ($acao === 'criar') {
        $dados = [
            'name'     => trim($_POST['nome']  ?? ''),
            'email'    => trim($_POST['email'] ?? ''),
            'cpf'      => preg_replace('/\D/', '', $_POST['cpf']   ?? ''),
            'phone'    => preg_replace('/\D/', '', $_POST['phone'] ?? ''),
            'password' => $_POST['senha'] ?? '',
            'role'     => ($_POST['role'] ?? 'RECRUITER') === 'ADMIN' ? 'ADMIN' : 'RECRUITER',
        ];
        $resp = $api->companhia()->criarMembro($dados);
        if ($resp['success'] ?? false) {
            $sucesso = 'Membro adicionado com sucesso!';
        } else {
            $erro = $resp['message'] ?? 'Erro ao adicionar membro.';
        }

    } elseif ($acao === 'role' && !empty($_POST['membro_id'])) {
        $novoRole = ($_POST['role'] ?? 'RECRUITER') === 'ADMIN' ? 'ADMIN' : 'RECRUITER';
        $resp = $api->companhia()->atualizarMembro(trim($_POST['membro_id']), ['role' => $novoRole]);
        if ($resp['success'] ?? false) {
            $sucesso = 'Função do membro atualizada!';
        } else {
            $erro = $resp['message'] ?? 'Erro ao atualizar a função.';
        }

    } elseif ($acao === 'remover' && !empty($_POST['membro_id'])) {
        $resp = $api->companhia()->removerMembro(trim($_POST['membro_id']));
        if ($resp['success'] ?? false) {
            $sucesso = 'Membro removido.';
        } else {
            $erro = $resp['message'] ?? 'Erro ao remover o membro.';
        }

    } elseif ($acao === 'totp' && !empty($_POST['membro_id'])) {
        $resp = $api->companhia()->resetarTotpMembro(trim($_POST['membro_id']));
        if ($resp['success'] ?? false) {
            $sucesso = 'TOTP do membro foi resetado. Ele configurará novamente no próximo login.';
        } else {
            $erro = $resp['message'] ?? 'Erro ao resetar o TOTP.';
        }
    }
}

$respMembros = $api->companhia()->membros();
$membros     = $respMembros['data'] ?? [];

function formatarCpf(string $cpf): string {
    $cpf = preg_replace('/\D/', '', $cpf);
    return strlen($cpf) === 11
        ? substr($cpf, 0, 3) . '.' . substr($cpf, 3, 3) . '.' . substr($cpf, 6, 3) . '-' . substr($cpf, 9, 2)
        : $cpf;
}

function roleLabel(string $role): string {
    return $role === 'ADMIN' ? 'Administrador' : 'Recrutador';
}
?>

<!-- TOPO -->
<section class="vagas-hero">
  <div class="container">
    <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-outline-light btn-sm mb-3">
      <i class="bi bi-arrow-left me-1"></i> Voltar ao painel
    </a>
    <h1 class="vagas-hero-titulo">Membros da Empresa</h1>
    <p class="vagas-hero-sub">Gerencie quem tem acesso ao painel da empresa</p>
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

    <!-- NOVO MEMBRO -->
    <div class="vaga-card p-4 mb-5">
      <h5 class="fw-semibold mb-3"><i class="bi bi-person-plus me-2"></i>Adicionar membro</h5>
      <form method="POST" action="<?= BASE ?>index.php?page=empresa-membros">
        <input type="hidden" name="acao" value="criar">
        <div class="row g-3">

          <div class="col-md-6">
            <label class="form-label" for="m-nome">Nome completo</label>
            <input type="text" name="nome" id="m-nome" class="form-control" required>
          </div>

          <div class="col-md-6">
            <label class="form-label" for="m-email">E-mail</label>
            <input type="email" name="email" id="m-email" class="form-control" required>
          </div>

          <div class="col-md-4">
            <label class="form-label" for="m-cpf">CPF</label>
            <input type="text" name="cpf" id="m-cpf" class="form-control" placeholder="000.000.000-00" maxlength="14" required>
          </div>

          <div class="col-md-4">
            <label class="form-label" for="m-phone">Telefone <span class="text-muted small">(opcional)</span></label>
            <input type="text" name="phone" id="m-phone" class="form-control" placeholder="(44) 99999-9999">
          </div>

          <div class="col-md-4">
            <label class="form-label" for="m-role">Função</label>
            <select name="role" id="m-role" class="form-select">
              <option value="RECRUITER">Recrutador</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label" for="m-senha">Senha</label>
            <div class="input-group">
              <input type="password" name="senha" id="m-senha" class="form-control"
                     placeholder="Mínimo 8 caracteres" required>
              <button class="btn btn-outline-secondary" type="button"
                      onclick="alternarSenha('m-senha', this)" aria-label="Mostrar ou ocultar senha">
                <i class="bi bi-eye"></i>
              </button>
            </div>
            <div class="form-text">Use maiúsculas, minúsculas, número e caractere especial.</div>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary">
              <i class="bi bi-plus-lg me-1"></i> Adicionar membro
            </button>
          </div>

        </div>
      </form>
    </div>

    <!-- LISTA DE MEMBROS -->
    <h4 class="fw-semibold mb-3">Membros atuais</h4>

    <?php if (empty($membros)): ?>
      <div class="vagas-vazio">
        <i class="bi bi-people"></i>
        <p>Nenhum membro encontrado.</p>
      </div>
    <?php else: ?>
      <div class="row g-4">
        <?php foreach ($membros as $m):
            $souEu = ($m['userId'] ?? '') === $meuUserId;
        ?>
          <div class="col-lg-6 col-12">
            <div class="vaga-card">
              <div class="vaga-card-top">
                <div class="empresa-logo">
                  <div class="empresa-logo-placeholder" style="display:flex;">
                    <i class="bi bi-person fs-4"></i>
                  </div>
                </div>
                <span class="badge <?= ($m['role'] ?? '') === 'ADMIN' ? 'bg-primary' : 'bg-secondary' ?> ms-auto">
                  <?= roleLabel($m['role'] ?? 'RECRUITER') ?>
                </span>
              </div>

              <h5 class="vaga-titulo">
                <?= htmlspecialchars($m['name'] ?? 'Membro') ?>
                <?php if ($souEu): ?><span class="text-muted small">(você)</span><?php endif; ?>
              </h5>

              <div class="vaga-infos">
                <span><i class="bi bi-person-badge"></i> CPF: <?= htmlspecialchars(formatarCpf($m['cpf'] ?? '')) ?></span>
                <?php if (!empty($m['phone'])): ?>
                  <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($m['phone']) ?></span>
                <?php endif; ?>
              </div>

              <?php if (!$souEu): ?>
              <div class="vaga-footer flex-wrap gap-2">
                <!-- Alterar função -->
                <form method="POST" action="<?= BASE ?>index.php?page=empresa-membros" class="d-flex gap-2 align-items-center">
                  <input type="hidden" name="acao" value="role">
                  <input type="hidden" name="membro_id" value="<?= htmlspecialchars($m['id'] ?? '') ?>">
                  <select name="role" class="form-select form-select-sm" style="width:auto;" aria-label="Função do membro">
                    <option value="RECRUITER" <?= ($m['role'] ?? '') === 'RECRUITER' ? 'selected' : '' ?>>Recrutador</option>
                    <option value="ADMIN"     <?= ($m['role'] ?? '') === 'ADMIN'     ? 'selected' : '' ?>>Administrador</option>
                  </select>
                  <button type="submit" class="btn btn-outline-primary btn-sm">Salvar função</button>
                </form>

                <!-- Resetar TOTP -->
                <form method="POST" action="<?= BASE ?>index.php?page=empresa-membros">
                  <input type="hidden" name="acao" value="totp">
                  <input type="hidden" name="membro_id" value="<?= htmlspecialchars($m['id'] ?? '') ?>">
                  <button type="submit" class="btn btn-outline-secondary btn-sm"
                          onclick="return confirm('Resetar o TOTP deste membro? Ele terá que reconfigurar no próximo login.')">
                    <i class="bi bi-shield-lock"></i> Resetar TOTP
                  </button>
                </form>

                <!-- Remover -->
                <form method="POST" action="<?= BASE ?>index.php?page=empresa-membros">
                  <input type="hidden" name="acao" value="remover">
                  <input type="hidden" name="membro_id" value="<?= htmlspecialchars($m['id'] ?? '') ?>">
                  <button type="submit" class="btn btn-outline-danger btn-sm"
                          onclick="return confirm('Remover este membro da empresa?')">
                    <i class="bi bi-trash"></i> Remover
                  </button>
                </form>
              </div>
              <?php endif; ?>

            </div>
          </div>
        <?php endforeach; ?>
      </div>
    <?php endif; ?>

  </div>
</section>

<script>
function alternarSenha(id, btn) {
  const campo = document.getElementById(id);
  const icone = btn.querySelector('i');
  const mostrar = campo.type === 'password';
  campo.type = mostrar ? 'text' : 'password';
  icone.className = mostrar ? 'bi bi-eye-slash' : 'bi bi-eye';
}
</script>
