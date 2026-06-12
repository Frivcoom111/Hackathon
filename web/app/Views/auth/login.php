<section class="login-reference">
  <aside class="login-reference-poster" aria-label="Arte institucional Portal de Estagios UniALFA">
    <img src="<?= e(asset('img/unialfa-portal-estagios.jpeg')) ?>" alt="Portal de Estagios e Empregos UniALFA">
  </aside>

  <section class="login-reference-panel">
    <div class="login-welcome">
      <div class="login-avatar"><i data-lucide="user-round"></i></div>
      <h1><span>Seja</span> Bem-vindo</h1>
      <p>Acesse sua conta para<br>oportunidades de emprego incr&iacute;veis</p>
    </div>

    <form class="login-reference-card" method="post" action="<?= e(url('/login')) ?>">
      <?= csrf_field() ?>
      <h2>Fa&ccedil;a seu login</h2>

      <div class="login-profile-switch" role="group" aria-label="Tipo de acesso">
        <button class="active" type="button" data-login-profile="student"><i data-lucide="graduation-cap"></i>Aluno</button>
        <button type="button" data-login-profile="company"><i data-lucide="building-2"></i>Empresa</button>
      </div>

      <label>
        <span>E-mail</span>
        <span class="login-input-wrap">
          <i data-lucide="mail"></i>
          <input id="login-email" type="email" name="email" autocomplete="email" placeholder="email@email.com" required>
        </span>
      </label>

      <label>
        <span class="login-label-row">
          Senha
          <a href="<?= e(url('/faq')) ?>">Esqueci minha senha</a>
        </span>
        <span class="login-input-wrap">
          <i data-lucide="lock-keyhole"></i>
          <input id="login-password" type="password" name="password" autocomplete="current-password" placeholder="********" required>
          <button class="login-icon-action" type="button" data-toggle-password aria-label="Mostrar senha">
            <i data-lucide="eye"></i>
          </button>
        </span>
      </label>

      <p class="login-caps-warning" data-caps-warning>Caps Lock ativado.</p>

      <label class="login-remember">
        <input id="remember-email" type="checkbox" value="1">
        <span>Lembrar meu e-mail neste dispositivo</span>
      </label>

      <button class="login-reference-submit" type="submit">
        <i data-lucide="log-in"></i>
        Entrar
      </button>

      <div class="login-secondary-actions">
        <a href="<?= e(url('/vagas')) ?>"><i data-lucide="search"></i>Ver vagas</a>
        <a href="<?= e(url('/cadastro/aluno')) ?>"><i data-lucide="user-plus"></i>Criar conta aluno</a>
        <a href="<?= e(url('/cadastro/empresa')) ?>"><i data-lucide="briefcase-business"></i>Cadastrar empresa</a>
      </div>

      <p class="login-security-note"><i data-lucide="lock"></i>Acesso seguro com sessao protegida para alunos e empresas.</p>
    </form>
  </section>
</section>

<script>
  document.addEventListener('DOMContentLoaded', function () {
    var form = document.querySelector('.login-reference-card');
    var email = document.getElementById('login-email');
    var password = document.getElementById('login-password');
    var remember = document.getElementById('remember-email');
    var capsWarning = document.querySelector('[data-caps-warning]');
    var togglePassword = document.querySelector('[data-toggle-password]');
    var profileButtons = document.querySelectorAll('[data-login-profile]');
    var rememberedEmail = localStorage.getItem('portalLoginEmail');

    if (rememberedEmail && email && remember) {
      email.value = rememberedEmail;
      remember.checked = true;
    }

    profileButtons.forEach(function (button) {
      button.addEventListener('click', function () {
        profileButtons.forEach(function (item) { item.classList.remove('active'); });
        button.classList.add('active');
        if (email) {
          email.placeholder = button.dataset.loginProfile === 'company' ? 'empresa@email.com' : 'email@email.com';
          email.focus();
        }
      });
    });

    if (togglePassword && password) {
      togglePassword.addEventListener('click', function () {
        var visible = password.type === 'text';
        password.type = visible ? 'password' : 'text';
        togglePassword.setAttribute('aria-label', visible ? 'Mostrar senha' : 'Ocultar senha');
        togglePassword.innerHTML = visible ? '<i data-lucide="eye"></i>' : '<i data-lucide="eye-off"></i>';
        if (window.lucide) window.lucide.createIcons();
      });
    }

    if (password && capsWarning) {
      password.addEventListener('keyup', function (event) {
        capsWarning.classList.toggle('visible', event.getModifierState && event.getModifierState('CapsLock'));
      });
    }

    if (form) {
      form.addEventListener('submit', function () {
        if (remember && email) {
          if (remember.checked) {
            localStorage.setItem('portalLoginEmail', email.value);
          } else {
            localStorage.removeItem('portalLoginEmail');
          }
        }

        form.classList.add('is-loading');
      });
    }
  });
</script>

