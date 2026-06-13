<?php
// Carrega a classe Vaga para representar cada vaga retornada pela API
require_once __DIR__ . '/../classes/Vaga.php';

// Chama a API via cURL para buscar as 3 últimas vagas ativas
$ch = curl_init('http://localhost:3000/jobs?limit=3&status=ACTIVE');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); // retorna a resposta em vez de imprimir
$resp = curl_exec($ch);
curl_close($ch);

// Converte o JSON da API em objetos Vaga
$vagas = [];
if ($resp) {
    $data = json_decode($resp, true);
    foreach ($data['jobs'] ?? [] as $item) {
        $vagas[] = new Vaga($item);
    }
}
?>

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

<!-- VAGAS EM DESTAQUE -->
<section class="vagas-section">
  <div class="container">

    <div class="vagas-header">
      <div>
        <h2 class="vagas-titulo">Vagas em destaque</h2>
        <p class="vagas-sub">Oportunidades selecionadas para você</p>
      </div>
      <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Ver todas</a>
    </div>

    <div class="row g-4">

      <?php if (empty($vagas)): ?>
        <!-- Sem vagas: API offline ou nenhuma vaga ativa -->
        <div class="col-12 text-center py-4">
          <i class="bi bi-briefcase fs-2 text-secondary"></i>
          <p class="text-secondary mt-2">Nenhuma vaga disponível no momento.</p>
        </div>

      <?php else: ?>
        <!-- Renderiza um card para cada vaga recebida da API -->
        <?php foreach ($vagas as $vaga): ?>
          <div class="col-lg-4 col-md-6">
            <div class="vaga-card">
              <div class="vaga-card-top">
                <div class="empresa-logo">
                  <div class="empresa-logo-placeholder" style="display:flex;">
                    <i class="bi bi-building"></i>
                  </div>
                </div>
                <span class="vaga-badge">Estágio</span>
              </div>
              <h5 class="vaga-titulo"><?= htmlspecialchars($vaga->getTitulo()) ?></h5>
              <p class="vaga-empresa"><?= htmlspecialchars($vaga->getArea()) ?></p>
              <div class="vaga-infos">
                <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                <span><i class="bi bi-building"></i> <?= htmlspecialchars($vaga->getModalidadeLabel()) ?></span>
              </div>
              <div class="vaga-footer">
                <!-- getSalarioFormatado() retorna "R$ 1.200,00" ou "A combinar" -->
                <span class="vaga-bolsa"><?= $vaga->getSalarioFormatado() ?></span>
                <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">Ver vaga</a>
              </div>
            </div>
          </div>
        <?php endforeach; ?>
      <?php endif; ?>

    </div>
  </div>
</section>
