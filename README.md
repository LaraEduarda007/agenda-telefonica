# Agenda Telefônica

**Projeto Integrador II A — Análise e Desenvolvimento de Sistemas EaD**  
**PUC Goiás · Professor: José Ricardo Cosme Lérias Ribeiro**  
**Aluna: Lara Eduarda**

Aplicação de console em Java para gerenciar contatos telefônicos, com persistência em banco de dados MySQL via JDBC.

---

## Como o sistema funciona

A aplicação roda no terminal e apresenta um menu numerado. O usuário escolhe uma opção digitando o número e pressionando Enter.

O sistema é organizado em três camadas:

- **model** (`Contato.java`) — representa um contato com os campos: ID, nome, telefone e e-mail.
- **dao** (`ContatoDAO.java`) — é a camada que conversa com o banco de dados. Toda operação SQL (inserir, listar, buscar, editar, excluir, exportar) está centralizada aqui.
- **service** (`AgendaTelefonica.java`) — é o menu interativo. Recebe a entrada do usuário, chama o DAO e exibe o resultado na tela com mensagens coloridas.

O fluxo de uma operação típica é:

```
Usuário digita no terminal
    → AgendaTelefonica (menu) processa a entrada
        → ContatoDAO executa o SQL no MySQL
            → resultado volta para a tela
```

A conexão com o banco é gerenciada pela `ConnectionFactory.java`, usando JDBC. Cada operação abre e fecha a conexão automaticamente com `try-with-resources`.

---

## Funcionalidades

| Opção | O que faz |
|-------|-----------|
| 1 | Adicionar novo contato (nome, telefone, e-mail opcional) |
| 2 | Listar todos os contatos em ordem alfabética |
| 3 | Buscar contato por nome, telefone ou e-mail |
| 4 | Editar contato existente |
| 5 | Excluir contato com confirmação antes de deletar |
| 6 | Exportar todos os contatos para arquivo `.txt` |
| 0 | Sair |

**Diferenciais implementados:**
- Busca por qualquer campo com `LIKE` — nome, telefone ou e-mail, sem precisar digitar completo
- Listagem em ordem alfabética (`ORDER BY nome ASC`)
- Reutilização de IDs: ao excluir o contato de ID 3, o próximo cadastro recebe o ID 3
- Formatação automática de telefone: digitar `62990001111` vira `(62) 99000-1111`
- Validação de telefone por quantidade de dígitos: 10 (fixo) ou 11 (celular), não avança se errado
- Confirmação obrigatória antes de excluir
- Quando dois contatos têm a mesma informação (ex: mesmo e-mail), o sistema lista os dois e pede para escolher
- Exportação para `.txt` formatado (bloqueia se a agenda estiver vazia)
- Validação de e-mail (formato `nome@dominio.com`)
- Interface colorida no terminal com cores ANSI

---

## Pré-requisitos

- [Java JDK 11 ou superior](https://www.oracle.com/java/technologies/downloads/)
- [MySQL Server 8.x](https://dev.mysql.com/downloads/mysql/)
- [MySQL Connector/J 9.7.0](https://dev.mysql.com/downloads/connector/j/) — driver JDBC (arquivo `mysql-connector-j-9.7.0.jar`)

---

## Passo a passo para configurar e rodar

### 1. Clonar o repositório

```bash
git clone https://github.com/LaraEduarda007/agenda-telefonica.git
cd agenda-telefonica
```

### 2. Adicionar o driver JDBC

Baixe o arquivo `mysql-connector-j-9.7.0.jar` no [site oficial](https://dev.mysql.com/downloads/connector/j/) e coloque dentro da pasta `lib/` do projeto.

### 3. Criar o banco de dados

Abra o terminal e conecte ao MySQL:

```bash
mysql -u root -p
```

Depois execute o script do projeto:

```sql
source database/agenda.sql;
```

Isso cria o banco `agenda`, a tabela `contatos` e insere 4 contatos de exemplo prontos para teste.

### 4. Configurar a senha do banco

A senha padrão configurada no projeto é `root1234`. Se a senha do seu MySQL for diferente, abra o arquivo `src/agenda/dao/ConnectionFactory.java` e altere a constante:

```java
private static final String SENHA = "root1234"; // troque pela sua senha do MySQL
```

> O usuário padrão configurado é `root` e o banco é `agenda` na porta `3306`. Altere se necessário.

### 5. Compilar e executar

Este projeto foi desenvolvido usando o **terminal integrado do VS Code**. É a forma mais simples de rodar.

**Abra o terminal do VS Code** (`Ctrl + '`) com a pasta do projeto aberta e rode:

```
.\compilar.bat
.\rodar.bat
```

**Ou, se preferir pelo terminal do Windows (PowerShell/CMD)** na pasta do projeto:

```
compilar.bat
rodar.bat
```

---

## Erros comuns e como resolver

**`Public Key Retrieval is not allowed`**
Ocorre com MySQL 8.x. Já está corrigido na URL de conexão do projeto com o parâmetro `allowPublicKeyRetrieval=true`. Se aparecer, verifique se o arquivo `ConnectionFactory.java` contém esse parâmetro na URL.

**`Access denied for user 'root'@'localhost'`**
A senha configurada em `ConnectionFactory.java` não corresponde à senha do seu MySQL. Abra o arquivo e corrija a constante `SENHA`.

**`Communications link failure` ou `Connection refused`**
O MySQL não está rodando. Inicie o serviço do MySQL pelo Gerenciador de Serviços do Windows ou via terminal:
```bash
net start mysql
```

**`No suitable driver found`**
O arquivo `.jar` do conector JDBC não foi encontrado. Confirme que `mysql-connector-j-9.7.0.jar` está dentro da pasta `lib/` e que você compilou com o `compilar.bat`.

**`Unknown database 'agenda'`**
O banco ainda não foi criado. Execute o script SQL conforme o passo 3 acima.

**`Table 'agenda.contatos' doesn't exist`**
A tabela foi removida ou o script não foi executado corretamente. Rode o `database/agenda.sql` novamente no MySQL.

---

## Como testar cada funcionalidade

O banco já vem com 4 contatos de exemplo após executar o `agenda.sql` (Marcio, Lara, Teteu e Benito). Os testes abaixo podem ser feitos em sequência.

---

### Opção 1 — Adicionar contato

**Teste 1: telefone só com dígitos (formatação automática)**
- Nome: `Ana Paula`
- Telefone: `62991112222`
- O sistema formata automaticamente para `(62) 99111-2222` antes de salvar.

**Teste 2: telefone com dígitos de menos**
- Telefone: `1234`
- O sistema avisa que precisa de 10 ou 11 dígitos e pede novamente — não avança.

**Teste 3: e-mail inválido**
- E-mail: `ana.gmail`
- O sistema rejeita e pede de novo. Pressionar Enter pula o e-mail (campo opcional).

---

### Opção 2 — Listar contatos

Exibe todos os contatos em ordem alfabética (A → Z), com ID, nome, telefone e e-mail.

---

### Opção 3 — Buscar contato

A busca funciona por **qualquer campo**: nome, telefone ou e-mail, sem precisar digitar completo.

**Teste 1: busca por parte do nome**
- Digite `ar` → encontra `Lara` e `Marcio`.

**Teste 2: busca por parte do telefone**
- Digite `9600` → encontra o contato que tem esse trecho no telefone.

**Teste 3: busca por parte do e-mail**
- Digite `@email` → lista todos que têm e-mail cadastrado.

**Teste 4: busca sem resultado**
- Digite `zzzzz` → informa que nenhum contato foi encontrado.

---

### Opção 4 — Editar contato

A busca para editar também funciona por qualquer campo (nome, telefone ou e-mail).

**Teste 1: busca por telefone**
- Digite `98002` → localiza a Lara e abre para edição.
- Pressione Enter em cada campo para manter o valor atual.

**Teste 2: validação de telefone no editar**
- No campo telefone, digite `123` → o sistema avisa que precisa de 10 ou 11 dígitos e **não avança** para o próximo campo até corrigir.
- Digite `62991234567` → formata para `(62) 99123-4567` e aceita.

**Teste 3: dois contatos com a mesma informação**
- Adicione dois contatos com o mesmo e-mail (ex: `teste@email.com`).
- Ao editar, busque por `teste@email` → o sistema lista os dois e pede para escolher o ID do que deseja editar.

---

### Opção 5 — Excluir contato e reutilização de ID

**Teste 1: confirmação antes de excluir**
- Busque qualquer contato e quando aparecer `Confirma a exclusão? (s/N):`
- Digite `n` → operação cancelada, ninguém é excluído.

**Teste 2: reutilização de ID**
- Exclua o contato de ID 2 (opção 5).
- Adicione um novo contato (opção 1).
- Liste (opção 2) → o novo contato recebe o **ID 2**, não um número novo no fim da lista.

---

### Opção 6 — Exportar para .txt

Gera o arquivo `contatos.txt` na mesma pasta onde o programa está rodando.

**Teste 1: exportação normal**
- Com contatos cadastrados, escolha a opção 6. O arquivo é criado e o sistema informa o caminho.

**Teste 2: agenda vazia**
- Exclua todos os contatos e tente exportar → o sistema avisa que não há contatos e não cria o arquivo.

---

## Estrutura do projeto

```
agenda-telefonica/
├── database/
│   └── agenda.sql              # Script SQL: cria banco, tabela e dados de exemplo
├── lib/
│   └── mysql-connector-j.jar   # Driver JDBC (adicionar manualmente)
├── src/
│   └── agenda/
│       ├── Cores.java              # Utilitário de cores ANSI para o terminal
│       ├── AgendaTeste.java        # Ponto de entrada — método main()
│       ├── model/
│       │   └── Contato.java        # Entidade contato (id, nome, telefone, email)
│       ├── dao/
│       │   ├── ConnectionFactory.java  # Gerencia a conexão com o MySQL
│       │   └── ContatoDAO.java         # CRUD completo no banco de dados
│       └── service/
│           └── AgendaTelefonica.java   # Menu interativo e lógica de entrada
├── compilar.bat                # Script de compilação (Windows)
└── rodar.bat                   # Script de execução (Windows)
```

---

## Tecnologias utilizadas

- Java 17
- JDBC (Java Database Connectivity)
- MySQL 8.x
- Padrão de projeto: DAO (Data Access Object)
- Arquitetura em camadas: model / dao / service
