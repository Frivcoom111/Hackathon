<!-- BANNER -->
<section class="banner-section">
  <div class="container">
    <div class="banner-box">
      <img src="assets/images/site/banner.png" alt="Banner"
           onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
      <div class="banner-placeholder" style="display:none;">
        <span>Banner</span>
      </div>
    </div>
  </div>
</section>

<!-- VAGAS -->
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

    <div class="row g-4">

      <!-- Card 1 -->
      <div class="col-lg-4 col-md-6">
        <div class="vaga-card">
          <div class="vaga-card-top">
            <div class="empresa-logo">
              <img src="assets/images/empresas/empresa1.png" alt="Empresa"
                   onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
              <div class="empresa-logo-placeholder" style="display:none;"><i class="bi bi-building"></i></div>
            </div>
            <span class="vaga-badge">Estágio</span>
          </div>
          <h5 class="vaga-titulo">Desenvolvedor Front-end</h5>
          <p class="vaga-empresa">Tech Solutions Ltda.</p>
          <div class="vaga-infos">
            <span><i class="bi bi-geo-alt"></i> Umuarama, PR</span>
            <span><i class="bi bi-clock"></i> Período parcial</span>
          </div>
          <div class="vaga-footer">
            <span class="vaga-bolsa">R$ 800,00</span>
            <a href="#" class="btn btn-primary btn-sm">Ver vaga</a>
          </div>
        </div>
      </div>

      <!-- Card 2 -->
      <div class="col-lg-4 col-md-6">
        <div class="vaga-card">
          <div class="vaga-card-top">
            <div class="empresa-logo">
              <img src="assets/images/empresas/empresa2.png" alt="Empresa"
                   onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
              <div class="empresa-logo-placeholder" style="display:none;"><i class="bi bi-building"></i></div>
            </div>
            <span class="vaga-badge vaga-badge-amarelo">Trainee</span>
          </div>
          <h5 class="vaga-titulo">Analista de Marketing</h5>
          <p class="vaga-empresa">Agência Criativa S/A</p>
          <div class="vaga-infos">
            <span><i class="bi bi-geo-alt"></i> Maringá, PR</span>
            <span><i class="bi bi-clock"></i> Período integral</span>
          </div>
          <div class="vaga-footer">
            <span class="vaga-bolsa">R$ 1.200,00</span>
            <a href="#" class="btn btn-primary btn-sm">Ver vaga</a>
          </div>
        </div>
      </div>

      <!-- Card 3 -->
      <div class="col-lg-4 col-md-6">
        <div class="vaga-card">
          <div class="vaga-card-top">
            <div class="empresa-logo">
              <img src="assets/images/empresas/empresa3.png" alt="Empresa"
                   onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
              <div class="empresa-logo-placeholder" style="display:none;"><i class="bi bi-building"></i></div>
            </div>
            <span class="vaga-badge">Estágio</span>
          </div>
          <h5 class="vaga-titulo">Assistente Administrativo</h5>
          <p class="vaga-empresa">Grupo Empresarial Norte</p>
          <div class="vaga-infos">
            <span><i class="bi bi-geo-alt"></i> Cascavel, PR</span>
            <span><i class="bi bi-clock"></i> Período parcial</span>
          </div>
          <div class="vaga-footer">
            <span class="vaga-bolsa">R$ 750,00</span>
            <a href="#" class="btn btn-primary btn-sm">Ver vaga</a>
          </div>
        </div>
      </div>

    </div>
  </div>
</section>
