<?php
// Todo o fluxo de login é feito aqui no PHP via cURL — sem JS chamando a API

$erro        = '';
$mostrarTotp = false;
$qrCode      = '';

// Exibe mensagem deixada pelo cadastro (ex: "Cadastro realizado!")
if (!empty($_SESSION['msg_sucesso'])) {
    $sucesso = $_SESSION['msg_sucesso'];
    unset($_SESSION['msg_sucesso']);
} else {
    $sucesso = '';
}

// Limpa estado TOTP se o usuário clicou em "Voltar"
if (isset($_GET['voltar'])) {
    unset($_SESSION['tempToken'], $_SESSION['totp_tipo'], $_SESSION['qrCode']);
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? 'login';

    // ── Etapa 1: e-mail e senha ───────────────────────────────────────────────
    if ($acao === 'login') {
        $ch = curl_init('http://localhost:3000/auth/login');
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode([
            'email'    => trim($_POST['email'] ?? ''),
            'password' => $_POST['senha'] ?? '',
        ]));
        $resp = curl_exec($ch);
        curl_close($ch);

        $data = json_decode($resp, true);

        if (!($data['success'] ?? false)) {
            $erro = $data['message'] ?? 'Credenciais inválidas.';
        } else {
            $tipo = $data['data']['type'] ?? '';

            if ($tipo === 'AUTHENTICATED') {
                // Aluno — salva token na session e vai para a home
                $_SESSION['token'] = $data['data']['token'];
                header('Location: ' . BASE . 'index.php?page=home');
                exit;

            } elseif ($tipo === 'TOTP_SETUP') {
                // Empresa sem TOTP — precisa escanear o QR code pela primeira vez
                $_SESSION['tempToken'] = $data['data']['tempToken'];
                $_SESSION['totp_tipo'] = 'setup';

                $ch2 = curl_init('http://localhost:3000/auth/totp/setup');
                curl_setopt($ch2, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch2, CURLOPT_HTTPHEADER, [
                    'Authorization: Bearer ' . $data['data']['tempToken'],
                ]);
                $resp2 = curl_exec($ch2);
                curl_close($ch2);

                $data2  = json_decode($resp2, true);
                $qrCode = $data2['data']['qrCode'] ?? '';
                $_SESSION['qrCode'] = $qrCode;
                $mostrarTotp = true;

            } elseif ($tipo === 'TOTP_REQUIRED') {
                // Empresa com TOTP já configurado — só digita o código
                $_SESSION['tempToken'] = $data['data']['tempToken'];
                $_SESSION['totp_tipo'] = 'verify';
                $mostrarTotp = true;
            }
        }

    // ── Etapa 2: código TOTP ─────────────────────────────────────────────────
    } elseif ($acao === 'totp') {
        $tempToken = $_SESSION['tempToken'] ?? '';
        $totpTipo  = $_SESSION['totp_tipo'] ?? 'verify';

        $rota = $totpTipo === 'setup'
            ? 'http://localhost:3000/auth/totp/setup/confirm'
            : 'http://localhost:3000/auth/totp/verify';

        $ch = curl_init($rota);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json',
            'Authorization: Bearer ' . $tempToken,
        ]);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode([
            'code' => trim($_POST['codigo'] ?? ''),
        ]));
        $resp = curl_exec($ch);
        curl_close($ch);

        $data = json_decode($resp, true);

        if (!($data['success'] ?? false)) {
            $erro        = $data['message'] ?? 'Código inválido.';
            $mostrarTotp = true;
            $qrCode      = $_SESSION['qrCode'] ?? '';
        } else {
            $_SESSION['token'] = $data['data']['token'];
            unset($_SESSION['tempToken'], $_SESSION['totp_tipo'], $_SESSION['qrCode']);
            header('Location: ' . BASE . 'index.php?page=home');
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

    <div class="login-form-box">

      <div class="login-tabs">
        <a href="<?= BASE ?>index.php?page=login" class="login-tab active">Entrar</a>
        <a href="<?= BASE ?>index.php?page=cadastro" class="login-tab">Cadastrar</a>
      </div>

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

        <!-- Tela 2: verificação TOTP (apenas empresas) -->
        <h2 class="login-titulo">Confirme seu acesso</h2>

        <?php if ($qrCode): ?>
          <p class="login-sub">Escaneie o QR code com Google Authenticator e insira o código gerado.</p>
          <div class="text-center my-3">
            <img src="<?= htmlspecialchars($qrCode) ?>" alt="QR Code" style="max-width:200px; border-radius:8px;">
          </div>
        <?php else: ?>
          <p class="login-sub">Insira o código de 6 dígitos do seu aplicativo autenticador.</p>
        <?php endif; ?>

        <form method="POST" action="<?= BASE ?>index.php?page=login" class="row g-3">
          <input type="hidden" name="acao" value="totp">

          <div class="col-12">
            <label class="form-label">Código de confirmação</label>
            <input type="text" name="codigo" class="form-control input-token"
                   placeholder="000000" maxlength="6" inputmode="numeric" required>
          </div>

          <?php if ($erro): ?>
            <div class="col-12">
              <div class="alert alert-danger py-2 mb-0"><?= htmlspecialchars($erro) ?></div>
            </div>
          <?php endif; ?>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Confirmar</button>
          </div>

          <div class="col-12 text-center">
            <a href="<?= BASE ?>index.php?page=login&voltar=1" class="btn-voltar">
              <i class="bi bi-arrow-left me-1"></i> Voltar
            </a>
          </div>
        </form>

      <?php endif; ?>

      <p class="login-cadastro">
        Não tem uma conta? <a href="<?= BASE ?>index.php?page=cadastro">Cadastre-se</a>
      </p>

    </div>
  </div>
</main>

<script>
// Apenas toggle de visibilidade da senha — sem chamada à API
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
</script>
