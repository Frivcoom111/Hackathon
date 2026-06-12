<?php
$companyImages = [
    'company-tech-local' => 'tech-local.svg',
    'company-agencia-alfa' => 'agencia-alfa.svg',
    'company-winfo' => 'winfo.svg',
];
$companyImage = $companyImages[$job->companyId()] ?? 'company-default.svg';
?>

<article class="job-card">
  <div class="job-card-cover">
    <img src="<?= e(asset('img/companies/' . $companyImage)) ?>" alt="<?= e($job->companyName()) ?>">
  </div>
  <div class="job-card-head">
    <strong><?= e($job->companyName()) ?></strong>
    <span class="tag tag-info"><?= e(modality_label($job->modality())) ?></span>
  </div>
  <h3><?= e($job->title()) ?></h3>
  <p class="job-card-meta"><i data-lucide="map-pin"></i><?= e($job->area()) ?> - <?= e($job->location()) ?></p>
  <div class="job-card-foot">
    <span class="tag tag-warning"><?= e(money($job->salary())) ?></span>
    <a class="link-action" href="<?= e(url('/vagas/' . $job->id())) ?>">Ver vaga<i data-lucide="arrow-right"></i></a>
  </div>
</article>
