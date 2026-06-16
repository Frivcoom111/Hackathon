<?php
/**
 * Proxy de download do currículo do próprio aluno.
 * Incluído por index.php ANTES do layout (saída binária + exit).
 * O <a href> não envia o JWT, então buscamos o binário via API (com Authorization)
 * e o repassamos com os cabeçalhos corretos.
 */

\App\Auth\Guard::requireStudent($api->jwt());

$resp = $api->estudante()->baixarCurriculo();

if (!($resp['ok'] ?? false)) {
    http_response_code($resp['status'] ?? 404);
    header('Content-Type: text/html; charset=utf-8');
    $msg = $resp['message'] ?? 'Não foi possível baixar o currículo.';
    echo '<p style="font-family:sans-serif">' . htmlspecialchars($msg) . '</p>';
    return;
}

$filename = $resp['filename'] ?: 'curriculo';
header('Content-Type: ' . $resp['contentType']);
header('Content-Disposition: inline; filename="' . $filename . '"');
header('Content-Length: ' . strlen($resp['body']));
echo $resp['body'];
