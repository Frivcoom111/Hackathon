<section class="page">
  <div class="page-title">
    <h1>Cadastro de aluno</h1>
    <span class="pill">Aluno</span>
  </div>
  <form class="panel form-grid" method="post" action="<?= e(url('/cadastro/aluno')) ?>">
    <?= csrf_field() ?>
    <h2>Dados pessoais</h2>
    <label>Nome<input type="text" name="name" required></label>
    <label>Email<input type="email" name="email" required></label>
    <label>Senha<input type="password" name="password" required></label>
    <label>RA<input type="text" name="ra" required></label>
    <label>CPF<input type="text" name="cpf" required></label>
    <label>Telefone<input type="text" name="phone"></label>
    <label>ID do curso<input type="text" name="courseId" value="<?= e(app_config('default_course_id', '')) ?>" required></label>
    <label>Inicio do curso<input type="date" name="startedAt" value="<?= e(date('Y-m-d')) ?>" required></label>
    <input type="hidden" name="status" value="ACTIVE">
    <div class="form-actions">
      <button class="btn btn-primary" type="submit"><i data-lucide="save"></i>Cadastrar</button>
      <a class="btn btn-light" href="<?= e(url('/login')) ?>">Cancelar</a>
    </div>
  </form>
</section>
