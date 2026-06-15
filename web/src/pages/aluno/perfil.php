<?php
require_once __DIR__ . '/../../api.php';

if (empty($_SESSION['token']) || ($_SESSION['role'] ?? '') === 'empresa') {
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

$token = $_SESSION['token'];

function voltar_perfil(): never
{
    header('Location: ' . BASE . 'index.php?page=perfil');
    exit;
}

function perfil_salvar_resposta(?array $resposta, string $sucessoPadrao): void
{
    if ($resposta['success'] ?? false) {
        $_SESSION['perfil_sucesso'] = $resposta['message'] ?? $sucessoPadrao;
        return;
    }

    $_SESSION['perfil_erro'] = $resposta['message'] ?? 'Nao foi possivel salvar. Tente novamente.';
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? '';

    try {
        if ($acao === 'perfil') {
            $resposta = api_patch_json('/student/profile', [
                'name' => trim($_POST['name'] ?? ''),
                'phone' => preg_replace('/\D/', '', $_POST['phone'] ?? ''),
                'headline' => trim($_POST['headline'] ?? ''),
                'summary' => trim($_POST['summary'] ?? ''),
                'isEligible' => isset($_POST['isEligible']),
            ], $token);

            perfil_salvar_resposta($resposta, 'Perfil atualizado com sucesso.');
        } elseif ($acao === 'endereco') {
            $resposta = api_patch_json('/student/address', [
                'street' => trim($_POST['street'] ?? ''),
                'number' => trim($_POST['number'] ?? ''),
                'complement' => trim($_POST['complement'] ?? ''),
                'district' => trim($_POST['district'] ?? ''),
                'city' => trim($_POST['city'] ?? ''),
                'state' => trim($_POST['state'] ?? ''),
                'zipCode' => preg_replace('/\D/', '', $_POST['zipCode'] ?? ''),
            ], $token);

            perfil_salvar_resposta($resposta, 'Endereco atualizado com sucesso.');
        } elseif ($acao === 'curso') {
            $resposta = api_patch_json('/student/course', [
                'courseId' => trim($_POST['courseId'] ?? ''),
                'status' => $_POST['status'] ?? 'ACTIVE',
                'startedAt' => $_POST['startedAt'] ?? '',
                'finishedAt' => $_POST['finishedAt'] ?? '',
            ], $token);

            perfil_salvar_resposta($resposta, 'Formacao atualizada com sucesso.');
        } elseif ($acao === 'certificado') {
            $resposta = api_post_json('/student/certificates', [
                'name' => trim($_POST['name'] ?? ''),
                'institution' => trim($_POST['institution'] ?? ''),
                'issuedAt' => $_POST['issuedAt'] ?? '',
            ], $token);

            perfil_salvar_resposta($resposta, 'Certificado adicionado ao perfil.');
        } elseif ($acao === 'capa') {
            if (empty($_FILES['coverPhoto']['tmp_name'])) {
                $_SESSION['perfil_erro'] = 'Escolha uma imagem de capa.';
            } else {
                $resposta = api_post_form('/student/profile/cover', [
                    'image' => $_FILES['coverPhoto'],
                ], $token);
                perfil_salvar_resposta($resposta, 'Capa atualizada.');
            }
        } else {
            $_SESSION['perfil_erro'] = 'Acao nao encontrada.';
        }
    } catch (Throwable $e) {
        $_SESSION['perfil_erro'] = 'Erro inesperado ao salvar o perfil.';
    }

    voltar_perfil();
}

$profileResponse = api_get('/student/profile', $token);
$student = $profileResponse['data'] ?? null;

if (!$student) {
    $demo = demo_students()[1] ?? demo_students()[0];
    $student = [
        'name' => $demo['name'],
        'ra' => $demo['ra'],
        'cpf' => $demo['cpf'],
        'phone' => $demo['phone'],
        'headline' => 'Aluno de ' . $demo['course'],
        'summary' => 'Aluno da UniALFA em busca de oportunidades para aprender na pratica e crescer profissionalmente.',
        'coverPhotoPath' => null,
        'isEligible' => true,
        'user' => ['email' => 'aluno@unialfa.edu.br'],
        'address' => ['city' => 'Umuarama', 'state' => 'PR'],
        'courses' => [
            [
                'status' => 'ACTIVE',
                'startedAt' => date('Y-m-d'),
                'finishedAt' => null,
                'course' => ['id' => 'course-ads', 'name' => $demo['course'], 'code' => 'ADS'],
            ],
        ],
        'certificates' => [],
        'applications' => [],
    ];
}

$cursosDisponiveis = api_items(api_get('/courses'), 'courses');
if ($cursosDisponiveis === []) {
    $cursosDisponiveis = demo_courses();
}

$sucesso = $_SESSION['perfil_sucesso'] ?? '';
$erro = $_SESSION['perfil_erro'] ?? '';
unset($_SESSION['perfil_sucesso'], $_SESSION['perfil_erro']);

$courses = $student['courses'] ?? [];
$mainCourseItem = $courses[0] ?? null;
$mainCourse = $mainCourseItem['course']['name'] ?? 'Curso nao informado';
$courseStatus = $mainCourseItem['status'] ?? 'ACTIVE';
$certificates = $student['certificates'] ?? [];
$address = $student['address'] ?? [];
$city = $address['city'] ?? 'Umuarama';
$state = $address['state'] ?? 'PR';
$email = $student['user']['email'] ?? '';
$phone = $student['phone'] ?? '';
$applications = $student['applications'] ?? [];
$summary = trim((string)($student['summary'] ?? ''));
$headline = trim((string)($student['headline'] ?? ''));
$coverUrl = uploaded_file_url($student['coverPhotoPath'] ?? null) ?: BASE . 'assets/images/site/login.png';

function initials_from_name(string $name): string
{
    $parts = array_values(array_filter(explode(' ', trim($name))));
    $first = strtoupper(substr($parts[0] ?? 'A', 0, 1));
    $last = strtoupper(substr($parts[count($parts) - 1] ?? 'L', 0, 1));
    return $first . $last;
}

function uploaded_file_url(?string $path): string
{
    if (!$path) {
        return '';
    }

    $clean = str_replace('\\', '/', $path);
    if (str_starts_with($clean, 'http://') || str_starts_with($clean, 'https://')) {
        return $clean;
    }

    $pos = strpos($clean, 'uploads/');
    if ($pos !== false) {
        $clean = substr($clean, $pos);
    }

    return api_base_url() . '/' . ltrim($clean, '/');
}

function date_input(?string $date): string
{
    if (!$date) {
        return '';
    }

    $time = strtotime($date);
    return $time ? date('Y-m-d', $time) : '';
}

function date_br(?string $date): string
{
    if (!$date) {
        return '';
    }

    $time = strtotime($date);
    return $time ? date('d/m/Y', $time) : '';
}

function student_status_label(string $status): string
{
    return match ($status) {
        'COMPLETED' => 'Finalizado',
        'CANCELLED' => 'Nao finalizado',
        default => 'Em andamento',
    };
}

function application_status_label(string $status): string
{
    return match ($status) {
        'ANALYSING' => 'Em analise',
        'APPROVED' => 'Aprovada',
        'REJECTED' => 'Rejeitada',
        'CANCELLED' => 'Cancelada',
        default => 'Pendente',
    };
}

function application_badge_class(string $status): string
{
    return match ($status) {
        'APPROVED' => 'badge-success',
        'REJECTED' => 'badge-danger',
        'CANCELLED' => 'badge-secondary',
        'ANALYSING' => 'badge-warning',
        default => 'badge-info',
    };
}
?>

<section class="perfil-page">
  <div class="container perfil-layout">
    <div class="perfil-main">
      <?php if ($sucesso): ?>
        <div class="alert alert-success perfil-alert"><?= htmlspecialchars($sucesso) ?></div>
      <?php endif; ?>

      <?php if ($erro): ?>
        <div class="alert alert-danger perfil-alert"><?= htmlspecialchars($erro) ?></div>
      <?php endif; ?>

      <article class="perfil-card perfil-hero-card">
        <div class="perfil-cover">
          <img src="<?= htmlspecialchars($coverUrl) ?>" alt="Capa do perfil">
          <button class="perfil-cover-action" type="button" data-bs-toggle="modal" data-bs-target="#modalCapa" title="Alterar capa">
            <i class="bi bi-camera"></i>
          </button>
        </div>

        <div class="perfil-identity">
          <div class="perfil-avatar" aria-label="Foto do perfil">
            <?= htmlspecialchars(initials_from_name($student['name'] ?? 'Aluno')) ?>
          </div>

          <div class="perfil-actions">
            <button class="btn btn-outline-secondary btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalPerfil">
              <i class="bi bi-pencil"></i>
            </button>
          </div>

          <h1><?= htmlspecialchars($student['name'] ?? 'Aluno') ?></h1>
          <div class="perfil-verify">
            <i class="bi bi-patch-check-fill"></i>
            <span>Perfil academico verificado</span>
          </div>

          <p class="perfil-headline">
            <?= htmlspecialchars($headline ?: $mainCourse . ' - Portal de Estagios UniALFA') ?>
          </p>

          <p class="perfil-location">
            <?= htmlspecialchars($city) ?>, <?= htmlspecialchars($state) ?>
            <span class="perfil-dot">-</span>
            <a href="#contato">Dados de contato</a>
          </p>

          <div class="perfil-buttons">
            <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">
              <?= !empty($student['isEligible']) ? 'Disponivel para vagas' : 'Ver vagas disponiveis' ?>
            </a>
            <button class="btn btn-outline-primary btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalCertificado">Adicionar secao</button>
            <button class="btn btn-outline-primary btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalPerfil">Aprimorar perfil</button>
          </div>
        </div>
      </article>

      <article class="perfil-card">
        <div class="perfil-card-header">
          <div>
            <h2>Sobre</h2>
            <p>Resumo profissional exibido para empresas parceiras.</p>
          </div>
          <button class="btn btn-light btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalPerfil">
            <i class="bi bi-pencil"></i>
          </button>
        </div>
        <p class="perfil-text">
          <?= htmlspecialchars($summary ?: 'Escreva um resumo simples falando sobre seus objetivos, habilidades e tipo de oportunidade que procura.') ?>
        </p>
      </article>

      <article class="perfil-card" id="formacao">
        <div class="perfil-card-header">
          <div>
            <h2>Formacao academica</h2>
            <p>Curso, situacao atual e periodo de estudo.</p>
          </div>
          <button class="btn btn-light btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalCurso">
            <i class="bi bi-pencil"></i>
          </button>
        </div>

        <?php if (empty($courses)): ?>
          <div class="perfil-empty">
            <i class="bi bi-mortarboard"></i>
            <p>Nenhum curso vinculado ao perfil.</p>
            <button class="btn btn-outline-primary btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalCurso">Adicionar curso</button>
          </div>
        <?php else: ?>
          <div class="perfil-timeline">
            <?php foreach ($courses as $courseItem): ?>
              <?php $course = $courseItem['course'] ?? []; ?>
              <div class="perfil-timeline-item">
                <div class="perfil-timeline-icon"><i class="bi bi-mortarboard"></i></div>
                <div>
                  <h3><?= htmlspecialchars($course['name'] ?? 'Curso') ?></h3>
                  <p><?= htmlspecialchars($course['code'] ?? 'UniALFA') ?> - <?= student_status_label($courseItem['status'] ?? 'ACTIVE') ?></p>
                  <?php if (!empty($courseItem['startedAt'])): ?>
                    <span>Inicio: <?= htmlspecialchars(date_br($courseItem['startedAt'])) ?></span>
                  <?php endif; ?>
                  <?php if (!empty($courseItem['finishedAt'])): ?>
                    <span>Conclusao: <?= htmlspecialchars(date_br($courseItem['finishedAt'])) ?></span>
                  <?php endif; ?>
                </div>
              </div>
            <?php endforeach; ?>
          </div>
        <?php endif; ?>
      </article>

      <article class="perfil-card">
        <div class="perfil-card-header">
          <div>
            <h2>Certificados</h2>
            <p>Cursos extras e comprovantes que valorizam o perfil.</p>
          </div>
          <button class="btn btn-light btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalCertificado">
            <i class="bi bi-plus-lg"></i>
          </button>
        </div>

        <?php if (empty($certificates)): ?>
          <div class="perfil-empty">
            <i class="bi bi-award"></i>
            <p>Nenhum certificado adicionado ainda.</p>
            <button class="btn btn-outline-primary btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalCertificado">Adicionar certificado</button>
          </div>
        <?php else: ?>
          <div class="perfil-timeline">
            <?php foreach ($certificates as $certificate): ?>
              <div class="perfil-timeline-item">
                <div class="perfil-timeline-icon"><i class="bi bi-award"></i></div>
                <div>
                  <h3><?= htmlspecialchars($certificate['name'] ?? 'Certificado') ?></h3>
                  <p><?= htmlspecialchars($certificate['institution'] ?? 'Instituicao nao informada') ?></p>
                  <?php if (!empty($certificate['issuedAt'])): ?>
                    <span>Emitido em: <?= htmlspecialchars(date_br($certificate['issuedAt'])) ?></span>
                  <?php endif; ?>
                </div>
              </div>
            <?php endforeach; ?>
          </div>
        <?php endif; ?>
      </article>

      <article class="perfil-card">
        <div class="perfil-card-header">
          <div>
            <h2>Candidaturas</h2>
            <p>Vagas acompanhadas pelo portal.</p>
          </div>
          <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Buscar vagas</a>
        </div>

        <?php if (empty($applications)): ?>
          <div class="perfil-empty">
            <i class="bi bi-briefcase"></i>
            <p>Nenhuma candidatura registrada ainda.</p>
            <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">Ver vagas</a>
          </div>
        <?php else: ?>
          <div class="perfil-app-list">
            <?php foreach ($applications as $application): ?>
              <?php $job = $application['job'] ?? []; ?>
              <div class="perfil-app-item">
                <div class="perfil-company-avatar">
                  <?= htmlspecialchars(initials_from_name($job['company']['name'] ?? 'Empresa')) ?>
                </div>
                <div>
                  <h3><?= htmlspecialchars($job['title'] ?? 'Vaga') ?></h3>
                  <p><?= htmlspecialchars($job['company']['name'] ?? 'Empresa') ?> - <?= htmlspecialchars($job['location'] ?? '') ?></p>
                </div>
                <span class="badge <?= application_badge_class($application['status'] ?? 'PENDING') ?>">
                  <?= application_status_label($application['status'] ?? 'PENDING') ?>
                </span>
              </div>
            <?php endforeach; ?>
          </div>
        <?php endif; ?>
      </article>
    </div>

    <aside class="perfil-side">
      <article class="perfil-card" id="contato">
        <div class="perfil-card-header">
          <div>
            <h2>Contato</h2>
            <p>Dados usados pelas empresas.</p>
          </div>
          <button class="btn btn-light btn-sm" type="button" data-bs-toggle="modal" data-bs-target="#modalEndereco">
            <i class="bi bi-pencil"></i>
          </button>
        </div>
        <div class="perfil-contact-list">
          <span><i class="bi bi-envelope"></i> <?= htmlspecialchars($email ?: 'E-mail nao informado') ?></span>
          <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($phone ?: 'Telefone nao informado') ?></span>
          <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($city) ?>, <?= htmlspecialchars($state) ?></span>
          <span><i class="bi bi-card-text"></i> RA <?= htmlspecialchars($student['ra'] ?? '-') ?></span>
        </div>
      </article>

      <article class="perfil-card">
        <h2>Sugestoes</h2>
        <button class="perfil-suggestion" type="button" data-bs-toggle="modal" data-bs-target="#modalPerfil">
          <i class="bi bi-file-person"></i>
          <span>
            <strong>Atualize seu resumo</strong>
            <small>Perfis com resumo claro chamam mais atencao das empresas.</small>
          </span>
        </button>
        <button class="perfil-suggestion" type="button" data-bs-toggle="modal" data-bs-target="#modalCertificado">
          <i class="bi bi-award"></i>
          <span>
            <strong>Adicione certificados</strong>
            <small>Mostre cursos extras e atividades importantes.</small>
          </span>
        </button>
      </article>
    </aside>
  </div>
</section>

<div class="modal fade" id="modalPerfil" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil">
      <input type="hidden" name="acao" value="perfil">
      <div class="modal-header">
        <h5 class="modal-title">Editar perfil</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">Nome completo</label>
          <input class="form-control" name="name" value="<?= htmlspecialchars($student['name'] ?? '') ?>" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Titulo profissional</label>
          <input class="form-control" name="headline" maxlength="160" value="<?= htmlspecialchars($headline) ?>" placeholder="Ex: Aluno de ADS buscando estagio em desenvolvimento">
        </div>
        <div class="mb-3">
          <label class="form-label">Telefone</label>
          <input class="form-control" name="phone" value="<?= htmlspecialchars($phone) ?>" placeholder="(44) 99999-9999">
        </div>
        <div class="mb-3">
          <label class="form-label">Resumo</label>
          <textarea class="form-control" name="summary" rows="5" placeholder="Fale sobre seus objetivos, habilidades e experiencias."><?= htmlspecialchars($summary) ?></textarea>
        </div>
        <div class="form-check">
          <input class="form-check-input" type="checkbox" name="isEligible" id="perfilElegivel" <?= !empty($student['isEligible']) ? 'checked' : '' ?>>
          <label class="form-check-label" for="perfilElegivel">Estou disponivel para vagas</label>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary">Salvar perfil</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="modalEndereco" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil">
      <input type="hidden" name="acao" value="endereco">
      <div class="modal-header">
        <h5 class="modal-title">Editar endereco</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <div class="row g-3">
          <div class="col-8">
            <label class="form-label">Rua</label>
            <input class="form-control" name="street" value="<?= htmlspecialchars($address['street'] ?? '') ?>" required>
          </div>
          <div class="col-4">
            <label class="form-label">Numero</label>
            <input class="form-control" name="number" value="<?= htmlspecialchars($address['number'] ?? '') ?>" required>
          </div>
          <div class="col-12">
            <label class="form-label">Complemento</label>
            <input class="form-control" name="complement" value="<?= htmlspecialchars($address['complement'] ?? '') ?>">
          </div>
          <div class="col-12">
            <label class="form-label">Bairro</label>
            <input class="form-control" name="district" value="<?= htmlspecialchars($address['district'] ?? '') ?>" required>
          </div>
          <div class="col-7">
            <label class="form-label">Cidade</label>
            <input class="form-control" name="city" value="<?= htmlspecialchars($city) ?>" required>
          </div>
          <div class="col-5">
            <label class="form-label">UF</label>
            <input class="form-control" name="state" maxlength="2" value="<?= htmlspecialchars($state) ?>" required>
          </div>
          <div class="col-12">
            <label class="form-label">CEP</label>
            <input class="form-control" name="zipCode" value="<?= htmlspecialchars($address['zipCode'] ?? '') ?>" required>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary">Salvar endereco</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="modalCurso" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil">
      <input type="hidden" name="acao" value="curso">
      <div class="modal-header">
        <h5 class="modal-title">Editar formacao</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">Curso</label>
          <select class="form-select" name="courseId" required>
            <option value="">Selecione</option>
            <?php foreach ($cursosDisponiveis as $curso): ?>
              <option value="<?= htmlspecialchars($curso['id']) ?>" <?= (($mainCourseItem['course']['id'] ?? '') === ($curso['id'] ?? '')) ? 'selected' : '' ?>>
                <?= htmlspecialchars($curso['name']) ?>
              </option>
            <?php endforeach; ?>
          </select>
        </div>
        <div class="mb-3">
          <label class="form-label">Status do curso</label>
          <select class="form-select" name="status" id="perfilStatusCurso" onchange="perfilToggleConclusao()">
            <option value="ACTIVE" <?= $courseStatus === 'ACTIVE' ? 'selected' : '' ?>>Em andamento</option>
            <option value="COMPLETED" <?= $courseStatus === 'COMPLETED' ? 'selected' : '' ?>>Finalizado</option>
            <option value="CANCELLED" <?= $courseStatus === 'CANCELLED' ? 'selected' : '' ?>>Nao finalizado</option>
          </select>
        </div>
        <div class="mb-3">
          <label class="form-label">Inicio do curso</label>
          <input class="form-control" type="date" name="startedAt" value="<?= htmlspecialchars(date_input($mainCourseItem['startedAt'] ?? '')) ?>" required>
        </div>
        <div class="mb-3" id="perfilConclusaoGrupo">
          <label class="form-label">Data de conclusao</label>
          <input class="form-control" type="date" name="finishedAt" id="perfilFinishedAt" value="<?= htmlspecialchars(date_input($mainCourseItem['finishedAt'] ?? '')) ?>">
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary">Salvar formacao</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="modalCertificado" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil">
      <input type="hidden" name="acao" value="certificado">
      <div class="modal-header">
        <h5 class="modal-title">Adicionar certificado</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label">Nome do certificado</label>
          <input class="form-control" name="name" placeholder="Ex: HTML e CSS para iniciantes" required>
        </div>
        <div class="mb-3">
          <label class="form-label">Instituicao</label>
          <input class="form-control" name="institution" placeholder="Ex: UniALFA, Alura, Curso em Video">
        </div>
        <div class="mb-3">
          <label class="form-label">Data de emissao</label>
          <input class="form-control" type="date" name="issuedAt" required>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary">Adicionar</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="modalCapa" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="<?= BASE ?>index.php?page=perfil" enctype="multipart/form-data">
      <input type="hidden" name="acao" value="capa">
      <div class="modal-header">
        <h5 class="modal-title">Alterar capa</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <label class="form-label">Imagem de capa</label>
        <input class="form-control" type="file" name="coverPhoto" accept=".jpg,.jpeg,.png,.webp" required>
        <small class="text-secondary">Use uma imagem horizontal para ficar melhor.</small>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="submit" class="btn btn-primary">Salvar capa</button>
      </div>
    </form>
  </div>
</div>

<script>
function perfilToggleConclusao() {
  const status = document.getElementById('perfilStatusCurso');
  const grupo = document.getElementById('perfilConclusaoGrupo');
  const campo = document.getElementById('perfilFinishedAt');

  if (!status || !grupo || !campo) return;

  const finalizado = status.value === 'COMPLETED';
  grupo.classList.toggle('d-none', !finalizado);
  campo.required = finalizado;

  if (!finalizado) {
    campo.value = '';
  }
}

perfilToggleConclusao();
</script>

