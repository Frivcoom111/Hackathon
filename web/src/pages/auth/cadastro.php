<?php
$erro     = '';
$abaAtiva = 'aluno';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $tipo     = $_POST['tipo'] ?? 'aluno';
    $abaAtiva = $tipo;

    $senha     = $_POST['senha']          ?? '';
    $confirmar = $_POST['senha_confirmar'] ?? '';

    if ($senha !== $confirmar) {
        $erro = 'As senhas não coincidem.';

    // ── Cadastro de aluno ─────────────────────────────────────────────────────
    } elseif ($tipo === 'aluno') {
        $statusCurso = $_POST['statusCurso'] ?? 'ACTIVE';

        $campos = [
            'email'     => trim($_POST['email']    ?? ''),
            'password'  => $senha,
            'name'      => trim($_POST['nome']     ?? ''),
            'ra'        => trim($_POST['ra']        ?? ''),
            'cpf'       => preg_replace('/\D/', '', $_POST['cpf']   ?? ''),
            'phone'     => preg_replace('/\D/', '', $_POST['phone'] ?? ''),
            'courseId'  => trim($_POST['courseId'] ?? ''),
            'status'    => $statusCurso,
            'startedAt' => $_POST['startedAt']     ?? '',
        ];

        if ($statusCurso === 'COMPLETED') {
            $campos['finishedAt'] = $_POST['finishedAt'] ?? '';
        }

        $data = $api->auth()->registrarEstudante($campos);

        if ($data['success'] ?? false) {
            $_SESSION['msg_sucesso'] = 'Cadastro realizado! Faça login para continuar.';
            header('Location: ' . BASE . 'index.php?page=login');
            exit;
        } else {
            $erro = $data['message'] ?? 'Erro ao cadastrar. Tente novamente.';
        }

    // ── Cadastro de empresa ───────────────────────────────────────────────────
    } elseif ($tipo === 'empresa') {
        $payload = [
            'email'       => trim($_POST['email']     ?? ''),
            'password'    => $senha,
            'name'        => trim($_POST['nome']      ?? ''),
            'cnpj'        => preg_replace('/\D/', '', $_POST['cnpj']  ?? ''),
            'description' => trim($_POST['descricao'] ?? ''),
            'phone'       => preg_replace('/\D/', '', $_POST['phone'] ?? ''),
            'address' => [
                'street'     => trim($_POST['rua']         ?? ''),
                'number'     => trim($_POST['numero']      ?? ''),
                'complement' => trim($_POST['complemento'] ?? ''),
                'district'   => trim($_POST['bairro']      ?? ''),
                'city'       => trim($_POST['cidade']      ?? ''),
                'state'      => trim($_POST['estado']      ?? ''),
                'zipCode'    => preg_replace('/\D/', '', $_POST['cep'] ?? ''),
            ],
            'member' => [
                'name'  => trim($_POST['resp_nome']  ?? ''),
                'cpf'   => preg_replace('/\D/', '', $_POST['resp_cpf']   ?? ''),
                'phone' => preg_replace('/\D/', '', $_POST['resp_phone'] ?? ''),
            ],
        ];

        $data = $api->auth()->registrarEmpresa($payload);

        if ($data['success'] ?? false) {
            $_SESSION['msg_sucesso'] = 'Empresa cadastrada! Aguarde a aprovação pelo administrador.';
            header('Location: ' . BASE . 'index.php?page=login');
            exit;
        } else {
            $erro = $data['message'] ?? 'Erro ao cadastrar empresa. Tente novamente.';
        }
    }
}

$cursos     = $api->cursos()->listar();
$semCursos  = empty($cursos);
?>

<main class="cadastro-page">
  <div class="cadastro-container">

    <div class="cadastro-imagem">
      <img src="<?= BASE ?>assets/images/site/login.png" alt="Cadastro">
    </div>

    <div class="cadastro-form-box">

      <div class="login-tabs">
        <a href="<?= BASE ?>index.php?page=login" class="login-tab">Entrar</a>
        <a href="<?= BASE ?>index.php?page=cadastro" class="login-tab active">Cadastrar</a>
      </div>

      <h2 class="cadastro-titulo">Crie sua conta</h2>
      <p class="cadastro-sub">Escolha o tipo de conta para continuar</p>

      <div class="cadastro-tabs">
        <button class="cadastro-tab <?= $abaAtiva === 'aluno' ? 'active' : '' ?>"
                onclick="trocarAba('aluno', this)">Aluno</button>
        <button class="cadastro-tab <?= $abaAtiva === 'empresa' ? 'active' : '' ?>"
                onclick="trocarAba('empresa', this)">Empresa</button>
      </div>

      <?php if ($erro): ?>
        <div class="alert alert-danger mt-3 py-2"><?= htmlspecialchars($erro) ?></div>
      <?php endif; ?>

      <!-- ── FORMULÁRIO ALUNO ── -->
      <form id="form-aluno" class="cadastro-form <?= $abaAtiva === 'empresa' ? 'd-none' : '' ?>"
            method="POST" action="<?= BASE ?>index.php?page=cadastro">
        <input type="hidden" name="tipo" value="aluno">
        <div class="row g-3">

          <div class="col-12">
            <label class="form-label">Nome completo</label>
            <input type="text" name="nome" class="form-control" placeholder="Seu nome completo" required>
          </div>

          <div class="col-12">
            <label class="form-label">E-mail</label>
            <input type="email" name="email" class="form-control" placeholder="seu@email.com" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">CPF</label>
            <input type="text" name="cpf" class="form-control" placeholder="000.000.000-00" maxlength="14" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Telefone</label>
            <input type="text" name="phone" class="form-control" placeholder="(44) 99999-9999">
          </div>

          <div class="col-md-6">
            <label class="form-label">RA</label>
            <input type="text" name="ra" class="form-control" placeholder="Registro Acadêmico" required>
          </div>

          <div class="col-md-6">
            <label class="form-label" for="courseId">Curso</label>
            <select name="courseId" id="courseId" class="form-select" required <?= $semCursos ? 'disabled' : '' ?>>
              <option value="">Selecione o curso</option>
              <?php foreach ($cursos as $curso): ?>
                <option value="<?= htmlspecialchars($curso['id']) ?>">
                  <?= htmlspecialchars($curso['name']) ?>
                </option>
              <?php endforeach; ?>
            </select>
            <?php if ($semCursos): ?>
              <div class="form-text text-danger">
                <i class="bi bi-exclamation-triangle me-1"></i>
                Não foi possível carregar a lista de cursos. Verifique se a API está ativa e tente novamente.
              </div>
            <?php endif; ?>
          </div>

          <div class="col-md-6">
            <label class="form-label" for="statusCurso">Status do curso</label>
            <select name="statusCurso" id="statusCurso" class="form-select" onchange="toggleConclusao()">
              <option value="ACTIVE">Em andamento</option>
              <option value="COMPLETED">Finalizado</option>
              <option value="CANCELLED">Não finalizado</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Data de início do curso</label>
            <input type="date" name="startedAt" class="form-control" required>
          </div>

          <div class="col-12 d-none" id="campoConclusao">
            <label class="form-label" for="finishedAt">Data de conclusão do curso</label>
            <input type="date" name="finishedAt" id="finishedAt" class="form-control">
          </div>

          <div class="col-12">
            <label class="form-label">Senha</label>
            <input type="password" name="senha" class="form-control" placeholder="Mínimo 8 caracteres" required>
          </div>

          <div class="col-12">
            <label class="form-label">Confirmar senha</label>
            <input type="password" name="senha_confirmar" class="form-control" placeholder="Repita a senha" required>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Cadastrar como Aluno</button>
          </div>

        </div>
      </form>

      <!-- ── FORMULÁRIO EMPRESA ── -->
      <form id="form-empresa" class="cadastro-form <?= $abaAtiva === 'empresa' ? '' : 'd-none' ?>"
            method="POST" action="<?= BASE ?>index.php?page=cadastro">
        <input type="hidden" name="tipo" value="empresa">
        <div class="row g-3">

          <div class="col-12">
            <label class="form-label">Nome da empresa</label>
            <input type="text" name="nome" class="form-control" placeholder="Razão social" required>
          </div>

          <div class="col-12">
            <label class="form-label">E-mail</label>
            <input type="email" name="email" class="form-control" placeholder="contato@empresa.com" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">CNPJ</label>
            <input type="text" name="cnpj" class="form-control" placeholder="00.000.000/0000-00" maxlength="18" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Telefone</label>
            <input type="text" name="phone" class="form-control" placeholder="(44) 99999-9999">
          </div>

          <div class="col-12">
            <label class="form-label">Descrição <span class="text-muted small">(opcional)</span></label>
            <textarea name="descricao" class="form-control" rows="2" placeholder="Fale sobre sua empresa"></textarea>
          </div>

          <div class="col-12"><hr class="my-1"><p class="fw-semibold mb-0 small text-secondary">Endereço</p></div>

          <div class="col-md-8">
            <label class="form-label">Rua</label>
            <input type="text" name="rua" class="form-control" placeholder="Av. Paulista" required>
          </div>

          <div class="col-md-4">
            <label class="form-label">Número</label>
            <input type="text" name="numero" class="form-control" placeholder="1000" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Complemento <span class="text-muted small">(opcional)</span></label>
            <input type="text" name="complemento" class="form-control" placeholder="Sala 12">
          </div>

          <div class="col-md-6">
            <label class="form-label">Bairro</label>
            <input type="text" name="bairro" class="form-control" placeholder="Centro" required>
          </div>

          <div class="col-md-5">
            <label class="form-label">Cidade</label>
            <input type="text" name="cidade" class="form-control" placeholder="Umuarama" required>
          </div>

          <div class="col-md-3">
            <label class="form-label">Estado</label>
            <input type="text" name="estado" class="form-control" placeholder="PR" maxlength="2" required>
          </div>

          <div class="col-md-4">
            <label class="form-label">CEP</label>
            <input type="text" name="cep" class="form-control" placeholder="00000-000" maxlength="9" required>
          </div>

          <div class="col-12"><hr class="my-1"><p class="fw-semibold mb-0 small text-secondary">Responsável</p></div>

          <div class="col-12">
            <label class="form-label">Nome do responsável</label>
            <input type="text" name="resp_nome" class="form-control" placeholder="Nome completo" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">CPF do responsável</label>
            <input type="text" name="resp_cpf" class="form-control" placeholder="000.000.000-00" maxlength="14" required>
          </div>

          <div class="col-md-6">
            <label class="form-label">Telefone do responsável</label>
            <input type="text" name="resp_phone" class="form-control" placeholder="(44) 99999-9999">
          </div>

          <div class="col-12">
            <label class="form-label">Senha</label>
            <input type="password" name="senha" class="form-control" placeholder="Mínimo 8 caracteres" required>
          </div>

          <div class="col-12">
            <label class="form-label">Confirmar senha</label>
            <input type="password" name="senha_confirmar" class="form-control" placeholder="Repita a senha" required>
          </div>

          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Cadastrar como Empresa</button>
          </div>

        </div>
      </form>

      <p class="cadastro-login">
        Já tem uma conta? <a href="<?= BASE ?>index.php?page=login">Entrar</a>
      </p>

    </div>
  </div>
</main>

<script>
function trocarAba(tipo, btn) {
  document.querySelectorAll('.cadastro-tab').forEach(t => t.classList.remove('active'));
  btn.classList.add('active');
  document.getElementById('form-aluno').classList.toggle('d-none',   tipo !== 'aluno');
  document.getElementById('form-empresa').classList.toggle('d-none', tipo !== 'empresa');
}

function toggleConclusao() {
  const status = document.getElementById('statusCurso').value;
  const campo = document.getElementById('campoConclusao');
  const input = document.getElementById('finishedAt');
  const finalizado = status === 'COMPLETED';

  campo.classList.toggle('d-none', !finalizado);
  input.required = finalizado;
  if (!finalizado) input.value = '';
}
</script>
