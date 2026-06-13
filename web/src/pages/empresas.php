<!-- TOPO DA PÁGINA -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Empresas Parceiras</h1>
    <p class="vagas-hero-sub">Conheça as empresas que oferecem oportunidades de estágio</p>
  </div>
</section>

<!-- LISTAGEM DE EMPRESAS -->
<section class="vagas-lista-section">
  <div class="container py-4">

    <!-- Os cards são gerados pelo JavaScript abaixo, conforme a API responde -->
    <div class="row g-4" id="lista-empresas">

      <!-- Spinner: aparece enquanto aguarda a resposta da API -->
      <div class="col-12 text-center py-4" id="loading-empresas">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-secondary mt-2">Carregando empresas...</p>
      </div>

    </div>

    <!-- Aparece se a API retornar lista vazia ou der erro -->
    <div id="sem-empresas" style="display:none;" class="text-center py-5">
      <i class="bi bi-building fs-2 text-secondary"></i>
      <p class="text-secondary mt-2">Nenhuma empresa cadastrada no momento.</p>
    </div>

  </div>
</section>

<script>
// Busca a lista de empresas na API assim que a página carrega
async function carregarEmpresas() {
  const container  = document.getElementById('lista-empresas');
  const loading    = document.getElementById('loading-empresas');
  const semEmpresas = document.getElementById('sem-empresas');

  try {
    // Chama o endpoint da API que retorna as empresas aprovadas
    const res  = await fetch('http://localhost:3000/companies');
    const data = await res.json();

    // Remove o spinner
    loading.remove();

    // Se não vier nenhuma empresa, mostra a mensagem de vazio
    if (!data.companies || data.companies.length === 0) {
      semEmpresas.style.display = 'block';
      return;
    }

    // Para cada empresa, cria e adiciona um card na tela
    data.companies.forEach(empresa => {
      const col = document.createElement('div');
      col.className = 'col-lg-4 col-md-6';
      col.innerHTML = `
        <div class="vaga-card">

          <div class="vaga-card-top">
            <!-- Ícone genérico enquanto não tiver logo da empresa -->
            <div class="empresa-logo">
              <div class="empresa-logo-placeholder" style="display:flex;">
                <i class="bi bi-building fs-4"></i>
              </div>
            </div>
          </div>

          <!-- Nome e descrição da empresa -->
          <h5 class="vaga-titulo">${empresa.name}</h5>
          <p class="vaga-empresa">${empresa.description ?? 'Sem descrição disponível.'}</p>

          <!-- Informações de contato -->
          <div class="vaga-infos">
            <span><i class="bi bi-envelope"></i> ${empresa.email}</span>
            ${empresa.phone ? `<span><i class="bi bi-telephone"></i> ${empresa.phone}</span>` : ''}
          </div>

          <div class="vaga-footer">
            <!-- Leva para a página de vagas filtrando por essa empresa (futuro) -->
            <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">
              Ver vagas
            </a>
          </div>

        </div>
      `;
      container.appendChild(col);
    });

  } catch (erro) {
    // Se a API estiver fora ou der algum erro de rede
    loading.remove();
    semEmpresas.style.display = 'block';
    console.error('Erro ao buscar empresas:', erro);
  }
}

// Dispara quando a página termina de carregar
carregarEmpresas();
</script>
