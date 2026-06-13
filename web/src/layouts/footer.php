<footer>
  <div class="container">

    <div class="footer-inner">

      <!-- Logo — usa BASE para montar o caminho certo independente de onde o PHP está rodando -->
      <a href="<?= BASE ?>index.php" class="footer-brand">
        <img src="<?= BASE ?>assets/images/site/logo.png" alt="Portal UniALFA" height="28"
             onerror="this.style.display='none'; this.nextElementSibling.style.display='inline'">
        <span style="display:none;">Portal UniALFA</span>
      </a>

      <!-- Links de navegação usando o roteador central (index.php?page=...) -->
      <ul class="footer-links">
        <li><a href="<?= BASE ?>index.php?page=home">Início</a></li>
        <li><a href="<?= BASE ?>index.php?page=vagas">Vagas</a></li>
        <li><a href="<?= BASE ?>index.php?page=empresas">Empresas</a></li>
        <li><a href="<?= BASE ?>index.php?page=alunos">Alunos</a></li>
      </ul>

      <!-- Ano gerado automaticamente pelo PHP -->
      <p class="footer-copy">
        &copy; <?= date('Y') ?> Portal de Estágios UniALFA. Todos os direitos reservados.
      </p>

    </div>

  </div>
</footer>

<!-- Bootstrap JS (local) — carrega o JS do Bootstrap para modais, menu hamburguer etc. -->
<script src="<?= BASE ?>assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>

</body>
</html>