<?php
/**
 * Proxy de download do currículo de um candidato (uso da empresa).
 * Incluído por index.php ANTES do layout (saída binária + exit).
 * A API valida que a vaga pertence à empresa do membro autenticado.
 */

\App\Auth\Guard::requireCompany($api->jwt());

$vagaId = trim($_GET['job'] ?? '');
$appId  = trim($_GET['app'] ?? '');

if ($vagaId === '' || $appId === '') {
    http_response_code(400);
    header('Content-Type: text/html; charset=utf-8');
    echo '<p style="font-family:sans-serif">Parâmetros inválidos.</p>';
    return;
}

$resp = $api->companhia()->baixarCurriculoCandidato($vagaId, $appId);

if (!($resp['ok'] ?? false)) {
    http_response_code($resp['status'] ?? 404);
    header('Content-Type: text/html; charset=utf-8');
    $msg = $resp['message'] ?? 'Não foi possível baixar o currículo.';
    echo '<p style="font-family:sans-serif">' . htmlspecialchars($msg) . '</p>';
    return;
}

$filename = $resp['filename'] ?: 'curriculo-candidato';
header('Content-Type: ' . $resp['contentType']);
header('Content-Disposition: inline; filename="' . $filename . '"');
header('Content-Length: ' . strlen($resp['body']));
echo $resp['body'];
