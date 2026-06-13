<main class="login-page">
  <div class="login-container">

    <!-- Lado esquerdo — imagem -->
    <div class="login-imagem">
      <img src="<?= BASE ?>assets/images/site/login.png" alt="Login">
    </div>

    <!-- Lado direito — formulário -->
    <div class="login-form-box">

      <!-- Abas -->
      <div class="login-tabs">
        <a href="<?= BASE ?>index.php?page=login" class="login-tab active">Entrar</a>
        <a href="<?= BASE ?>index.php?page=cadastro" class="login-tab">Cadastrar</a>
      </div>

      <!-- TELA 1: Login -->
      <div id="tela-login">
        <h2 class="login-titulo">Logar sua conta</h2>
        <p class="login-sub">Bem-vindo de volta!</p>

        <form id="form-login" class="row g-3">

          <div class="col-12">
            <label class="form-label">E-mail</label>
            <input type="email" id="login-email" name="email" class="form-control" placeholder="seu@email.com" required>
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

          <div id="login-erro" class="col-12" style="display:none;">
            <div class="alert alert-danger py-2 mb-0" role="alert" id="login-erro-msg"></div>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100" id="btn-login">
              Entrar
            </button>
          </div>

        </form>
      </div>

      <!-- TELA 2: Token de confirmação (empresa) -->
      <div id="tela-token" style="display:none;">
        <h2 class="login-titulo">Confirme seu acesso</h2>
        <p class="login-sub">Insira o código de confirmação enviado para seu e-mail.</p>

        <form id="form-token" class="row g-3">

          <div class="col-12">
            <label class="form-label">Código de confirmação</label>
            <input type="text" id="token-codigo" name="token" class="form-control input-token"
                   placeholder="000000" maxlength="6" required>
          </div>

          <div id="token-erro" class="col-12" style="display:none;">
            <div class="alert alert-danger py-2 mb-0" id="token-erro-msg"></div>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Confirmar</button>
          </div>

          <div class="col-12 text-center">
            <button type="button" class="btn-voltar" onclick="voltarLogin()">
              <i class="bi bi-arrow-left me-1"></i> Voltar
            </button>
          </div>

        </form>
      </div>

      <p class="login-cadastro">
        Não tem uma conta? <a href="<?= BASE ?>index.php?page=cadastro">Cadastre-se</a>
      </p>

    </div>
  </div>
</main>

<script>
// Toggle senha
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

// Voltar para login
function voltarLogin() {
  document.getElementById('tela-token').style.display = 'none';
  document.getElementById('tela-login').style.display = 'block';
}

// Guarda o token temporário do login (necessário para enviar no TOTP)
let tokenTemporario = null;

// Submit login
document.getElementById('form-login').addEventListener('submit', async function(e) {
  e.preventDefault();

  const btn     = document.getElementById('btn-login');
  const erro    = document.getElementById('login-erro');
  const erroMsg = document.getElementById('login-erro-msg');

  btn.disabled = true;
  btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Entrando...';
  erro.style.display = 'none';

  try {
    const res = await fetch('http://localhost:3000/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email:    document.getElementById('login-email').value,
        password: document.getElementById('login-senha').value,
      })
    });

    const data = await res.json();

    if (!res.ok) {
      // A API retorna erros no formato { message: "..." }
      throw new Error(data.message || 'Credenciais inválidas.');
    }

    // A API retorna os dados dentro de data.data (ex: data.data.token, data.data.user.role)
    const usuario = data.data.user;
    const token   = data.data.token;

    // COMPANY e ADMIN precisam confirmar o código TOTP antes de entrar
    if (usuario.role === 'COMPANY' || usuario.role === 'ADMIN') {
      // Salva o token temporário para usar na verificação do TOTP
      tokenTemporario = token;
      document.getElementById('tela-login').style.display = 'none';
      document.getElementById('tela-token').style.display = 'block';
      return;
    }

    // STUDENT entra direto sem TOTP
    localStorage.setItem('token', token);
    localStorage.setItem('role', usuario.role);
    window.location.href = '<?= BASE ?>index.php?page=home';

  } catch (err) {
    erroMsg.textContent = err.message;
    erro.style.display = 'block';
  } finally {
    btn.disabled = false;
    btn.innerHTML = 'Entrar';
  }
});

// Submit TOTP — confirma o código de 6 dígitos para ADMIN e COMPANY
document.getElementById('form-token').addEventListener('submit', async function(e) {
  e.preventDefault();

  const erro    = document.getElementById('token-erro');
  const erroMsg = document.getElementById('token-erro-msg');
  erro.style.display = 'none';

  try {
    // Envia o token temporário no header e o código TOTP no body
    const res = await fetch('http://localhost:3000/auth/totp/verify', {
      method: 'POST',
      headers: {
        'Content-Type':  'application/json',
        'Authorization': `Bearer ${tokenTemporario}`,
      },
      body: JSON.stringify({
        code: document.getElementById('token-codigo').value,
      })
    });

    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || 'Código inválido.');
    }

    // Substitui o token temporário pelo token final (com TOTP verificado)
    localStorage.setItem('token', data.data.token);
    window.location.href = '<?= BASE ?>index.php?page=home';

  } catch (err) {
    erroMsg.textContent = err.message;
    erro.style.display = 'block';
  }
});
</script>