# Importação de Alunos — Formato .txt

## Estrutura de cada linha

```
nome;ra;cpf;email
```

Os campos são separados por **ponto e vírgula** (`;`). A ordem é obrigatória.

| Campo | Regra de validação |
|-------|--------------------|
| nome  | Obrigatório, não pode ser vazio |
| ra    | Somente dígitos, entre 5 e 20 caracteres |
| cpf   | 11 dígitos numéricos — pontos e traços são aceitos e removidos automaticamente |
| email | Formato padrão `usuario@dominio.ext` |

## Regras gerais

- Linhas em branco são ignoradas.
- Linhas que começam com `#` são tratadas como comentários e ignoradas.
- Linhas com menos de 4 campos são ignoradas com aviso no console.
- Linhas com RA, CPF ou e-mail inválidos são ignoradas individualmente com aviso no console.
- Todos os alunos importados com sucesso são marcados como **elegíveis** automaticamente.

## Arquivo de exemplo

```
# Turma de Engenharia de Software — 2026
João da Silva;12345;123.456.789-09;joao.silva@email.com
Maria Oliveira;67890;987.654.321-00;maria.oliveira@faculdade.edu.br
Carlos Souza;11223;111.444.777-35;carlos.souza@gmail.com

# Linha abaixo será ignorada — RA inválido (menos de 5 dígitos)
Pedro Lima;123;000.000.000-00;pedro@email.com

# Linha abaixo será ignorada — CPF inválido
Ana Costa;55667;000.000.000-00;ana@email.com
```
