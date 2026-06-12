<section class="page">
  <div class="page-title">
    <div>
      <h1>Empresas parceiras</h1>
      <p class="muted">Publique vagas, acompanhe candidaturas e encontre talentos da UniALFA.</p>
    </div>
    <a class="btn btn-primary" href="<?= e(url('/cadastro/empresa')) ?>"><i data-lucide="building-2"></i>Cadastrar empresa</a>
  </div>

  <div class="dashboard-hero">
    <div>
      <span class="eyebrow">Para recrutadores</span>
      <h2>Um fluxo simples para divulgar oportunidades e analisar alunos.</h2>
      <p>O portal centraliza cadastro de vagas, candidaturas, status do processo seletivo e comunicacao com candidatos.</p>
    </div>
    <a class="btn btn-hero" href="<?= e(url('/empresa/vagas/nova')) ?>"><i data-lucide="plus-circle"></i>Publicar vaga</a>
  </div>

  <div class="stats">
    <article>
      <span>Cadastro</span>
      <strong>1</strong>
      <p class="muted">Empresa envia dados institucionais para analise.</p>
    </article>
    <article>
      <span>Vagas</span>
      <strong>2</strong>
      <p class="muted">Oportunidades ficam organizadas por area, local e modalidade.</p>
    </article>
    <article>
      <span>Candidatos</span>
      <strong>3</strong>
      <p class="muted">Acompanhe alunos inscritos e historico de status.</p>
    </article>
    <article>
      <span>Contratacao</span>
      <strong>4</strong>
      <p class="muted">Avance candidatos com mais clareza e registro.</p>
    </article>
  </div>

  <section class="page-section two-columns">
    <article class="panel">
      <h2>Recursos para empresas</h2>
      <div class="steps">
        <span>Publicacao e edicao de vagas</span>
        <span>Lista de candidatos por oportunidade</span>
        <span>Alteracao de status da candidatura</span>
        <span>Painel com indicadores rapidos</span>
      </div>
    </article>
    <article class="panel">
      <h2>Boas praticas</h2>
      <ul class="clean-list">
        <li>Descreva atividades, requisitos e bolsa com clareza.</li>
        <li>Informe modalidade e local para alinhar expectativas.</li>
        <li>Mantenha o status atualizado para o aluno acompanhar.</li>
        <li>Use o cadastro institucional antes de publicar vagas.</li>
      </ul>
    </article>
  </section>

  <section class="page-section">
    <div class="section-title">
      <div>
        <h2>Parceiras cadastradas</h2>
        <p class="muted">Empresas presentes no ambiente de demonstracao do portal.</p>
      </div>
      <a class="btn btn-light" href="<?= e(url('/cadastro/empresa')) ?>">Quero participar</a>
    </div>
    <div class="card-grid">
      <?php foreach ($companies as $company): ?>
        <article class="job-card">
          <div class="job-card-head">
            <strong><?= e($company->name()) ?></strong>
            <span class="tag <?= e(status_class($company->status())) ?>"><?= e(status_label($company->status())) ?></span>
          </div>
          <p><?= e($company->description() ?? 'Empresa parceira do portal.') ?></p>
          <p class="muted"><?= e($company->city() ?? 'Umuarama') ?><?= $company->phone() ? ' - ' . e($company->phone()) : '' ?></p>
        </article>
      <?php endforeach; ?>
    </div>
  </section>
</section>
