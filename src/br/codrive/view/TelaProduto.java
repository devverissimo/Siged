/**
 * Classe: TelaProduto
 * Objetivo: JInternalFrame de cadastro e pesquisa de produtos. Aba CADASTRO
 *           exibe CRUD com JComboBox de categoria e quantidade somente leitura.
 *           Aba PESQUISA oferece busca por código, nome ou categoria com JTable.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Categoria;
import br.codrive.model.Produto;
import br.codrive.service.CategoriaService;
import br.codrive.service.ProdutoService;
import br.codrive.util.AppTheme;
import br.codrive.util.Formatador;
import br.codrive.util.Mensagem;
import br.codrive.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class TelaProduto extends JInternalFrame implements ModuloAcoes {

    // Aba CADASTRO
    private JTextField           campoId, campoNome, campoValor, campoQuantidade;
    private JComboBox<Categoria> comboCategoria;
    private JButton              btnNovo, btnSalvar, btnCancelar;
    private JPanel               painelConteudo;

    // Aba PESQUISA
    private JComboBox<String>  comboTipoBusca;
    private JTextField         campoBusca;
    private JTable             tabela;
    private DefaultTableModel  modeloTabela;
    private JTabbedPane        abas;
    private JButton            btnEditarPesquisa, btnExcluirPesquisa;

    // Estado
    private Produto produtoAtual = null;

    private final ProdutoService   produtoService   = new ProdutoService();
    private final CategoriaService categoriaService = new CategoriaService();

    public TelaProduto() {
        super(Mensagem.get("titulo.produto"), true, true, true, true);
        setSize(720, 500);
        setMinimumSize(new Dimension(600, 440));
        construirInterface();
        definirEstadoInicial();
    }

    // -------------------------------------------------------------------------
    // Construção da interface
    // -------------------------------------------------------------------------

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(AppTheme.COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JPanel abaCadastroPanel = construirAbaCadastro();
        JPanel abaPesquisaPanel = construirAbaPesquisa();

        JPanel barraAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barraAcoes.setBackground(new Color(0xF3, 0xF4, 0xF6));
        barraAcoes.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.COR_BORDA));
        barraAcoes.add(btnNovo);
        barraAcoes.add(btnSalvar);
        barraAcoes.add(btnCancelar);
        barraAcoes.add(btnEditarPesquisa);
        barraAcoes.add(btnExcluirPesquisa);
        btnEditarPesquisa.setVisible(false);
        btnExcluirPesquisa.setVisible(false);

        abas = new JTabbedPane();
        abas.add(Mensagem.get("tab.cadastro"), abaCadastroPanel);
        abas.add(Mensagem.get("tab.pesquisa"), abaPesquisaPanel);

        abas.addChangeListener(e -> {
            boolean cadastro = abas.getSelectedIndex() == 0;
            btnNovo.setVisible(cadastro);
            btnSalvar.setVisible(cadastro);
            btnCancelar.setVisible(cadastro);
            if (!cadastro) {
                btnEditarPesquisa.setEnabled(false);
                btnExcluirPesquisa.setEnabled(false);
                btnEditarPesquisa.setVisible(true);
                btnExcluirPesquisa.setVisible(true);
                campoBusca.requestFocus();
            } else {
                btnEditarPesquisa.setVisible(false);
                btnExcluirPesquisa.setVisible(false);
            }
        });

        raiz.add(barraAcoes, BorderLayout.NORTH);
        raiz.add(abas,       BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel construirAbaCadastro() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel card = AppTheme.criarPainelCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);

        campoId       = campoReadOnly(8);
        Validador.aplicarFiltroInteiro(campoId);
        campoNome     = new JTextField(30);
        comboCategoria = new JComboBox<>();
        comboCategoria.setFont(AppTheme.FONTE_DADOS);
        campoValor    = new JTextField(12);
        Validador.aplicarFiltroDecimal(campoValor);
        campoQuantidade = campoReadOnly(10);

        addFormRow(card, g, 0, Mensagem.get("lbl.codigo")     + ":", campoId);
        addFormRow(card, g, 1, Mensagem.get("lbl.nome")       + ":", campoNome);
        addFormRow(card, g, 2, Mensagem.get("lbl.categoria")  + ":", comboCategoria);
        addFormRow(card, g, 3, Mensagem.get("lbl.valor")      + ":", campoValor);
        addFormRow(card, g, 4, Mensagem.get("lbl.quantidade") + ":", campoQuantidade);

        campoNome.addActionListener(e -> { if (btnSalvar.isEnabled()) acaoSalvar(); });

        painelConteudo = new JPanel(new CardLayout());
        painelConteudo.add(criarPainelOrientativo(), "ORIENTATIVO");
        painelConteudo.add(card,                     "FORMULARIO");
        painel.add(painelConteudo, BorderLayout.CENTER);

        btnNovo     = new JButton(Mensagem.get("btn.novo"));
        btnSalvar   = new JButton(Mensagem.get("btn.salvar"));
        btnCancelar = new JButton("CANCELAR");

        AppTheme.estilizarBotaoToolbar(btnNovo);
        AppTheme.estilizarBotaoPrimario(btnSalvar);
        AppTheme.estilizarBotaoSecundario(btnCancelar);

        btnNovo.addActionListener(e    -> acaoNovo());
        btnSalvar.addActionListener(e  -> acaoSalvar());
        btnCancelar.addActionListener(e -> definirEstadoInicial());

        return painel;
    }

    private JPanel construirAbaPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new BorderLayout(6, 0));
        painelBusca.setBackground(AppTheme.COR_FUNDO);

        comboTipoBusca = new JComboBox<>(new String[]{
            Mensagem.get("busca.por.codigo"),
            Mensagem.get("busca.por.nome"),
            Mensagem.get("busca.por.categoria")
        });
        comboTipoBusca.setFont(AppTheme.FONTE_DADOS);
        comboTipoBusca.setPreferredSize(
            new Dimension(160, comboTipoBusca.getPreferredSize().height));

        campoBusca = new JTextField();
        JButton btnPesquisar = new JButton(Mensagem.get("btn.pesquisar"));
        AppTheme.estilizarBotaoPrimario(btnPesquisar);

        campoBusca.addActionListener(e   -> executarBusca());
        btnPesquisar.addActionListener(e -> executarBusca());

        painelBusca.add(comboTipoBusca, BorderLayout.WEST);
        painelBusca.add(campoBusca,     BorderLayout.CENTER);
        painelBusca.add(btnPesquisar,   BorderLayout.EAST);

        modeloTabela = new DefaultTableModel(new String[]{
            Mensagem.get("tabela.codigo"),
            Mensagem.get("tabela.nome"),
            Mensagem.get("tabela.categoria"),
            Mensagem.get("tabela.valor"),
            Mensagem.get("tabela.quantidade")
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0 || c == 4) ? Integer.class : String.class;
            }
        };

        tabela = new JTable(modeloTabela);
        AppTheme.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(70);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(4).setMaxWidth(65);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) selecionarDaTabela();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA));

        btnEditarPesquisa  = new JButton(Mensagem.get("btn.editar"));
        btnExcluirPesquisa = new JButton(Mensagem.get("btn.excluir"));
        AppTheme.estilizarBotaoPrimario(btnEditarPesquisa);
        AppTheme.estilizarBotaoDestrutivo(btnExcluirPesquisa);
        btnEditarPesquisa.setEnabled(false);
        btnExcluirPesquisa.setEnabled(false);
        btnEditarPesquisa.addActionListener(e  -> editarSelecionado());
        btnExcluirPesquisa.addActionListener(e -> excluirSelecionado());

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(scroll,      BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------------------------
    // Carga de dados
    // -------------------------------------------------------------------------

    private void carregarCategorias() {
        comboCategoria.removeAllItems();
        comboCategoria.addItem(new Categoria(0, "-- " + Mensagem.get("lbl.categoria") + " --"));
        try {
            categoriaService.listarTodos().forEach(comboCategoria::addItem);
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar categorias:\n" + ex.getMessage());
        }
    }

    private void preencherTabela(List<Produto> lista) {
        modeloTabela.setRowCount(0);
        for (Produto p : lista) {
            modeloTabela.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getNomeCategoria() != null ? p.getNomeCategoria() : "-",
                Formatador.formatarMoeda(p.getValor()),
                p.getQuantidade()
            });
        }
    }

    // -------------------------------------------------------------------------
    // Controle de estado
    // -------------------------------------------------------------------------

    private void definirEstadoInicial() {
        campoId.setText("");
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        campoNome.setText("");
        campoValor.setText("");
        campoQuantidade.setText("");
        comboCategoria.setSelectedIndex(0);
        produtoAtual = null;
        setFormEnabled(false);
        btnNovo.setEnabled(true);
        btnSalvar.setEnabled(false);
        btnCancelar.setEnabled(false);
        ((CardLayout) painelConteudo.getLayout()).show(painelConteudo, "ORIENTATIVO");
    }

    private void preencherFormulario(Produto p) {
        ((CardLayout) painelConteudo.getLayout()).show(painelConteudo, "FORMULARIO");
        produtoAtual = p;
        campoId.setText(String.valueOf(p.getId()));
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        campoNome.setText(p.getNome());
        campoValor.setText(String.format(Locale.US, "%.2f", p.getValor()));
        campoQuantidade.setText(String.valueOf(p.getQuantidade()));
        selecionarCategoria(p.getIdCategoria());
        setFormEnabled(false);
        btnSalvar.setEnabled(false);
        btnCancelar.setEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        campoNome.setEnabled(enabled);
        campoValor.setEnabled(enabled);
        comboCategoria.setEnabled(enabled);
        // campoQuantidade NUNCA habilitado — alterado apenas via Movimentação
    }

    private void selecionarCategoria(int idCategoria) {
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).getId() == idCategoria) {
                comboCategoria.setSelectedIndex(i);
                return;
            }
        }
        comboCategoria.setSelectedIndex(0);
    }

    // -------------------------------------------------------------------------
    // Implementação de ModuloAcoes — delegação da toolbar de TelaMenu
    // -------------------------------------------------------------------------

    @Override
    public void acaoNovo() {
        definirEstadoInicial();
        carregarCategorias();
        campoId.setEditable(true);
        campoId.setBackground(AppTheme.COR_PAINEL);
        setFormEnabled(true);
        campoId.requestFocus();
        btnNovo.setEnabled(false);
        btnSalvar.setEnabled(true);
        btnCancelar.setEnabled(true);
        ((CardLayout) painelConteudo.getLayout()).show(painelConteudo, "FORMULARIO");
    }

    @Override
    public void acaoSalvar() {
        double valor;
        try {
            valor = Formatador.parseDecimal(campoValor.getText());
        } catch (NumberFormatException ex) {
            Mensagem.erro(this, "O campo VALOR é obrigatório e deve ser um número válido.");
            return;
        }

        Categoria catSel = (Categoria) comboCategoria.getSelectedItem();
        int idCat = (catSel != null) ? catSel.getId() : 0;

        try {
            Produto p = (produtoAtual != null) ? produtoAtual : new Produto();
            p.setNome(campoNome.getText());
            p.setValor(valor);
            p.setIdCategoria(idCat);

            if (produtoAtual == null) {
                String idTexto = campoId.getText().trim();
                if (!idTexto.isEmpty()) p.setId(Integer.parseInt(idTexto));
                int idGerado = produtoService.inserir(p);
                p.setId(idGerado);
                Mensagem.sucesso(this, "Produto cadastrado com sucesso!");
            } else {
                produtoService.atualizar(p);
                Mensagem.sucesso(this, "Produto atualizado com sucesso!");
            }

            definirEstadoInicial();

        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao salvar produto:\n" + ex.getMessage());
        }
    }

    @Override public void acaoEditar()  { editarSelecionado();  }
    @Override public void acaoExcluir() { excluirSelecionado(); }

    @Override
    public void acaoPesquisar() {
        abas.setSelectedIndex(1);
        campoBusca.requestFocus();
    }

    // -------------------------------------------------------------------------
    // Ações internas
    // -------------------------------------------------------------------------

    private void executarBusca() {
        int    tipo  = comboTipoBusca.getSelectedIndex();
        String termo = campoBusca.getText().trim();

        if (tipo == 0 && !termo.isEmpty() && !Validador.isInteiroValido(termo)) {
            Mensagem.erro(this, "Para busca " + Mensagem.get("busca.por.codigo")
                + ", informe apenas números inteiros.");
            return;
        }

        try {
            List<Produto> lista;
            switch (tipo) {
                case 0:
                    if (termo.isEmpty()) lista = produtoService.listarTodos();
                    else {
                        Produto p = produtoService.buscarPorId(Integer.parseInt(termo));
                        lista = (p != null) ? List.of(p) : List.of();
                    }
                    break;
                case 2:
                    lista = produtoService.buscarPorNomeCategoria(termo);
                    break;
                default:
                    lista = produtoService.buscarPorNome(termo);
            }
            preencherTabela(lista);
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro na pesquisa:\n" + ex.getMessage());
        }
    }

    private void selecionarDaTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) modeloTabela.getValueAt(row, 0);
        try {
            Produto p = produtoService.buscarPorId(id);
            if (p != null) {
                produtoAtual = p;
                btnEditarPesquisa.setEnabled(true);
                btnExcluirPesquisa.setEnabled(true);
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar produto:\n" + ex.getMessage());
        }
    }

    private void editarSelecionado() {
        if (produtoAtual == null) return;
        preencherFormulario(produtoAtual);
        setFormEnabled(true);
        campoNome.requestFocus();
        btnNovo.setEnabled(false);
        btnSalvar.setEnabled(true);
        btnCancelar.setEnabled(true);
        abas.setSelectedIndex(0);
    }

    private void excluirSelecionado() {
        if (produtoAtual == null) return;
        if (!Mensagem.confirmar(this, Mensagem.get("confirm.excluir"))) return;
        try {
            produtoService.excluir(produtoAtual.getId());
            Mensagem.sucesso(this, "Produto excluído com sucesso!");
            produtoAtual = null;
            btnEditarPesquisa.setEnabled(false);
            btnExcluirPesquisa.setEnabled(false);
            executarBusca();
            definirEstadoInicial();
        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao excluir produto:\n" + ex.getMessage());
        }
    }

    /** Chamado pela topbar de TelaMenu para filtrar produtos na aba PESQUISA. */
    public void pesquisarDireto(String termo) {
        campoBusca.setText(termo);
        comboTipoBusca.setSelectedIndex(Validador.isInteiroValido(termo.trim()) ? 0 : 1);
        abas.setSelectedIndex(1);
        executarBusca();
        if (modeloTabela.getRowCount() == 0) {
            Mensagem.erro(this, "Nenhum produto encontrado para: " + termo);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private JTextField campoReadOnly(int colunas) {
        JTextField c = new JTextField(colunas);
        c.setEditable(false);
        c.setBackground(AppTheme.COR_LINHA_PAR);
        return c;
    }

    private void addFormRow(JPanel card, GridBagConstraints g, int row,
                            String labelText, JComponent campo) {
        g.gridx = 0; g.gridy = row;
        g.fill = GridBagConstraints.NONE; g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AppTheme.FONTE_BOLD);
        card.add(lbl, g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        card.add(campo, g);
    }

    private JPanel criarPainelOrientativo() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppTheme.COR_FUNDO);
        JLabel lbl = new JLabel("Clique em NOVO para iniciar um cadastro");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(0x9C, 0xA3, 0xAF));
        p.add(lbl);
        return p;
    }
}
