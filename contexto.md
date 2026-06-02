context.md
markdown# SIGED — Sistema de Gerenciamento de Estoque para Distribuidoras
## Produto: CoDrive

---

## Contexto do Projeto

Sistema desktop de gerenciamento de estoque desenvolvido como mini-projeto
acadêmico na PUC Goiás (disciplina CMP1611). O sistema deve ter cara de ERP
corporativo real — robusto, funcional, sem frescura de design moderno.

---

## Stack Obrigatória — SEM EXCEÇÕES

- **Linguagem**: Java SE (puro, sem frameworks)
- **Interface gráfica**: Java Swing — OBRIGATÓRIO. NÃO usar JavaFX, HTML,
  web, Electron ou qualquer outra coisa. TUDO é Swing: JFrame, JPanel,
  JTable, JTextField, JComboBox, JMenuBar, JTabbedPane, JButton.
- **Banco de dados**: MySQL
- **Conexão**: JDBC puro — NÃO usar Hibernate, JPA ou qualquer ORM
- **Arquitetura**: camadas separadas em pacotes (model / dao / service / view)
- **IDE alvo**: Eclipse (o professor vai importar e executar no Eclipse)

---

## Identidade Visual — Swing

O sistema deve parecer um ERP dos anos 2000 atualizado: pesado, sério,
funcional. Sem bordas arredondadas excessivas, sem animações, sem gradientes.

### Paleta de cores (usar via UIManager ou Color)
Principal (laranja queimado): #D97706  → botões primários, barra de título, acentos
Fundo geral:                  #F8FAFC  → background dos painéis
Cards / painéis internos:     #FFFFFF  → painéis de formulário
Texto principal:              #1F2937  → labels, dados
Bordas / separadores:         #E5E7EB  → bordas de tabelas, painéis
Barra de menu / sidebar:      #374151  → fundo escuro do menu e sidebar

### Elementos obrigatórios de aparência
- Barra de título da JFrame com nome completo: 
  "SIGED v1.0 — Sistema de Gerenciamento de Estoque para Distribuidoras [CoDrive]"
- JMenuBar escura (#374151) com itens: CADASTROS | MOVIMENTAÇÃO | LISTAGEM | USUÁRIOS | CONFIGURAÇÕES
- Toolbar com botões de ação no estilo ERP: [F2] NOVO | [F3] SALVAR | [F4] EDITAR | [F8] EXCLUIR | [F5] PESQUISAR
- Barra de status inferior (JLabel) mostrando: usuário logado | módulo ativo | status do banco | idioma
- Fonte monospace (Courier New ou Monospaced) nos campos de dados e tabelas
- Fonte sans-serif nos labels e menus
- JTable com cabeçalho em fundo #374151 e texto branco, linhas alternadas

---

## Entidades do Sistema

### Cadastro Básico — Categoria
categoria(id* INT AUTO_INCREMENT, nome VARCHAR(100) NOT NULL)

### Cadastro Intermediário — Produto
produto(id* INT AUTO_INCREMENT, nome VARCHAR(150) NOT NULL,
valor DECIMAL(10,2) NOT NULL, quantidade INT DEFAULT 0,
id_categoria INT — FK → categoria)
Regra: quantidade NÃO é editável manualmente. Só muda via Movimentação.

### Movimentação
movimentacao(id* INT AUTO_INCREMENT, tipo ENUM('ENTRADA','SAIDA'),
quantidade INT NOT NULL, data DATE NOT NULL,
observacao VARCHAR(255), id_produto INT — FK → produto)
Regra de negócio: ao registrar SAIDA, validar se quantidade disponível >= quantidade solicitada.
Ao salvar, atualizar automaticamente produto.quantidade via UPDATE.

### Usuário
usuario(id* INT AUTO_INCREMENT, nome VARCHAR(100), login VARCHAR(50) UNIQUE,
senha VARCHAR(255))

---

## Janelas do Sistema

### 1. TelaLogin
- JFrame simples, centralizada, sem menu
- Campos: login (JTextField), senha (JPasswordField)
- Botão ENTRAR valida contra banco. Se ok → abre TelaMenu e fecha TelaLogin
- Não permitir acesso sem login

### 2. TelaMenu (JFrame principal)
- JMenuBar escura com os módulos
- Toolbar com ações contextuais
- Sidebar esquerda com lista de módulos (JList ou JPanel com botões)
- Área central (JDesktopPane ou CardLayout) onde as sub-janelas abrem
- Barra de status inferior

### 3. TelaCategoria (JInternalFrame ou JPanel)
- Duas abas (JTabbedPane): [CADASTRO] e [PESQUISA]
- Aba CADASTRO: campos id (somente leitura), nome + botões NOVO/SALVAR/EDITAR/EXCLUIR
- Aba PESQUISA: JTextField para digitar + botão PESQUISAR + JTable com resultado
  - Pesquisa básica: aceita nome parcial (LIKE %termo%)
  - Clicar na linha da JTable preenche a aba CADASTRO

### 4. TelaProduto (JInternalFrame ou JPanel)
- Duas abas: [CADASTRO] e [PESQUISA]
- Aba CADASTRO: id (somente leitura), nome, JComboBox de categoria, valor,
  quantidade (somente leitura), botões NOVO/SALVAR/EDITAR/EXCLUIR
- Aba PESQUISA COMPLEXA:
  - JComboBox com opções: [POR CÓDIGO | POR NOME | POR CATEGORIA]
  - JTextField para digitar o valor de busca
  - Validação: se POR CÓDIGO selecionado e usuário digitar letra → mostrar erro
  - JTable com colunas: CÓD. | NOME | CATEGORIA | VALOR | QTD.
  - Clicar na linha preenche aba CADASTRO e ativa essa aba

### 5. TelaMovimentacao
- Campos: JComboBox de produto (mostra nome + estoque atual), tipo (JRadioButton ENTRADA/SAÍDA),
  quantidade (JTextField numérico), data (JTextField no formato DD/MM/AAAA), observação
- Ao selecionar produto no combobox → exibir quantidade atual em label
- Validar: SAÍDA não pode ser maior que estoque disponível
- Botão REGISTRAR → INSERT na movimentação + UPDATE na quantidade do produto (em transação)
- Exibir histórico das últimas movimentações em JTable abaixo

### 6. TelaListagem
- JComboBox com opções de ordenação: [POR DATA ↑ | POR DATA ↓ | POR PRODUTO A-Z | POR TIPO]
- JTable com colunas: DATA | PRODUTO | TIPO | QTD. | OBSERVAÇÃO
- Pelo menos 2 ordenações diferentes
- Botão EXPORTAR (opcional)

### 7. TelaUsuario
- CRUD completo de usuários
- Pesquisa por login ou nome
- Validação: login duplicado não pode ser cadastrado

---

## Arquitetura em Camadas (pacotes)
br.codrive.model      → Categoria.java, Produto.java, Movimentacao.java, Usuario.java
br.codrive.dao        → ConnectionFactory.java, CategoriaDAO.java, ProdutoDAO.java,
MovimentacaoDAO.java, UsuarioDAO.java
br.codrive.service    → CategoriaService.java, ProdutoService.java,
MovimentacaoService.java, UsuarioService.java
br.codrive.view       → TelaLogin.java, TelaMenu.java, TelaCategoria.java,
TelaProduto.java, TelaMovimentacao.java, TelaListagem.java,
TelaUsuario.java
br.codrive.util       → Validador.java, Formatador.java, Mensagem.java
br.codrive.i18n       → messages_pt_BR.properties, messages_en.properties

---

## Internacionalização (i18n)

- Usar ResourceBundle com arquivos .properties
- Janela de configuração permite trocar entre PT-BR e EN
- Strings de interface devem vir dos arquivos de propriedades, não hardcoded
- Exemplo:
  - messages_pt_BR.properties: `btn.salvar=SALVAR`, `lbl.nome=NOME`
  - messages_en.properties: `btn.salvar=SAVE`, `lbl.nome=NAME`

---

## Regras de Validação

- Campos numéricos (valor, quantidade): usar DocumentFilter ou KeyListener para
  bloquear caracteres não numéricos e exibir JOptionPane de erro
- Campos obrigatórios: validar antes de qualquer INSERT/UPDATE
- Duplicidade: verificar antes de inserir (SELECT EXISTS antes do INSERT)
- Mensagens de erro: sempre via JOptionPane.showMessageDialog com ícone WARNING
- Mensagens de sucesso: JOptionPane com ícone INFORMATION

---

## Cabeçalho obrigatório em cada classe

```java
/**
 * Classe: NomeDaClasse
 * Objetivo: Descrição em até 3 linhas do propósito desta classe.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
```

---

## Script do Banco (MySQL)

O script deve:
- Criar o banco: `CREATE DATABASE IF NOT EXISTS siged_db;`
- Criar todas as tabelas com chaves primárias AUTO_INCREMENT e FK com ON DELETE RESTRICT
- Inserir dados de teste coerentes com uma distribuidora de eletrônicos:
  - Categorias: Notebooks, Smartphones, Periféricos, Monitores, Tablets
  - Produtos: pelo menos 8 produtos reais com valores e quantidades
  - Movimentações: pelo menos 10 registros misturando ENTRADA e SAIDA
  - Usuário padrão: login=admin / senha=admin123

---

## Entrega

Estrutura de pastas no ZIP:
CMP1611-MATRICULA-MARIA RITA VERISSIMO.zip
├── OPROJETO/          → código fonte Eclipse (src/, lib/, .classpath, .project)
└── BANCO_DADOS/       → script.sql gerado pelo HeidiSQL com dados de teste

