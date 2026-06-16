<?php $semFooter = in_array($pagina ?? '', ['login', 'cadastro'], true); ?>

<?php if (!$semFooter): ?>
<footer>
  <div class="container">

    <div class="footer-grid">

      <!-- Sobre + endereço -->
      <div class="footer-col footer-col--sobre">
        <a href="<?= BASE ?>index.php" class="footer-brand">
          <img src="<?= BASE ?>assets/images/site/logo.png" alt="Portal UniALFA"
               onerror="this.style.display='none'; this.nextElementSibling.style.display='inline'">
          <span style="display:none;">Portal UniALFA</span>
        </a>
        <p class="footer-sobre">
          Portal de Estágios da UniALFA — conectando os talentos da faculdade às
          oportunidades das empresas da região.
        </p>
        <p class="footer-endereco">
          <i class="bi bi-geo-alt-fill"></i>
          Av. Paraná, 7327 — Zona III<br>
          Umuarama/PR — CEP 87502-000
        </p>
      </div>

      <!-- Links rápidos -->
      <div class="footer-col">
        <h6 class="footer-titulo">Navegação</h6>
        <ul class="footer-links">
          <li><a href="<?= BASE ?>index.php?page=home">Início</a></li>
          <li><a href="<?= BASE ?>index.php?page=vagas">Vagas</a></li>
        </ul>
      </div>

      <!-- Contato -->
      <div class="footer-col">
        <h6 class="footer-titulo">Contato</h6>
        <ul class="footer-contato">
          <li>
            <a href="tel:+554436222500"><i class="bi bi-telephone-fill"></i> (44) 3622-2500</a>
          </li>
          <li>
            <a href="https://wa.me/554436222500" target="_blank" rel="noopener">
              <i class="bi bi-whatsapp"></i> WhatsApp
            </a>
          </li>
        </ul>
      </div>

      <!-- Redes sociais -->
      <div class="footer-col">
        <h6 class="footer-titulo">Redes sociais</h6>
        <div class="footer-social">
          <a href="https://www.instagram.com/faculdadealfaumuarama" target="_blank"
             rel="noopener" aria-label="Instagram"><i class="bi bi-instagram"></i></a>
          <a href="https://www.facebook.com/faculdadeAlfaUmuarama" target="_blank"
             rel="noopener" aria-label="Facebook"><i class="bi bi-facebook"></i></a>
          <a href="https://www.youtube.com/" target="_blank"
             rel="noopener" aria-label="YouTube"><i class="bi bi-youtube"></i></a>
        </div>
      </div>

    </div>

    <div class="footer-bottom">
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
