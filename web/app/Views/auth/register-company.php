<section class="page">
  <div class="page-title">
    <h1>Cadastro de empresa</h1>
    <span class="pill">Empresa</span>
  </div>
  <form class="panel form-grid" method="post" action="<?= e(url('/cadastro/empresa')) ?>">
    <?= csrf_field() ?>
    <h2>Dados da empresa</h2>
    <label>Razao social<input type="text" name="name" required></label>
    <label>CNPJ<input type="text" name="cnpj" required></label>
    <label>Email<input type="email" name="email" required></label>
    <label>Senha<input type="password" name="password" required></label>
    <label>Telefone<input type="text" name="phone" required></label>
    <label class="span-all">Descricao<textarea name="description" rows="4"></textarea></label>

    <h2>Endereco</h2>
    <label>Rua<input type="text" name="address[street]" required></label>
    <label>Numero<input type="text" name="address[number]" required></label>
    <label>Bairro<input type="text" name="address[district]" required></label>
    <label>Cidade<input type="text" name="address[city]" required></label>
    <label>UF<input type="text" name="address[state]" maxlength="2" required></label>
    <label>CEP<input type="text" name="address[zipCode]" required></label>
    <label class="span-all">Complemento<input type="text" name="address[complement]"></label>

    <h2>Responsavel</h2>
    <label>Nome<input type="text" name="member[name]" required></label>
    <label>CPF<input type="text" name="member[cpf]" required></label>
    <label>Telefone<input type="text" name="member[phone]"></label>
    <div class="form-actions">
      <button class="btn btn-primary" type="submit"><i data-lucide="send"></i>Enviar cadastro</button>
      <a class="btn btn-light" href="<?= e(url('/login')) ?>">Cancelar</a>
    </div>
  </form>
</section>
