POO (PHP Orientado a Objetos)
Para a camada de interação, deve ser construída uma aplicação web em PHP que consuma a
API Node.JS. O rigor na modelagem orientada a objetos é essencial.
● Modelagem de Domínio: É obrigatória a criação e utilização de classes bem definidas
para representar as entidades centrais do negócio, no mínimo: Aluno, Empresa, Vaga e
Candidatura.
● Painel da Empresa: Desenvolver uma área restrita (Back Office) onde a empresa possa
realizar o CRUD (Criar, Ler, Atualizar, Excluir) de suas vagas e visualizar a lista de alunos
candidatos a cada uma delas.
● Portal do Aluno: Desenvolver a interface onde o aluno visualiza a listagem de vagas
disponíveis (consumidas da API) e um formulário para submeter sua candidatura.
● Integração: A aplicação PHP não deve acessar o banco de dados diretamente; todas as
operações de leitura e escrita devem ser feitas através de requisições HTTP à API
Node.JS.
● Boas Práticas: É fundamental o uso dos conceitos de POO (encapsulamento, herança,
polimorfismo, separação de responsabilidades). O código deve ser organizado e limpo. A
não aplicação destes fundamentos possui caráter eliminatório.

nao crie nada complexo apenas o basico para eu entender e de forma simples nao quero extruturas dificies nem codigos complexos demais e faça por partes tudo sera commitado
