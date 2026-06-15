<?php
require_once __DIR__ . '/../api.php';

$erro = '';
$mostrarTotp = false;
$qrCode = '';
$totpEmail = $_SESSION['totp_email'] ?? '';
$totpTipoAtual = $_SESSION['totp_tipo'] ?? '';
$totpSessionKeys = ['tempToken', 'totp_tipo', 'qrCode', 'totp_email'];

function limpar_totp(array $keys): void
{
    foreach ($keys as $key) unset($_SESSION[$key]);
}

function redirecionar(string $pagina): never
{
    header('Location: ' . BASE . 'index.php?page=' . $pagina);
    exit;
}

function preparar_totp(array $data, string $modo, string $emailFallback): array
{
    $_SESSION['tempToken'] = $data['data']['tempToken'] ?? '';
    $_SESSION['totp_tipo'] = $modo;
    $_SESSION['totp_email'] = $data['data']['user']['email'] ?? $emailFallback;
    $setup = api_get('/auth/totp/setup', $_SESSION['tempToken']);
    $_SESSION['qrCode'] = $setup['data']['qrCode'] ?? '';

    return ['mostrar' => true, 'qrCode' => $_SESSION['qrCode'], 'email' => $_SESSION['totp_email'], 'tipo' => $modo];
}

$sucesso = $_SESSION['msg_sucesso'] ?? '';
unset($_SESSION['msg_sucesso']);

if (isset($_GET['voltar'])) {
    limpar_totp($totpSessionKeys);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (($_POST['acao'] ?? 'login') === 'login') {
        $email = trim($_POST['email'] ?? '');
        $data = api_post_json('/auth/login', [
            'email' => $email,
            'password' => $_POST['senha'] ?? '',
        ]);

        if (!($data['success'] ?? false)) {
            $erro = $data['message'] ?? 'Credenciais invalidas.';
        } else {
            $tipo = $data['data']['type'] ?? '';

            if ($tipo === 'AUTHENTICATED') {
                $_SESSION['token'] = $data['data']['token'];
                $_SESSION['role'] = strtoupper($data['data']['user']['role'] ?? 'STUDENT') === 'COMPANY' ? 'empresa' : 'aluno';
                redirecionar($_SESSION['role'] === 'empresa' ? 'empresa-dashboard' : 'home');
            }

            if (in_array($tipo, ['TOTP_SETUP', 'TOTP_REQUIRED'], true)) {
                $totp = preparar_totp($data, $tipo === 'TOTP_SETUP' ? 'setup' : 'verify', $email);
                $mostrarTotp = $totp['mostrar'];
                $qrCode = $totp['qrCode'];
                $totpEmail = $totp['email'];
                $totpTipoAtual = $totp['tipo'];
            }
        }
    }

    if (($_POST['acao'] ?? '') === 'totp') {
        $tempToken = $_SESSION['tempToken'] ?? '';
        $totpTipo = $_SESSION['totp_tipo'] ?? 'verify';
        $rota = $totpTipo === 'setup' ? '/auth/totp/setup/confirm' : '/auth/totp/verify';

        $data = api_post_json($rota, [
            'code' => trim($_POST['codigo'] ?? ''),
        ], $tempToken);

        if (!($data['success'] ?? false)) {
            $erro = $data['message'] ?? 'Codigo invalido.';
            $mostrarTotp = true;
            $qrCode = $_SESSION['qrCode'] ?? '';
            $totpEmail = $_SESSION['totp_email'] ?? '';
            $totpTipoAtual = $_SESSION['totp_tipo'] ?? '';
        } else {
            $_SESSION['token'] = $data['data']['token'];
            $_SESSION['role'] = 'empresa';
            limpar_totp($totpSessionKeys);
            redirecionar('empresa-dashboard');
        }
    }
}
?>

<main class="login-page">
  <div class="login-container <?= $mostrarTotp ? 'authenticator-container' : '' ?>">
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
        <h2 class="login-titulo">Logar sua conta</h2>
        <p class="login-sub">Bem-vindo de volta!</p>

        <form method="POST" action="<?= BASE ?>index.php?page=login" class="row g-3">
          <input type="hidden" name="acao" value="login">

          <div class="col-12">
            <label class="form-label">E-mail</label>
            <input type="email" name="email" class="form-control" placeholder="seu@email.com" required>
          </div>

          <div class="col-12">
            <label class="form-label">Senha</label>
            <div class="input-senha">
              <input type="password" id="login-senha" name="senha" class="form-control" placeholder="Sua senha" required>
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
              Escaneie o QR Code ou use o codigo atual do seu aplicativo autenticador.
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
          <?php if ($qrCode): ?>
            <div class="authenticator-qr-card">
              <span class="authenticator-step">1</span>
              <img src="<?= htmlspecialchars($qrCode) ?>" alt="QR Code do Authenticator">
              <p>Escaneie no Google Authenticator, Microsoft Authenticator ou app similar.</p>
            </div>
          <?php else: ?>
            <div class="authenticator-qr-card authenticator-verify-card">
              <span class="authenticator-step">1</span>
              <div class="authenticator-lock">
                <i class="bi bi-phone-lock"></i>
              </div>
              <p>Use o codigo que aparece no aplicativo autenticador ja configurado.</p>
            </div>
          <?php endif; ?>

          <form method="POST" action="<?= BASE ?>index.php?page=login" class="authenticator-code-card">
            <input type="hidden" name="acao" value="totp">
            <span class="authenticator-step">2</span>

            <label class="form-label" for="codigo-auth">Codigo de 6 digitos</label>
            <input type="text" name="codigo" id="codigo-auth" class="form-control input-token"
                   placeholder="000000" maxlength="6" inputmode="numeric" autocomplete="one-time-code"
                   pattern="[0-9]{6}" required autofocus>

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
          Nao tem uma conta? <a href="<?= BASE ?>index.php?page=cadastro">Cadastre-se</a>
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
