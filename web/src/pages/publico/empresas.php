<?php
// Carrega a classe Empresa para representar cada empresa retornada pela API
require_once __DIR__ . '/../../classes/Empresa.php';

// Chama a API via cURL para buscar as empresas aprovadas
$ch = curl_init(API_URL . '/companies');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); // retorna a resposta em vez de imprimir
$resp = curl_exec($ch);

// Converte o JSON da API em objetos Empresa
$empresas = [];
if ($resp) {
    $data = json_decode($resp, true);
    foreach ($data['data'] ?? [] as $item) {
        $empresas[] = new Empresa($item);
    }
}
?>

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
    <div class="row g-4">

      <?php if (empty($empresas)): ?>
        <!-- Sem empresas: API offline ou nenhuma empresa aprovada -->
        <div class="col-12 text-center py-5">
          <i class="bi bi-building fs-2 text-secondary"></i>
          <p class="text-secondary mt-2">Nenhuma empresa cadastrada no momento.</p>
        </div>

      <?php else: ?>
        <!-- Renderiza um card para cada empresa recebida da API -->
        <?php foreach ($empresas as $empresa): ?>
          <div class="col-lg-4 col-md-6">
            <div class="vaga-card">

              <div class="vaga-card-top">
                <div class="empresa-logo">
                  <div class="empresa-logo-placeholder" style="display:flex;">
                    <i class="bi bi-building fs-4"></i>
                  </div>
                </div>
              </div>

              <h5 class="vaga-titulo"><?= htmlspecialchars($empresa->getNome()) ?></h5>
              <!-- getCnpjFormatado() retorna o CNPJ no padrão 00.000.000/0000-00 -->
              <p class="vaga-empresa"><?= $empresa->getCnpjFormatado() ?></p>

              <div class="vaga-infos">
                <?php if ($empresa->getDescricao()): ?>
                  <span><i class="bi bi-info-circle"></i> <?= htmlspecialchars($empresa->getDescricao()) ?></span>
                <?php endif; ?>
                <?php if ($empresa->getTelefone()): ?>
                  <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($empresa->getTelefone()) ?></span>
                <?php endif; ?>
              </div>

              <div class="vaga-footer">
                <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">Ver vagas</a>
              </div>

            </div>
          </div>
        <?php endforeach; ?>
      <?php endif; ?>

    </div>
  </div>
</section>
