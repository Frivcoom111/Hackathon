<section class="page">
  <div class="page-title">
    <div>
      <h1>Sobre o portal</h1>
      <p class="muted">Uma ponte entre alunos UniALFA e empresas com oportunidades reais.</p>
    </div>
    <a class="btn btn-primary" href="<?= e(url('/vagas')) ?>"><i data-lucide="search"></i>Ver vagas</a>
  </div>

  <div class="dashboard-hero">
    <div>
      <span class="eyebrow">Portal de Estagios e Empregos</span>
      <h2>Conectando talentos academicos a oportunidades da regiao.</h2>
      <p>O projeto organiza a jornada de busca por vagas, candidatura, acompanhamento de status e gestao de oportunidades por empresas parceiras.</p>
    </div>
    <a class="btn btn-hero" href="<?= e(url('/cadastro/aluno')) ?>"><i data-lucide="user-plus"></i>Criar conta</a>
  </div>

  <div class="stats">
    <article>
      <span>Foco</span>
      <strong>Aluno</strong>
      <p class="muted">Busca por vagas e acompanhamento de candidaturas.</p>
    </article>
    <article>
      <span>Gestao</span>
      <strong>Empresa</strong>
      <p class="muted">Publicacao de vagas e avaliacao de candidatos.</p>
    </article>
    <article>
      <span>Fluxo</span>
      <strong>Status</strong>
      <p class="muted">Candidaturas com etapas visiveis no painel.</p>
    </article>
    <article>
      <span>Base</span>
      <strong>API</strong>
      <p class="muted">Integracao preparada para servicos externos.</p>
    </article>
  </div>

  <section class="page-section two-columns">
    <article class="panel">
      <h2>Objetivo</h2>
      <p>Centralizar oportunidades de estagio e emprego em uma experiencia unica para alunos, empresas e equipe institucional.</p>
      <ul class="clean-list">
        <li>Facilitar o acesso dos alunos a vagas compativeis com sua area.</li>
        <li>Dar visibilidade ao andamento das candidaturas.</li>
        <li>Ajudar empresas a organizar oportunidades e candidatos.</li>
      </ul>
    </article>
    <article class="panel">
      <h2>Funcionalidades</h2>
      <div class="steps">
        <span>Login e cadastro de perfis</span>
        <span>Listagem e detalhe de vagas</span>
        <span>Candidatura de alunos</span>
        <span>Painel para empresas e candidatos</span>
      </div>
    </article>
  </section>

  <section class="page-section two-columns">
    <article class="note">
      <h2>Para alunos</h2>
      <p>O aluno encontra vagas, envia candidatura e acompanha a evolucao pelo painel e pelas notificacoes.</p>
      <a class="link-action" href="<?= e(url('/cadastro/aluno')) ?>">Cadastrar como aluno</a>
    </article>
    <article class="note">
      <h2>Para empresas</h2>
      <p>A empresa cadastra oportunidades, visualiza candidatos e atualiza o status do processo seletivo.</p>
      <a class="link-action" href="<?= e(url('/cadastro/empresa')) ?>">Cadastrar empresa</a>
    </article>
  </section>
</section>
