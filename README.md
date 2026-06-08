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
| 3 | Buscar contato por parte do nome (busca parcial) |
| 4 | Editar contato existente |
| 5 | Excluir contato com confirmação antes de deletar |
| 6 | Exportar todos os contatos para arquivo `.txt` |
| 0 | Sair |

**Diferenciais implementados:**
- Busca parcial com `LIKE` no SQL — não precisa digitar o nome completo
- Listagem em ordem alfabética (`ORDER BY nome ASC`)
- Confirmação obrigatória antes de excluir
- Exportação para `.txt` formatado (bloqueia se a agenda estiver vazia)
- Validação de telefone (mínimo 8 dígitos, caracteres permitidos)
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

Abra o arquivo `src/agenda/dao/ConnectionFactory.java` e ajuste a senha na constante:

```java
private static final String SENHA = "sua_senha_aqui";
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

**Adicionar contato (opção 1)**
Digite nome, telefone (ex: `(62) 99000-0000`) e e-mail opcional.
O sistema bloqueia nomes duplicados, telefones inválidos e e-mails com formato errado.

**Listar contatos (opção 2)**
Exibe todos os contatos em ordem alfabética, com ID, nome, telefone e e-mail.

**Buscar por nome (opção 3)**
Digite apenas parte do nome. Exemplo: digitando `ar` encontra `Lara`, `Marcio`, etc.

**Editar contato (opção 4)**
Busque pelo ID ou parte do nome. Pressione Enter nos campos que não quiser alterar.

**Excluir contato (opção 5)**
Busque o contato e confirme com `s` quando perguntado. Qualquer outra tecla cancela.

**Exportar para .txt (opção 6)**
Gera o arquivo `contatos.txt` na pasta onde o programa está rodando. Se a agenda estiver vazia, avisa e não cria o arquivo.

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
