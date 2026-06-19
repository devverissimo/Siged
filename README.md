# SIGED v1.0 — Sistema de Gerenciamento de Estoque

> Projeto acadêmico desenvolvido para a disciplina de **Programação Orientada a Objetos com Banco de Dados** — PUC Goiás (CMP1611)

Sistema desktop em Java para controle de estoque de distribuidoras, com cadastro de produtos, categorias e usuários, registro de movimentações, dashboard com indicadores e log de auditoria.

---

## Funcionalidades

- **Login** com autenticação por usuário e senha
- **Dashboard** com indicadores: total de produtos, categorias, valor do estoque, estoque crítico e últimas movimentações
- **Produtos** — cadastro, edição, exclusão e busca por nome ou categoria
- **Categorias** — cadastro e gerenciamento
- **Movimentações** — registro de entradas e saídas com controle automático de estoque
- **Usuários** — cadastro com perfis ADMIN e OPERADOR
- **Log de Acesso** — histórico de login/logout por usuário
- **Exportação de relatório**

---

## Tecnologias

| Tecnologia | Versão |
|------------|--------|
| Java | JDK 11+ |
| Swing | Interface gráfica |
| MySQL | 8.x |
| JDBC | mysql-connector-j 9.7.0 |
| Eclipse IDE | 2023+ |

---

## Arquitetura

O projeto segue o padrão **MVC em três camadas**:

```
src/br/codrive/
├── Main.java              # Ponto de entrada
├── model/                 # Entidades (Produto, Usuario, Movimentacao...)
├── dao/                   # Acesso ao banco de dados (SQL / JDBC)
├── service/               # Regras de negócio e validações
├── view/                  # Telas (Swing)
└── util/                  # Utilitários (tema, formatação, validação)
```

---

## Como rodar

### Pré-requisitos

- Java JDK 11 ou superior instalado
- MySQL 8.x instalado e em execução
- Eclipse IDE

---

### 1. Criar o banco de dados

1. Abra o **MySQL Workbench** (ou outro cliente MySQL)
2. Conecte com seu usuário root
3. Abra o arquivo `BANCO_DADOS/script.sql`
4. Execute o script completo

O script cria o banco `siged_db`, todas as tabelas e insere dados de exemplo.

---

### 2. Configurar a conexão

Abra o arquivo `src/db.properties` e altere a senha para a do seu MySQL:

```properties
db.url=jdbc:mysql://localhost:3306/siged_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
db.user=root
db.password=SUA_SENHA_AQUI
```

---

### 3. Importar no Eclipse

1. `File` → `Import` → `Existing Projects into Workspace`
2. Selecione a pasta raiz do projeto
3. Clique em **Finish**

O `.classpath` já aponta automaticamente para o driver MySQL em `lib/`. Nenhuma configuração adicional é necessária.

---

### 4. Executar

Clique com o botão direito em `src/br/codrive/Main.java` → **Run As → Java Application**

---

## Credenciais de acesso (dados de exemplo)

| Campo | Valor |
|-------|-------|
| Login | `admin` |
| Senha | `admin123` |

---

## Estrutura de pastas

```
poo/
├── BANCO_DADOS/
│   └── script.sql          # Script completo do banco — rodar antes de tudo
├── lib/
│   └── mysql-connector-j-9.7.0.jar  # Driver JDBC (já incluído)
├── src/
│   └── br/codrive/         # Código-fonte
├── .classpath              # Configuração de build do Eclipse
├── .project                # Configuração do projeto Eclipse
└── README.md               # Este arquivo
```

---

## Desenvolvedoras

Projeto desenvolvido por **Maria** — PUC Goiás,2026.
