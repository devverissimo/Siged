/**
 * Classe: TelaMovimentacao
 * Objetivo: JInternalFrame para registro de entradas e saídas de produtos.
 *           Exibe JComboBox com estoque atual, JRadioButtons de tipo, campos
 *           de quantidade e data, e histórico das últimas movimentações.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Movimentacao;
import br.codrive.model.Produto;
import br.codrive.service.MovimentacaoService;
import br.codrive.service.ProdutoService;
import br.codrive.util.AppTheme;
import br.codrive.util.Formatador;
import br.codrive.util.Mensagem;
import br.codrive.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.util.List;

public class TelaMovimentacao extends JInternalFrame {

    // Formulário
    private JComboBox<Produto> comboProduto;
    private JRadioButton       rdEntrada, rdSaida;
    private JTextField         campoQuantidade, campoData, campoObservacao;
    private JLabel             lblEstoqueAtual;
    private JButton            btnRegistrar;

    // Histórico
    private JTable            tabelaHistorico;
    private DefaultTableModel modeloHistorico;

    private final MovimentacaoService movService     = new MovimentacaoService();
    private final ProdutoService      produtoService = new ProdutoService();

    // Produto placeholder — id=0, nunca enviado ao banco
    private static final Produto SELECIONE = new Produto(0, "-- Selecione um produto --", 0, 0, 0);

    public TelaMovimentacao() {
        super(Mensagem.get("titulo.movimentacao"), true, true, true, true);
        setSize(780, 600);
        setMinimumSize(new Dimension(640, 500));
        construirInterface();
        carregarProdutos();
        carregarHistorico();
    }

    // -------------------------------------------------------------------------
    // Construção da interface
    // -------------------------------------------------------------------------

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBackground(AppTheme.COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        raiz.add(construirPainelFormulario(), BorderLayout.NORTH);
        raiz.add(construirPainelHistorico(),  BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private JPanel construirPainelFormulario() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(AppTheme.COR_FUNDO);

        // --- Card do formulário ---
        JPanel card = AppTheme.criarPainelCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        // --- Linha 0: PRODUTO ---
        comboProduto = new JComboBox<>();
        comboProduto.setFont(AppTheme.FONTE_DADOS);
        comboProduto.setRenderer(rendererProduto());
        comboProduto.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) atualizarEstoque();
        });

        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        card.add(lbl("lbl.produto.label", "PRODUTO:"), g);
        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        card.add(comboProduto, g);

        // --- Linha 1: TIPO + ESTOQUE ATUAL ---
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        g.gridx = 0; g.gridy = 1;
        card.add(lbl("lbl.tipo.label", "TIPO:"), g);

        ButtonGroup grupo = new ButtonGroup();
        rdEntrada = radio(Mensagem.get("tipo.entrada"), grupo, true);
        rdSaida   = radio(Mensagem.get("tipo.saida"),   grupo, false);

        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        painelTipo.setBackground(AppTheme.COR_PAINEL);
        painelTipo.add(rdEntrada);
        painelTipo.add(rdSaida);

        g.gridx = 1;
        card.add(painelTipo, g);

        g.gridx = 2; g.anchor = GridBagConstraints.EAST;
        JLabel lblEstLabel = new JLabel(Mensagem.get("mov.estoque.label"));
        lblEstLabel.setFont(AppTheme.FONTE_BOLD);
        card.add(lblEstLabel, g);

        lblEstoqueAtual = new JLabel("--");
        lblEstoqueAtual.setFont(AppTheme.FONTE_BOLD);
        lblEstoqueAtual.setForeground(AppTheme.COR_PRIMARIA);
        g.gridx = 3; g.anchor = GridBagConstraints.WEST;
        card.add(lblEstoqueAtual, g);

        // --- Linha 2: QUANTIDADE + DATA ---
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        card.add(lbl("lbl.quantidade.label", "QUANTIDADE:"), g);

        campoQuantidade = new JTextField(8);
        Validador.aplicarFiltroInteiro(campoQuantidade);
        g.gridx = 1;
        card.add(campoQuantidade, g);

        g.gridx = 2; g.anchor = GridBagConstraints.EAST;
        card.add(lbl("lbl.data.label", "DATA (DD/MM/AAAA):"), g);

        campoData = new JTextField(10);
        campoData.setText(Formatador.dataHoje());
        g.gridx = 3; g.anchor = GridBagConstraints.WEST; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 0.3;
        card.add(campoData, g);

        // --- Linha 3: OBSERVAÇÃO ---
        g.fill = GridBagConstraints.NONE; g.weightx = 0; g.anchor = GridBagConstraints.WEST;
        g.gridx = 0; g.gridy = 3;
        card.add(lbl("lbl.observacao.label", "OBSERVAÇÃO:"), g);

        campoObservacao = new JTextField(40);
        g.gridx = 1; g.gridwidth = 3; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        card.add(campoObservacao, g);

        painel.add(card, BorderLayout.CENTER);

        // --- Botão REGISTRAR ---
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        painelBotao.setBackground(AppTheme.COR_FUNDO);

        btnRegistrar = new JButton(Mensagem.get("btn.registrar"));
        btnRegistrar.setPreferredSize(new Dimension(160, 34));
        AppTheme.estilizarBotaoPrimario(btnRegistrar);
        btnRegistrar.addActionListener(e -> acaoRegistrar());
        campoQuantidade.addActionListener(e -> acaoRegistrar());

        painelBotao.add(btnRegistrar);
        painel.add(painelBotao, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel construirPainelHistorico() {
        JPanel painel = new JPanel(new BorderLayout(0, 4));
        painel.setBackground(AppTheme.COR_FUNDO);

        JLabel lblTitulo = new JLabel(Mensagem.get("mov.historico.titulo"));
        lblTitulo.setFont(AppTheme.FONTE_BOLD);
        lblTitulo.setForeground(AppTheme.COR_TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 0));

        modeloHistorico = new DefaultTableModel(new String[]{
            Mensagem.get("tabela.data"),
            Mensagem.get("tabela.produto"),
            Mensagem.get("tabela.tipo"),
            Mensagem.get("tabela.quantidade"),
            Mensagem.get("tabela.observacao")
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 3 ? Integer.class : String.class;
            }
        };

        tabelaHistorico = new JTable(modeloHistorico);
        AppTheme.estilizarTabela(tabelaHistorico);
        tabelaHistorico.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaHistorico.getColumnModel().getColumn(0).setMaxWidth(100);
        tabelaHistorico.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabelaHistorico.getColumnModel().getColumn(2).setMaxWidth(80);
        tabelaHistorico.getColumnModel().getColumn(2).setPreferredWidth(70);
        tabelaHistorico.getColumnModel().getColumn(3).setMaxWidth(60);

        JScrollPane scroll = new JScrollPane(tabelaHistorico);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA));

        painel.add(lblTitulo, BorderLayout.NORTH);
        painel.add(scroll,    BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------------------------
    // Carga de dados
    // -------------------------------------------------------------------------

    private void carregarProdutos() {
        Produto selecionado = (Produto) comboProduto.getSelectedItem();
        comboProduto.removeAllItems();
        comboProduto.addItem(SELECIONE);
        try {
            produtoService.listarTodos().forEach(comboProduto::addItem);
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar produtos:\n" + ex.getMessage());
        }
        // Restaura seleção anterior (após atualização pós-registro)
        if (selecionado != null && selecionado.getId() > 0) {
            for (int i = 0; i < comboProduto.getItemCount(); i++) {
                if (comboProduto.getItemAt(i).getId() == selecionado.getId()) {
                    comboProduto.setSelectedIndex(i);
                    return;
                }
            }
        }
        comboProduto.setSelectedIndex(0);
    }

    private void carregarHistorico() {
        modeloHistorico.setRowCount(0);
        try {
            List<Movimentacao> lista = movService.listarRecentes(20);
            for (Movimentacao m : lista) {
                String tipoDisplay = "SAIDA".equals(m.getTipo())
                    ? Mensagem.get("tipo.saida")
                    : Mensagem.get("tipo.entrada");
                modeloHistorico.addRow(new Object[]{
                    Formatador.formatarData(m.getData()),
                    m.getNomeProduto(),
                    tipoDisplay,
                    m.getQuantidade(),
                    m.getObservacao() != null ? m.getObservacao() : ""
                });
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar histórico:\n" + ex.getMessage());
        }
    }

    private void atualizarEstoque() {
        Produto p = (Produto) comboProduto.getSelectedItem();
        if (p != null && p.getId() > 0) {
            lblEstoqueAtual.setText(p.getQuantidade() + " un.");
            lblEstoqueAtual.setForeground(
                p.getQuantidade() == 0 ? Color.RED : AppTheme.COR_PRIMARIA);
        } else {
            lblEstoqueAtual.setText("--");
            lblEstoqueAtual.setForeground(AppTheme.COR_PRIMARIA);
        }
    }

    // -------------------------------------------------------------------------
    // Ação de registro
    // -------------------------------------------------------------------------

    private void acaoRegistrar() {
        // Validação do produto
        Produto p = (Produto) comboProduto.getSelectedItem();
        if (p == null || p.getId() <= 0) {
            Mensagem.erro(this, "Selecione um PRODUTO.");
            return;
        }

        // Validação da quantidade
        int quantidade;
        try {
            quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Mensagem.erro(this, "O campo QUANTIDADE deve ser um número inteiro maior que zero.");
            return;
        }

        // Validação da data
        if (!Validador.isDataValida(campoData.getText())) {
            Mensagem.erro(this, "O campo DATA deve estar no formato DD/MM/AAAA e representar uma data válida.");
            return;
        }
        LocalDate data = Formatador.parseData(campoData.getText());

        String tipo = rdEntrada.isSelected() ? "ENTRADA" : "SAIDA";
        String obs  = campoObservacao.getText().trim();

        Movimentacao m = new Movimentacao(0, tipo, quantidade, data,
                                          obs.isEmpty() ? null : obs, p.getId());
        try {
            movService.registrar(m);
            Mensagem.sucesso(this,
                tipo.equals("ENTRADA") ? "Entrada registrada com sucesso!"
                                       : "Saída registrada com sucesso!");
            limparFormulario();
            carregarProdutos();   // atualiza estoque no combo
            carregarHistorico();  // atualiza tabela
        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao registrar movimentação:\n" + ex.getMessage());
        }
    }

    private void limparFormulario() {
        campoQuantidade.setText("");
        campoObservacao.setText("");
        campoData.setText(Formatador.dataHoje());
        rdEntrada.setSelected(true);
        campoQuantidade.requestFocus();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private JLabel lbl(String chaveIgnorada, String textoFallback) {
        // Usa o texto diretamente (label de campo, não de i18n neste contexto)
        JLabel l = new JLabel(textoFallback);
        l.setFont(AppTheme.FONTE_BOLD);
        return l;
    }

    private JRadioButton radio(String texto, ButtonGroup grupo, boolean selecionado) {
        JRadioButton rd = new JRadioButton(texto, selecionado);
        rd.setBackground(AppTheme.COR_PAINEL);
        rd.setFont(AppTheme.FONTE_LABEL);
        rd.setFocusPainted(false);
        grupo.add(rd);
        return rd;
    }

    @SuppressWarnings("unchecked")
    private ListCellRenderer<? super Produto> rendererProduto() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Produto) {
                    Produto p = (Produto) value;
                    setText(p.getId() == 0 ? p.getNome() : p.toString());
                }
                setFont(AppTheme.FONTE_DADOS);
                return this;
            }
        };
    }
}
