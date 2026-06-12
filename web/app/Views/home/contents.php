<section class="page">
  <div class="page-title">
    <div>
      <h1>Conteudos</h1>
      <p class="muted">Orientacoes rapidas para alunos e empresas usarem melhor o portal.</p>
    </div>
    <a class="btn btn-light" href="<?= e(url('/faq')) ?>"><i data-lucide="circle-help"></i>Ver FAQ</a>
  </div>

  <div class="card-grid">
    <article class="job-card">
      <div class="job-card-head">
        <strong>Curriculo para estagio</strong>
        <span class="tag tag-info">Aluno</span>
      </div>
      <p>Inclua curso, periodo, habilidades, projetos academicos e disponibilidade de horario.</p>
      <a class="link-action" href="<?= e(url('/cadastro/aluno')) ?>">Criar conta</a>
    </article>
    <article class="job-card">
      <div class="job-card-head">
        <strong>Como se candidatar</strong>
        <span class="tag tag-success">Vagas</span>
      </div>
      <p>Busque oportunidades, leia os requisitos e envie uma mensagem objetiva na candidatura.</p>
      <a class="link-action" href="<?= e(url('/vagas')) ?>">Buscar vagas</a>
    </article>
    <article class="job-card">
      <div class="job-card-head">
        <strong>Divulgacao de vagas</strong>
        <span class="tag tag-warning">Empresa</span>
      </div>
      <p>Cadastre a empresa, informe dados da vaga e acompanhe candidatos pelo painel.</p>
      <a class="link-action" href="<?= e(url('/empresas')) ?>">Ver empresas</a>
    </article>
  </div>

  <section class="page-section two-columns">
    <article class="panel">
      <h2>Trilha do aluno</h2>
      <div class="steps">
        <span>Complete seu cadastro</span>
        <span>Filtre vagas por area e modalidade</span>
        <span>Envie uma candidatura personalizada</span>
        <span>Acompanhe notificacoes e status</span>
      </div>
    </article>
    <article class="panel">
      <h2>Trilha da empresa</h2>
      <div class="steps">
        <span>Cadastre os dados institucionais</span>
        <span>Publique vagas com requisitos claros</span>
        <span>Analise candidatos por oportunidade</span>
        <span>Atualize status do processo seletivo</span>
      </div>
    </article>
  </section>

  <section class="page-section">
    <div class="section-title">
      <div>
        <h2>Materiais essenciais</h2>
        <p class="muted">Conteudos pensados para reduzir duvidas comuns no uso do portal.</p>
      </div>
    </div>
    <div class="list">
      <article class="row-card">
        <div>
          <h3>Preparacao para entrevista</h3>
          <p class="muted">Revise a vaga, organize exemplos de projetos e confirme disponibilidade.</p>
        </div>
        <span class="pill">Aluno</span>
      </article>
      <article class="row-card">
        <div>
          <h3>Descricao de vaga eficiente</h3>
          <p class="muted">Explique atividades, requisitos, modalidade, bolsa e etapa do processo.</p>
        </div>
        <span class="pill">Empresa</span>
      </article>
      <article class="row-card">
        <div>
          <h3>Acompanhamento de candidatura</h3>
          <p class="muted">Use o painel para consultar se a candidatura esta pendente, em analise ou finalizada.</p>
        </div>
        <span class="pill">Portal</span>
      </article>
    </div>
  </section>
</section>
