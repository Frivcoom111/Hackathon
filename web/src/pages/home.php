<!-- BANNER -->
<section class="banner-section">
  <div class="container">
    <div class="banner-box">
      <img src="assets/images/site/banner.png" alt="Banner"
           onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
      <!-- Aparece só se a imagem do banner não carregar -->
      <div class="banner-placeholder" style="display:none;">
        <span>Banner</span>
      </div>
    </div>
  </div>
</section>

<!-- VAGAS EM DESTAQUE -->
<section class="vagas-section">
  <div class="container">

    <div class="vagas-header">
      <div>
        <h2 class="vagas-titulo">Vagas em destaque</h2>
        <p class="vagas-sub">Oportunidades selecionadas para você</p>
      </div>
      <!-- Redireciona para a página de vagas via roteador central -->
      <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Ver todas</a>
    </div>

    <!-- Os cards de vagas vão ser carregados aqui via JavaScript, buscando da API -->
    <div class="row g-4" id="vagas-destaque">
      <!-- Estado de carregamento: aparece enquanto a API não responde -->
      <div class="col-12 text-center py-4" id="loading-vagas">
        <div class="spinner-border text-primary" role="status"></div>
        <p class="text-secondary mt-2">Carregando vagas...</p>
      </div>
    </div>

    <!-- Aparece se a API não retornar nenhuma vaga -->
    <div id="sem-vagas-home" style="display:none;" class="text-center py-4">
      <i class="bi bi-briefcase fs-2 text-secondary"></i>
      <p class="text-secondary mt-2">Nenhuma vaga disponível no momento.</p>
    </div>

  </div>
</section>

<script>
// Busca as vagas na API Node.js assim que a página carrega
async function carregarVagasDestaque() {
  const container = document.getElementById('vagas-destaque');
  const loading   = document.getElementById('loading-vagas');
  const semVagas  = document.getElementById('sem-vagas-home');

  try {
    // Chama o endpoint da API que retorna as vagas ativas
    const res  = await fetch('http://localhost:3000/jobs?limit=3&status=ACTIVE');
    const data = await res.json();

    // Remove o spinner de carregamento
    loading.remove();

    // Se não vier nenhuma vaga, mostra a mensagem de "sem vagas"
    if (!data.jobs || data.jobs.length === 0) {
      semVagas.style.display = 'block';
      return;
    }

    // Para cada vaga retornada, cria um card e adiciona na tela
    data.jobs.forEach(vaga => {
      const col = document.createElement('div');
      col.className = 'col-lg-4 col-md-6';
      col.innerHTML = `
        <div class="vaga-card">
          <div class="vaga-card-top">
            <!-- Ícone genérico de empresa (sem logo por enquanto) -->
            <div class="empresa-logo">
              <div class="empresa-logo-placeholder" style="display:flex;">
                <i class="bi bi-building"></i>
              </div>
            </div>
            <span class="vaga-badge">Estágio</span>
          </div>
          <h5 class="vaga-titulo">${vaga.title}</h5>
          <p class="vaga-empresa">${vaga.company?.name ?? 'Empresa'}</p>
          <div class="vaga-infos">
            <span><i class="bi bi-geo-alt"></i> ${vaga.location}</span>
            <span><i class="bi bi-laptop"></i> ${vaga.area}</span>
          </div>
          <div class="vaga-footer">
            <!-- Se não tiver salário, mostra "A combinar" -->
            <span class="vaga-bolsa">${vaga.salary ? 'R$ ' + Number(vaga.salary).toFixed(2).replace('.', ',') : 'A combinar'}</span>
            <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">Ver vaga</a>
          </div>
        </div>
      `;
      container.appendChild(col);
    });

  } catch (erro) {
    // Se a API estiver offline ou der erro, remove o spinner e mostra "sem vagas"
    loading.remove();
    semVagas.style.display = 'block';
    console.error('Erro ao buscar vagas:', erro);
  }
}

// Chama a função quando a página termina de carregar
carregarVagasDestaque();
</script>
