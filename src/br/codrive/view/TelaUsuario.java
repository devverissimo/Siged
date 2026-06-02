/**
 * Classe: TelaUsuario
 * Objetivo: JInternalFrame de cadastro e pesquisa de usuários do sistema.
 *           Aba CADASTRO oferece CRUD completo com JPasswordField para a senha.
 *           Aba PESQUISA permite busca simultânea por nome e login com JTable.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Usuario;
import br.codrive.service.UsuarioService;
import br.codrive.util.AppTheme;
import br.codrive.util.Mensagem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaUsuario extends JInternalFrame {

    // Aba CADASTRO
    private JTextField     campoId, campoNome, campoLogin;
    private JPasswordField campoSenha;
    private JButton        btnNovo, btnSalvar, btnEditar, btnExcluir;

    // Aba PESQUISA
    private JTextField        campoBusca;
    private JTable            tabela;
    private DefaultTableModel modeloTabela;
    private JTabbedPane       abas;

    // Estado
    private Usuario usuarioAtual = null;

    private final UsuarioService service = new UsuarioService();

    public TelaUsuario() {
        super(Mensagem.get("titulo.usuario"), true, true, true, true);
        setSize(620, 440);
        setMinimumSize(new Dimension(520, 390));
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

        // --- Card formulário ---
        JPanel card = AppTheme.criarPainelCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 6, 7, 6);
        g.anchor = GridBagConstraints.WEST;

        // Linha 0 — CÓD.
        campoId = campoReadOnly(8);
        addFormRow(card, g, 0, Mensagem.get("lbl.codigo") + ":", campoId);

        // Linha 1 — NOME
        campoNome = new JTextField(28);
        addFormRow(card, g, 1, Mensagem.get("lbl.nome") + ":", campoNome);

        // Linha 2 — LOGIN
        campoLogin = new JTextField(20);
        addFormRow(card, g, 2, Mensagem.get("lbl.login") + ":", campoLogin);

        // Linha 3 — SENHA
        campoSenha = new JPasswordField(20);
        campoSenha.setFont(AppTheme.FONTE_DADOS);
        addFormRow(card, g, 3, Mensagem.get("lbl.senha") + ":", campoSenha);

        campoLogin.addActionListener(e -> campoSenha.requestFocus());
        campoSenha.addActionListener(e -> { if (btnSalvar.isEnabled()) acaoSalvar(); });

        painel.add(card, BorderLayout.CENTER);

        // --- Botões ---
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

        // --- Barra de busca ---
        JPanel painelBusca = new JPanel(new BorderLayout(6, 0));
        painelBusca.setBackground(AppTheme.COR_FUNDO);

        JLabel lblBusca = new JLabel("NOME / LOGIN:");
        lblBusca.setFont(AppTheme.FONTE_BOLD);

        campoBusca = new JTextField();

        JButton btnPesquisar = new JButton(Mensagem.get("btn.pesquisar"));
        AppTheme.estilizarBotaoPrimario(btnPesquisar);

        campoBusca.addActionListener(e   -> carregarTabela(campoBusca.getText()));
        btnPesquisar.addActionListener(e -> carregarTabela(campoBusca.getText()));

        painelBusca.add(lblBusca,     BorderLayout.WEST);
        painelBusca.add(campoBusca,   BorderLayout.CENTER);
        painelBusca.add(btnPesquisar, BorderLayout.EAST);

        // --- Tabela (sem coluna de senha) ---
        modeloTabela = new DefaultTableModel(new String[]{
            Mensagem.get("tabela.codigo"),
            Mensagem.get("tabela.nome"),
            Mensagem.get("lbl.login")
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
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(2).setMaxWidth(200);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) selecionarDaTabela();
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
        campoNome.setText("");
        campoLogin.setText("");
        campoSenha.setText("");
        usuarioAtual = null;
        setFormEnabled(false);
        btnNovo.setEnabled(true);
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private void preencherFormulario(Usuario u) {
        usuarioAtual = u;
        campoId.setText(String.valueOf(u.getId()));
        campoNome.setText(u.getNome());
        campoLogin.setText(u.getLogin());
        campoSenha.setText(u.getSenha());
        setFormEnabled(false);
        btnNovo.setEnabled(true);
        btnSalvar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnExcluir.setEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        campoNome.setEnabled(enabled);
        campoLogin.setEnabled(enabled);
        campoSenha.setEnabled(enabled);
    }

    // -------------------------------------------------------------------------
    // Ações dos botões
    // -------------------------------------------------------------------------

    private void acaoNovo() {
        campoId.setText("");
        campoNome.setText("");
        campoLogin.setText("");
        campoSenha.setText("");
        usuarioAtual = null;
        setFormEnabled(true);
        campoNome.requestFocus();
        btnNovo.setEnabled(false);
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private void acaoSalvar() {
        String senha = new String(campoSenha.getPassword());

        try {
            Usuario u = usuarioAtual != null ? usuarioAtual : new Usuario();
            u.setNome(campoNome.getText());
            u.setLogin(campoLogin.getText());
            u.setSenha(senha);

            if (usuarioAtual == null) {
                int idGerado = service.inserir(u);
                u.setId(idGerado);
                Mensagem.sucesso(this, "Usuário cadastrado com sucesso!");
            } else {
                service.atualizar(u);
                Mensagem.sucesso(this, "Usuário atualizado com sucesso!");
            }
            preencherFormulario(u);

        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao salvar usuário:\n" + ex.getMessage());
        }
    }

    private void acaoEditar() {
        if (usuarioAtual == null) return;
        setFormEnabled(true);
        campoNome.requestFocus();
        btnNovo.setEnabled(false);
        btnSalvar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private void acaoExcluir() {
        if (usuarioAtual == null) return;
        if (!Mensagem.confirmar(this, Mensagem.get("confirm.excluir"))) return;
        try {
            service.excluir(usuarioAtual.getId());
            Mensagem.sucesso(this, "Usuário excluído com sucesso!");
            definirEstadoInicial();
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao excluir usuário:\n" + ex.getMessage());
        }
    }

    private void selecionarDaTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) modeloTabela.getValueAt(row, 0);
        try {
            Usuario u = service.buscarPorId(id);
            if (u != null) {
                preencherFormulario(u);
                abas.setSelectedIndex(0);
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar usuário:\n" + ex.getMessage());
        }
    }

    private void carregarTabela(String termo) {
        modeloTabela.setRowCount(0);
        try {
            List<Usuario> lista = service.buscarPorLoginOuNome(
                termo == null ? "" : termo);
            for (Usuario u : lista) {
                modeloTabela.addRow(new Object[]{
                    u.getId(), u.getNome(), u.getLogin()
                });
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao pesquisar usuários:\n" + ex.getMessage());
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

    private void addFormRow(JPanel card, GridBagConstraints g,
                            int row, String labelText, JComponent campo) {
        g.gridx = 0; g.gridy = row;
        g.fill = GridBagConstraints.NONE; g.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(AppTheme.FONTE_BOLD);
        card.add(lbl, g);

        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        card.add(campo, g);
    }
}
