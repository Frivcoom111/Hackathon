<main class="cadastro-page">
  <div class="cadastro-container">

    <div class="cadastro-imagem">
      <img src="<?= BASE ?>assets/images/site/cadastro.png" alt="Cadastro">
    </div>

    <div class="cadastro-form-box">

      <div class="login-tabs">
        <a href="<?= BASE ?>index.php?page=login" class="login-tab">Entrar</a>
        <a href="<?= BASE ?>index.php?page=cadastro" class="login-tab active">Cadastrar</a>
      </div>

      <h2 class="cadastro-titulo">Crie sua conta</h2>
      <p class="cadastro-sub">Escolha o tipo de conta para continuar</p>

      <div class="cadastro-tabs">
        <button class="cadastro-tab active" onclick="trocarAba('aluno', this)">Aluno</button>
        <button class="cadastro-tab" onclick="trocarAba('empresa', this)">Empresa</button>
      </div>

      <!-- FORMULÁRIO ALUNO -->
      <form id="form-aluno" class="cadastro-form" action="/api/cadastro-aluno" method="POST">
        <div class="row g-3">

          <div class="col-12">
            <label class="form-label">Nome completo</label>
            <input type="text" name="nome" class="form-control" placeholder="Seu nome completo" required>
          </div>

          <div class="col-12">
            <label class="form-label">E-mail</label>
            <input type="email" name="email" class="form-control" placeholder="seu@email.com" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">CPF</label>
            <input type="text" name="cpf" class="form-control" placeholder="000.000.000-00" maxlength="14" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Telefone</label>
            <input type="text" name="phone" class="form-control" placeholder="(44) 99999-9999">
          </div>

          <div class="col-md-6">
            <label class="form-label">RA</label>
            <input type="text" name="ra" class="form-control" placeholder="Registro Acadêmico" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Curso</label>
            <input type="text" name="curso" class="form-control" placeholder="Seu curso" required>
          </div>

          <div class="col-12">
            <label class="form-label">Currículo <span class="text-muted small">(opcional)</span></label>
            <input type="file" name="curriculo" class="form-control" accept=".pdf">
          </div>

          <div class="col-12">
            <label class="form-label">Senha</label>
            <input type="password" name="senha" class="form-control" placeholder="Mínimo 6 caracteres" required>
          </div>

          <div class="col-12">
            <label class="form-label">Confirmar senha</label>
            <input type="password" name="senha_confirmar" class="form-control" placeholder="Repita a senha" required>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Cadastrar como Aluno</button>
          </div>

        </div>
      </form>

      <!-- FORMULÁRIO EMPRESA -->
      <form id="form-empresa" class="cadastro-form" style="display:none;" action="/api/cadastro-empresa" method="POST">
        <div class="row g-3">

          <div class="col-12">
            <label class="form-label">Nome da empresa</label>
            <input type="text" name="nome" class="form-control" placeholder="Razão social" required>
          </div>

          <div class="col-12">
            <label class="form-label">E-mail</label>
            <input type="email" name="email" class="form-control" placeholder="contato@empresa.com" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">CNPJ</label>
            <input type="text" name="cnpj" class="form-control" placeholder="00.000.000/0000-00" maxlength="18" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Telefone</label>
            <input type="text" name="phone" class="form-control" placeholder="(44) 99999-9999">
          </div>

          <div class="col-12">
            <label class="form-label">Descrição <span class="text-muted small">(opcional)</span></label>
            <textarea name="descricao" class="form-control" rows="3" placeholder="Fale sobre sua empresa"></textarea>
          </div>

          <div class="col-12">
            <label class="form-label">Senha</label>
            <input type="password" name="senha" class="form-control" placeholder="Mínimo 6 caracteres" required>
          </div>

          <div class="col-12">
            <label class="form-label">Confirmar senha</label>
            <input type="password" name="senha_confirmar" class="form-control" placeholder="Repita a senha" required>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Cadastrar como Empresa</button>
          </div>

        </div>
      </form>

      <p class="cadastro-login">
        Já tem uma conta? <a href="<?= BASE ?>index.php?page=login">Entrar</a>
      </p>

    </div>
  </div>
</main>

<script>
function trocarAba(tipo, btn) {
  document.querySelectorAll('.cadastro-tab').forEach(t => t.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('form-aluno').style.display   = tipo === 'aluno'   ? 'block' : 'none';
  document.getElementById('form-empresa').style.display = tipo === 'empresa' ? 'block' : 'none';
}
</script>