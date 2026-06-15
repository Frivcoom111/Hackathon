<?php
$erro        = '';
$mostrarTotp = false;
$qrCode      = '';
$totpEmail   = $_SESSION['totp_email'] ?? '';

if (!empty($_SESSION['msg_sucesso'])) {
    $sucesso = $_SESSION['msg_sucesso'];
    unset($_SESSION['msg_sucesso']);
} else {
    $sucesso = '';
}

if (isset($_GET['voltar'])) {
    unset($_SESSION['token'], $_SESSION['tempToken'], $_SESSION['totp_tipo'], $_SESSION['qrCode'], $_SESSION['totp_email']);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? 'login';

    // ── Etapa 1: e-mail e senha ───────────────────────────────────────────────
    if ($acao === 'login') {
        $data = $api->auth()->login(
            trim($_POST['email'] ?? ''),
            $_POST['senha'] ?? ''
        );

        if (!($data['success'] ?? false)) {
            $erro = $data['message'] ?? 'Credenciais inválidas.';
        } else {
            $tipo = $data['data']['type'] ?? '';

            if ($tipo === 'AUTHENTICATED') {
                $api->jwt()->save($data['data']['token']);
                header('Location: ' . BASE . 'index.php?page=home');
                exit;

            } elseif ($tipo === 'TOTP_SETUP') {
                $tempToken = $data['data']['tempToken'];
                $api->jwt()->save($tempToken);
                $api->jwt()->saveTempToken($tempToken);
                $_SESSION['totp_tipo'] = 'setup';
                $_SESSION['totp_email'] = $data['data']['user']['email'] ?? trim($_POST['email'] ?? '');

                $setupResp = $api->auth()->totpSetup();
                $qrCode    = $setupResp['data']['qrCode'] ?? '';
                $_SESSION['qrCode'] = $qrCode;
                $totpEmail = $_SESSION['totp_email'];
                $mostrarTotp = true;

            } elseif ($tipo === 'TOTP_REQUIRED') {
                $tempToken = $data['data']['tempToken'];
                $api->jwt()->save($tempToken);
                $api->jwt()->saveTempToken($tempToken);
                $_SESSION['totp_tipo'] = 'verify';
                $_SESSION['totp_email'] = $data['data']['user']['email'] ?? trim($_POST['email'] ?? '');

                $setupResp = $api->auth()->totpSetup();
                $qrCode    = $setupResp['data']['qrCode'] ?? '';
                $_SESSION['qrCode'] = $qrCode;
                $totpEmail = $_SESSION['totp_email'];
                $mostrarTotp = true;
            }
        }

    // ── Etapa 2: código TOTP ─────────────────────────────────────────────────
    } elseif ($acao === 'totp') {
        $totpTipo = $_SESSION['totp_tipo'] ?? 'verify';
        $codigo   = trim($_POST['codigo'] ?? '');

        $data = $totpTipo === 'setup'
            ? $api->auth()->totpSetupConfirm($codigo)
            : $api->auth()->totpVerify($codigo);

        if (!($data['success'] ?? false)) {
            $erro        = $data['message'] ?? 'Código inválido.';
            $mostrarTotp = true;
            $qrCode      = $_SESSION['qrCode'] ?? '';
            $totpEmail   = $_SESSION['totp_email'] ?? '';
        } else {
            $api->jwt()->save($data['data']['token']);
            unset($_SESSION['tempToken'], $_SESSION['totp_tipo'], $_SESSION['qrCode'], $_SESSION['totp_email']);
            header('Location: ' . BASE . 'index.php?page=empresa-dashboard');
            exit;
        }
    }
}
?>

<main class="login-page">
  <div class="login-container">

    <div class="login-imagem">
      <img src="<?= BASE ?>assets/images/site/login.png" alt="Login">
    </div>

    <div class="login-form-box <?= $mostrarTotp ? 'authenticator-form-box' : '' ?>">

      <?php if (!$mostrarTotp): ?>
        <div class="login-tabs">
          <a href="<?= BASE ?>index.php?page=login" class="login-tab active">Entrar</a>
          <a href="<?= BASE ?>index.php?page=cadastro" class="login-tab">Cadastrar</a>
        </div>
      <?php endif; ?>

      <?php if ($sucesso): ?>
        <div class="alert alert-success mb-3"><?= htmlspecialchars($sucesso) ?></div>
      <?php endif; ?>

      <?php if (!$mostrarTotp): ?>

        <!-- Tela 1: login com e-mail e senha -->
        <h2 class="login-titulo">Logar sua conta</h2>
        <p class="login-sub">Bem-vindo de volta!</p>

        <form method="POST" action="<?= BASE ?>index.php?page=login" class="row g-3">
          <input type="hidden" name="acao" value="login">

          <div class="col-12">
            <label class="form-label" for="login-email">E-mail</label>
            <input type="email" id="login-email" name="email" class="form-control"
                   placeholder="seu@email.com" autocomplete="email" inputmode="email" required>
          </div>

          <div class="col-12">
            <label class="form-label" for="login-senha">Senha</label>
            <div class="input-senha">
              <input type="password" id="login-senha" name="senha" class="form-control"
                     placeholder="Sua senha" autocomplete="current-password" required>
              <button type="button" class="btn-ver-senha" onclick="toggleSenha()">
                <i class="bi bi-eye" id="icone-senha"></i>
              </button>
            </div>
          </div>

          <div class="col-12 d-flex justify-content-end">
            <a href="#" class="forgot-link">Esqueci minha senha</a>
          </div>

          <?php if ($erro): ?>
            <div class="col-12">
              <div class="alert alert-danger py-2 mb-0"><?= htmlspecialchars($erro) ?></div>
            </div>
          <?php endif; ?>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Entrar</button>
          </div>
        </form>

      <?php else: ?>

        <div class="authenticator-header">
          <div class="authenticator-icon">
            <i class="bi bi-shield-lock"></i>
          </div>
          <div>
            <span class="authenticator-kicker">Acesso seguro</span>
            <h2 class="login-titulo mb-1">Verificacao da empresa</h2>
            <p class="login-sub mb-0">
              Escaneie o QR Code e informe o codigo gerado no aplicativo autenticador.
            </p>
          </div>
        </div>

        <?php if ($totpEmail): ?>
          <div class="authenticator-account">
            <i class="bi bi-building-check"></i>
            <span><?= htmlspecialchars($totpEmail) ?></span>
          </div>
        <?php endif; ?>

        <div class="authenticator-panel">
          <div class="authenticator-qr-card">
            <span class="authenticator-step">1</span>
            <?php if ($qrCode): ?>
              <img src="<?= htmlspecialchars($qrCode) ?>" alt="QR Code do Authenticator">
              <p>Abra o Google Authenticator, Microsoft Authenticator ou app parecido.</p>
            <?php else: ?>
              <div class="authenticator-lock">
                <i class="bi bi-phone-lock"></i>
              </div>
              <p>O QR Code nao voltou da API. Use o codigo atual do app ja configurado.</p>
            <?php endif; ?>
          </div>

          <form method="POST" action="<?= BASE ?>index.php?page=login" class="authenticator-code-card">
            <input type="hidden" name="acao" value="totp">
            <span class="authenticator-step">2</span>

            <label class="form-label" for="codigo-auth">Codigo de 6 digitos</label>
            <input type="text" name="codigo" id="codigo-auth" class="form-control input-token"
                   placeholder="000000" maxlength="6" inputmode="numeric"
                   autocomplete="one-time-code" pattern="[0-9]{6}" required autofocus>

            <?php if ($erro): ?>
              <div class="alert alert-danger py-2 mb-0"><?= htmlspecialchars($erro) ?></div>
            <?php endif; ?>

            <button type="submit" class="btn btn-primary w-100">Confirmar acesso</button>

            <a href="<?= BASE ?>index.php?page=login&voltar=1" class="btn-voltar authenticator-back">
              <i class="bi bi-arrow-left me-1"></i> Voltar para login
            </a>
          </form>
        </div>

      <?php endif; ?>

      <?php if (!$mostrarTotp): ?>
        <p class="login-cadastro">
          Não tem uma conta? <a href="<?= BASE ?>index.php?page=cadastro">Cadastre-se</a>
        </p>
      <?php endif; ?>

    </div>
  </div>
</main>

<script>
function toggleSenha() {
  const input = document.getElementById('login-senha');
  const icone = document.getElementById('icone-senha');
  if (input.type === 'password') {
    input.type = 'text';
    icone.className = 'bi bi-eye-slash';
  } else {
    input.type = 'password';
    icone.className = 'bi bi-eye';
  }
}

const codigoAuth = document.getElementById('codigo-auth');
if (codigoAuth) {
  codigoAuth.addEventListener('input', () => {
    codigoAuth.value = codigoAuth.value.replace(/\D/g, '').slice(0, 6);
  });
}
</script>
