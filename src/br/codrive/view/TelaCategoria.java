/**
 * Classe: TelaCategoria
 * Objetivo: JInternalFrame de cadastro e pesquisa de categorias. Possui duas
 *           abas: CADASTRO (CRUD com controle de estado dos botões) e PESQUISA
 *           (busca parcial por nome com JTable; clique na linha preenche o form).
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Categoria;
import br.codrive.service.CategoriaService;
import br.codrive.util.AppTheme;
import br.codrive.util.Mensagem;
import br.codrive.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCategoria extends JInternalFrame implements ModuloAcoes {

    // Aba CADASTRO
    private JTextField campoId;
    private JTextField campoNome;
    private JButton    btnNovo, btnSalvar, btnEditar, btnExcluir;

    // Aba PESQUISA
    private JTextField         campoBusca;
    private JTable             tabela;
    private DefaultTableModel  modeloTabela;
    private JTabbedPane        abas;

    // Estado
    private Categoria categoriaAtual = null;

    private final CategoriaService service = new CategoriaService();

    public TelaCategoria() {
        super(Mensagem.get("titulo.categoria"), true, true, true, true);
        setSize(580, 400);
        setMinimumSize(new Dimension(480, 360));
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

        abas = new JTabbedPane();
        abas.add(Mensagem.get("tab.cadastro"), construirAbaCadastro());
        abas.add(Mensagem.get("tab.pesquisa"), construirAbaPesquisa());

        abas.addChangeListener(e -> {
            if (abas.getSelectedIndex() == 1) carregarTabela(campoBusca.getText());
        });

        raiz.add(abas, BorderLayout.CENTER);
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
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        JLabel lblId = label(Mensagem.get("lbl.codigo") + ":");
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        card.add(lblId, g);

        campoId = new JTextField(8);
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        Validador.aplicarFiltroInteiro(campoId);
        g.gridx = 1; g.fill = GridBagConstraints.NONE;
        card.add(campoId, g);

        JLabel lblNome = label(Mensagem.get("lbl.nome") + ":");
        g.gridx = 0; g.gridy = 1; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        card.add(lblNome, g);

        campoNome = new JTextField(28);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        card.add(campoNome, g);

        campoNome.addActionListener(e -> { if (btnSalvar.isEnabled()) acaoSalvar(); });

        painel.add(card, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        painelBotoes.setBackground(AppTheme.COR_FUNDO);

        btnNovo    = new JButton(Mensagem.get("btn.novo"));
        btnSalvar  = new JButton(Mensagem.get("btn.salvar"));
        btnEditar  = new JButton(Mensagem.get("btn.editar"));
        btnExcluir = new JButton(Mensagem.get("btn.excluir"));

        AppTheme.estilizarBotaoToolbar(btnNovo);
        AppTheme.estilizarBotaoPrimario(btnSalvar);
        AppTheme.estilizarBotaoToolbar(btnEditar);
        AppTheme.estilizarBotaoToolbar(btnExcluir);

        btnNovo.addActionListener(e    -> acaoNovo());
        btnSalvar.addActionListener(e  -> acaoSalvar());
        btnEditar.addActionListener(e  -> acaoEditar());
        btnExcluir.addActionListener(e -> acaoExcluir());

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        painel.add(painelBotoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel construirAbaPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelBusca = new JPanel(new BorderLayout(6, 0));
        painelBusca.setBackground(AppTheme.COR_FUNDO);

        JLabel lblBusca = label(Mensagem.get("lbl.nome") + ":");
        campoBusca = new JTextField();
        JButton btnPesquisar = new JButton(Mensagem.get("btn.pesquisar"));
        AppTheme.estilizarBotaoPrimario(btnPesquisar);

        campoBusca.addActionListener(e   -> carregarTabela(campoBusca.getText()));
        btnPesquisar.addActionListener(e -> carregarTabela(campoBusca.getText()));

        painelBusca.add(lblBusca,     BorderLayout.WEST);
        painelBusca.add(campoBusca,   BorderLayout.CENTER);
        painelBusca.add(btnPesquisar, BorderLayout.EAST);

        modeloTabela = new DefaultTableModel(
            new String[]{
                Mensagem.get("tabela.codigo"),
                Mensagem.get("tabela.nome")
            }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : String.class;
            }
        };

        tabela = new JTable(modeloTabela);
        AppTheme.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(80);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(60);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                selecionarDaTabela();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA));

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(scroll,      BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------------------------
    // Controle de estado
    // -------------------------------------------------------------------------

    private void definirEstadoInicial() {
        campoId.setText("");
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        campoNome.setText("");
        campoNome.setEnabled(false);
        categoriaAtual = null;
        btnNovo.setEnabled(true);
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private void preencherFormulario(Categoria c) {
        categoriaAtual = c;
        campoId.setText(String.valueOf(c.getId()));
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        campoNome.setText(c.getNome());
        campoNome.setEnabled(false);
        btnNovo.setEnabled(true);
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnExcluir.setEnabled(true);
    }

    // -------------------------------------------------------------------------
    // Implementação de ModuloAcoes — delegação da toolbar de TelaMenu
    // -------------------------------------------------------------------------

    @Override
    public void acaoNovo() {
        campoId.setText("");
        campoId.setEditable(true);
        campoId.setBackground(AppTheme.COR_PAINEL);
        campoNome.setText("");
        categoriaAtual = null;
        campoNome.setEnabled(true);
        campoId.requestFocus();
        btnNovo.setEnabled(false);
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    @Override
    public void acaoSalvar() {
        try {
            Categoria c = categoriaAtual != null ? categoriaAtual : new Categoria();
            c.setNome(campoNome.getText());

            if (categoriaAtual == null) {
                String idTexto = campoId.getText().trim();
                if (!idTexto.isEmpty()) c.setId(Integer.parseInt(idTexto));
                int idGerado = service.inserir(c);
                c.setId(idGerado);
                Mensagem.sucesso(this, "Categoria cadastrada com sucesso!");
            } else {
                service.atualizar(c);
                Mensagem.sucesso(this, "Categoria atualizada com sucesso!");
            }
            definirEstadoInicial();

        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao salvar categoria:\n" + ex.getMessage());
        }
    }

    @Override
    public void acaoEditar() {
        if (categoriaAtual == null) return;
        campoNome.setEnabled(true);
        campoNome.requestFocus();
        btnNovo.setEnabled(false);
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    @Override
    public void acaoExcluir() {
        if (categoriaAtual == null) return;
        if (!Mensagem.confirmar(this, Mensagem.get("confirm.excluir"))) return;
        try {
            service.excluir(categoriaAtual.getId());
            Mensagem.sucesso(this, "Categoria excluída com sucesso!");
            definirEstadoInicial();
        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao excluir categoria:\n" + ex.getMessage());
        }
    }

    @Override
    public void acaoPesquisar() {
        abas.setSelectedIndex(1);
        campoBusca.requestFocus();
    }

    // -------------------------------------------------------------------------
    // Ações internas
    // -------------------------------------------------------------------------

    private void selecionarDaTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) modeloTabela.getValueAt(row, 0);
        try {
            Categoria c = service.buscarPorId(id);
            if (c != null) {
                preencherFormulario(c);
                abas.setSelectedIndex(0);
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar categoria:\n" + ex.getMessage());
        }
    }

    private void carregarTabela(String termo) {
        try {
            modeloTabela.setRowCount(0);
            List<Categoria> lista = service.buscarPorNome(termo == null ? "" : termo);
            for (Categoria c : lista) {
                modeloTabela.addRow(new Object[]{c.getId(), c.getNome()});
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao pesquisar categorias:\n" + ex.getMessage());
        }
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(AppTheme.FONTE_BOLD);
        return l;
    }
}
