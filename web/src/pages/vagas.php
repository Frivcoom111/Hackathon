<!-- TOPO COM CAMPO DE BUSCA -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Vagas de Estágio</h1>
    <p class="vagas-hero-sub">Encontre a oportunidade ideal para o seu perfil</p>

    <form class="vagas-busca" id="form-busca">
      <div class="vagas-busca-inner">
        <div class="vagas-busca-campo">
          <i class="bi bi-search"></i>
          <input type="text" id="busca-titulo" placeholder="Cargo ou palavra-chave" class="form-control">
        </div>
        <div class="vagas-busca-campo">
          <i class="bi bi-geo-alt"></i>
          <input type="text" id="busca-cidade" placeholder="Cidade" class="form-control">
        </div>
        <!-- Ao submeter o form, aplica os filtros sem recarregar a página -->
        <button type="submit" class="btn btn-warning fw-semibold px-4">Buscar</button>
      </div>
    </form>
  </div>
</section>

<!-- CONTEÚDO: FILTROS + LISTA -->
<section class="vagas-lista-section">
  <div class="container">
    <div class="vagas-lista-layout">

      <!-- SIDEBAR DE FILTROS (aplicados no cliente após receber os dados da API) -->
      <aside class="vagas-filtros">
        <h6 class="filtros-titulo">Filtrar por</h6>

        <div class="filtro-grupo">
          <label class="filtro-label">Modalidade</label>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" id="f-presencial" value="PRESENCIAL" checked>
            <label class="form-check-label" for="f-presencial">Presencial</label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" id="f-remoto" value="REMOTE" checked>
            <label class="form-check-label" for="f-remoto">Remoto</label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="checkbox" id="f-hibrido" value="HYBRID" checked>
            <label class="form-check-label" for="f-hibrido">Híbrido</label>
          </div>
        </div>

        <div class="filtro-grupo">
          <label class="filtro-label">Área</label>
          <select class="form-select form-select-sm" id="f-area">
            <option value="">Todas as áreas</option>
            <option value="ti">Tecnologia</option>
            <option value="admin">Administração</option>
            <option value="marketing">Marketing</option>
            <option value="direito">Direito</option>
            <option value="saude">Saúde</option>
            <option value="educacao">Educação</option>
          </select>
        </div>

        <div class="filtro-grupo">
          <label class="filtro-label">Bolsa mínima</label>
          <select class="form-select form-select-sm" id="f-bolsa">
            <option value="0">Qualquer valor</option>
            <option value="500">A partir de R$ 500</option>
            <option value="800">A partir de R$ 800</option>
            <option value="1000">A partir de R$ 1.000</option>
            <option value="1500">A partir de R$ 1.500</option>
          </select>
        </div>

        <button class="btn btn-primary btn-sm w-100 mt-2" onclick="aplicarFiltros()">Aplicar filtros</button>
        <button class="btn btn-link btn-sm w-100 mt-1 text-secondary" onclick="limparFiltros()">Limpar filtros</button>
      </aside>

      <!-- LISTA DE VAGAS -->
      <div class="vagas-lista-conteudo">

        <div class="vagas-lista-header">
          <p class="vagas-total" id="total-vagas">Carregando...</p>
          <select class="form-select form-select-sm vagas-ordenar" id="ordenar" onchange="ordenarVagas()">
            <option value="recentes">Mais recentes</option>
            <option value="bolsa">Maior bolsa</option>
            <option value="az">A–Z</option>
          </select>
        </div>

        <!-- Spinner enquanto a API responde -->
        <div id="loading-vagas" class="text-center py-5">
          <div class="spinner-border text-primary" role="status"></div>
          <p class="text-secondary mt-2">Carregando vagas...</p>
        </div>

        <!-- Os cards são inseridos aqui pelo JavaScript -->
        <div class="row g-4" id="lista-vagas"></div>

        <!-- Aparece quando nenhuma vaga passa nos filtros -->
        <div id="sem-vagas" class="vagas-vazio" style="display:none;">
          <i class="bi bi-search"></i>
          <p>Nenhuma vaga encontrada com os filtros selecionados.</p>
          <button class="btn btn-outline-primary btn-sm" onclick="limparFiltros()">Limpar filtros</button>
        </div>

      </div>
    </div>
  </div>
</section>

<!-- MODAL: detalhes da vaga selecionada -->
<div class="modal fade" id="modal-vaga" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content vaga-modal-content">
      <div class="modal-header border-0 pb-0">
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body pt-0 px-4 pb-4">

        <div class="vaga-modal-top">
          <div class="empresa-logo empresa-logo-lg">
            <div class="empresa-logo-placeholder" style="display:flex;">
              <i class="bi bi-building fs-4"></i>
            </div>
          </div>
          <div>
            <!-- Preenchidos pelo JS ao abrir o modal -->
            <h4 class="vaga-modal-titulo mb-1" id="modal-titulo"></h4>
            <p class="vaga-empresa mb-0" id="modal-empresa"></p>
          </div>
          <span class="vaga-badge ms-auto align-self-start" id="modal-modalidade"></span>
        </div>

        <div class="vaga-modal-infos" id="modal-infos"></div>

        <hr>

        <h6 class="fw-semibold mb-2">Sobre a vaga</h6>
        <p class="descricao" id="modal-descricao"></p>

        <div id="modal-requisitos-bloco" style="display:none;">
          <h6 class="fw-semibold mb-2 mt-3">Requisitos</h6>
          <p class="descricao" id="modal-requisitos"></p>
        </div>

        <div class="d-flex gap-2 mt-4">
          <button class="btn btn-primary px-4" id="btn-candidatar">Candidatar-se</button>
          <button class="btn btn-outline-secondary" data-bs-dismiss="modal">Fechar</button>
        </div>

      </div>
    </div>
  </div>
</div>

<script>
// Guarda todas as vagas recebidas da API para poder filtrar sem buscar de novo
let todasVagas = [];

// Busca as vagas na API ao carregar a página
async function carregarVagas() {
  try {
    const res  = await fetch('http://localhost:3000/jobs?status=ACTIVE');
    const data = await res.json();

    todasVagas = data.jobs ?? [];

  } catch (erro) {
    // Se a API estiver offline, deixa a lista vazia
    todasVagas = [];
    console.error('Erro ao buscar vagas:', erro);
  }

  // Remove o spinner e exibe as vagas
  document.getElementById('loading-vagas').remove();
  renderizarVagas(todasVagas);
}

// Monta os cards na tela com base no array passado
function renderizarVagas(vagas) {
  const lista   = document.getElementById('lista-vagas');
  const semVagas = document.getElementById('sem-vagas');
  const total   = document.getElementById('total-vagas');

  // Limpa o que estava antes de renderizar novamente
  lista.innerHTML = '';

  if (vagas.length === 0) {
    semVagas.style.display = 'flex';
    total.innerHTML = 'Nenhuma vaga encontrada';
    return;
  }

  semVagas.style.display = 'none';
  total.innerHTML = `Mostrando <strong>${vagas.length}</strong> vaga${vagas.length !== 1 ? 's' : ''}`;

  vagas.forEach(vaga => {
    const col = document.createElement('div');
    col.className = 'col-lg-6 col-12';

    // Formata o salário ou mostra "A combinar"
    const salario = vaga.salary
      ? 'R$ ' + Number(vaga.salary).toFixed(2).replace('.', ',')
      : 'A combinar';

    // Label legível da modalidade
    const modalidadeLabel = { PRESENCIAL: 'Presencial', REMOTE: 'Remoto', HYBRID: 'Híbrido' };

    col.innerHTML = `
      <div class="vaga-card">
        <div class="vaga-card-top">
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
          <span><i class="bi bi-building"></i> ${modalidadeLabel[vaga.modality] ?? vaga.modality}</span>
        </div>
        <div class="vaga-footer">
          <span class="vaga-bolsa">${salario}</span>
          <button class="btn btn-primary btn-sm" onclick="abrirVaga(${JSON.stringify(vaga).replace(/"/g, '&quot;')})">
            Ver vaga
          </button>
        </div>
      </div>
    `;
    lista.appendChild(col);
  });
}

// Abre o modal com os detalhes da vaga clicada
function abrirVaga(vaga) {
  const modalidadeLabel = { PRESENCIAL: 'Presencial', REMOTE: 'Remoto', HYBRID: 'Híbrido' };
  const salario = vaga.salary
    ? 'R$ ' + Number(vaga.salary).toFixed(2).replace('.', ',')
    : 'A combinar';

  // Preenche os campos do modal com os dados da vaga
  document.getElementById('modal-titulo').textContent     = vaga.title;
  document.getElementById('modal-empresa').textContent    = vaga.company?.name ?? 'Empresa';
  document.getElementById('modal-modalidade').textContent = modalidadeLabel[vaga.modality] ?? vaga.modality;
  document.getElementById('modal-descricao').textContent  = vaga.description;

  document.getElementById('modal-infos').innerHTML = `
    <span><i class="bi bi-geo-alt"></i> ${vaga.location}</span>
    <span><i class="bi bi-currency-dollar"></i> ${salario}</span>
    <span><i class="bi bi-laptop"></i> ${vaga.area}</span>
  `;

  // Mostra os requisitos só se existirem
  if (vaga.requirements) {
    document.getElementById('modal-requisitos').textContent      = vaga.requirements;
    document.getElementById('modal-requisitos-bloco').style.display = 'block';
  } else {
    document.getElementById('modal-requisitos-bloco').style.display = 'none';
  }

  new bootstrap.Modal(document.getElementById('modal-vaga')).show();
}

// Filtra as vagas já carregadas sem fazer nova chamada à API
function aplicarFiltros() {
  const modalidades = [...document.querySelectorAll('input[type=checkbox]:checked')].map(el => el.value);
  const area        = document.getElementById('f-area').value.toLowerCase();
  const bolsaMin    = parseInt(document.getElementById('f-bolsa').value) || 0;
  const busca       = document.getElementById('busca-titulo').value.toLowerCase();
  const cidade      = document.getElementById('busca-cidade').value.toLowerCase();

  const filtradas = todasVagas.filter(vaga => {
    const okModalidade = modalidades.includes(vaga.modality);
    const okArea       = !area   || vaga.area.toLowerCase().includes(area);
    const okBolsa      = !vaga.salary || vaga.salary >= bolsaMin;
    const okBusca      = !busca  || vaga.title.toLowerCase().includes(busca);
    const okCidade     = !cidade || vaga.location.toLowerCase().includes(cidade);
    return okModalidade && okArea && okBolsa && okBusca && okCidade;
  });

  renderizarVagas(filtradas);
}

// Reseta todos os filtros e mostra todas as vagas
function limparFiltros() {
  document.getElementById('f-presencial').checked = true;
  document.getElementById('f-remoto').checked     = true;
  document.getElementById('f-hibrido').checked    = true;
  document.getElementById('f-area').value         = '';
  document.getElementById('f-bolsa').value        = '0';
  document.getElementById('busca-titulo').value   = '';
  document.getElementById('busca-cidade').value   = '';
  renderizarVagas(todasVagas);
}

// Ordena as vagas exibidas sem buscar da API de novo
function ordenarVagas() {
  const ordem = document.getElementById('ordenar').value;
  const copia = [...todasVagas];

  if (ordem === 'bolsa') {
    copia.sort((a, b) => (b.salary ?? 0) - (a.salary ?? 0));
  } else if (ordem === 'az') {
    copia.sort((a, b) => a.title.localeCompare(b.title));
  }

  renderizarVagas(copia);
}

// Submeter o form de busca aplica os filtros
document.getElementById('form-busca').addEventListener('submit', function(e) {
  e.preventDefault();
  aplicarFiltros();
});

// Inicia tudo buscando as vagas da API
carregarVagas();
</script>
