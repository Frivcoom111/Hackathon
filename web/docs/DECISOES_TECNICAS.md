# Decisoes tecnicas

Este documento resume decisoes de arquitetura e produto adotadas no Portal de Estagios UniALFA.

## Arquitetura

- A aplicacao PHP segue MVC simples para separar rotas, controllers, services, models e views.
- O front controller fica em `public/index.php`.
- Regras de integracao ficam em services dentro de `app/Services`.
- O PHP nao acessa banco diretamente; operacoes de negocio passam pela API.
- O modo mock permite demonstrar o portal mesmo sem o backend ativo.

## Perfis

O sistema trabalha com dois fluxos principais:

- Aluno: busca vagas, visualiza detalhes, envia candidatura e acompanha status.
- Empresa: cadastra vagas, acompanha candidatos e atualiza etapas do processo seletivo.

## Interface

A interface usa azul institucional, amarelo para acoes principais e cards brancos para organizar informacoes. As telas foram estruturadas para desktop e mobile, com foco em leitura rapida, botoes claros e menus de caminho.

## Integracao

A integracao usa `ApiClient` para padronizar requisicoes HTTP, tratamento de erros e leitura do envelope de resposta do backend.

## Dados demonstrativos

Quando `USE_MOCK_DATA=true`, o projeto usa dados locais em `MockData` para vagas, empresas, candidaturas e notificacoes. Isso facilita apresentacao, testes manuais e desenvolvimento sem infraestrutura externa.
