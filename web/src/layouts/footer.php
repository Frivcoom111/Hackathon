<?php $semFooter = in_array($pagina ?? '', ['login', 'cadastro'], true); ?>

<?php if (!$semFooter): ?>
<footer>
  <div class="container">

    <div class="footer-inner">

      <!-- Logo — usa BASE para montar o caminho certo independente de onde o PHP está rodando -->
      <a href="<?= BASE ?>index.php" class="footer-brand">
        <img src="<?= BASE ?>assets/images/site/logo.png" alt="Portal UniALFA"
             onerror="this.style.display='none'; this.nextElementSibling.style.display='inline'">
        <span style="display:none;">Portal UniALFA</span>
      </a>

      <?php if (empty($paginaAuth)): ?>
      <!-- Links de navegação — espelha o menu do header (ocultos nas telas de login/cadastro) -->
      <ul class="footer-links">
        <li><a href="<?= BASE ?>index.php?page=home">Início</a></li>
        <li><a href="<?= BASE ?>index.php?page=vagas">Vagas</a></li>
      </ul>
      <?php endif; ?>

      <!-- Ano gerado automaticamente pelo PHP -->
      <p class="footer-copy">
        &copy; <?= date('Y') ?> Portal de Estágios UniALFA. Todos os direitos reservados.
      </p>

    </div>

  </div>
</footer>
<?php endif; ?>

<!-- Bootstrap JS (local) — carrega o JS do Bootstrap para modais, menu hamburguer etc. -->
<script src="<?= BASE ?>assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

</body>
</html>
