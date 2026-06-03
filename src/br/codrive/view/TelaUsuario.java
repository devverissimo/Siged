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
import br.codrive.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaUsuario extends JInternalFrame implements ModuloAcoes {

    // Aba CADASTRO
    private JTextField     campoId, campoNome, campoLogin;
    private JPasswordField campoSenha;
    private JButton        btnSalvar, btnCancelar;
    private JPanel         painelConteudo;

    // Aba PESQUISA
    private JTextField        campoBusca;
    private JTable            tabela;
    private DefaultTableModel modeloTabela;
    private JTabbedPane       abas;
    private JButton           btnEditarPesquisa, btnExcluirPesquisa;

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

        JPanel abaCadastroPanel = construirAbaCadastro();
        JPanel abaPesquisaPanel = construirAbaPesquisa();

        JPanel barraAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barraAcoes.setBackground(new Color(0xF3, 0xF4, 0xF6));
        barraAcoes.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.COR_BORDA));
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
            btnSalvar.setVisible(cadastro);
            btnCancelar.setVisible(cadastro);
            if (!cadastro) {
                btnEditarPesquisa.setEnabled(false);
                btnExcluirPesquisa.setEnabled(false);
                btnEditarPesquisa.setVisible(true);
                btnExcluirPesquisa.setVisible(true);
                carregarTabela(campoBusca.getText());
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
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 6, 7, 6);
        g.anchor = GridBagConstraints.WEST;

        campoId = campoReadOnly(8);
        Validador.aplicarFiltroInteiro(campoId);
        addFormRow(card, g, 0, Mensagem.get("lbl.codigo") + ":", campoId);

        campoNome = new JTextField(28);
        addFormRow(card, g, 1, Mensagem.get("lbl.nome") + ":", campoNome);

        campoLogin = new JTextField(20);
        addFormRow(card, g, 2, Mensagem.get("lbl.login") + ":", campoLogin);

        campoSenha = new JPasswordField(20);
        campoSenha.setFont(AppTheme.FONTE_DADOS);
        addFormRow(card, g, 3, Mensagem.get("lbl.senha") + ":", campoSenha);

        campoLogin.addActionListener(e -> campoSenha.requestFocus());
        campoSenha.addActionListener(e -> { if (btnSalvar.isEnabled()) acaoSalvar(); });

        painelConteudo = new JPanel(new CardLayout());
        painelConteudo.add(criarPainelOrientativo(), "ORIENTATIVO");
        painelConteudo.add(card,                     "FORMULARIO");
        painel.add(painelConteudo, BorderLayout.CENTER);

        btnSalvar   = new JButton(Mensagem.get("btn.salvar"));
        btnCancelar = new JButton("CANCELAR");

        AppTheme.estilizarBotaoPrimario(btnSalvar);
        AppTheme.estilizarBotaoSecundario(btnCancelar);

        btnSalvar.addActionListener(e   -> acaoSalvar());
        btnCancelar.addActionListener(e -> definirEstadoInicial());

        return painel;
    }

    private JPanel construirAbaPesquisa() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
    // Controle de estado
    // -------------------------------------------------------------------------

    private void definirEstadoInicial() {
        campoId.setText("");
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        campoNome.setText("");
        campoLogin.setText("");
        campoSenha.setText("");
        usuarioAtual = null;
        setFormEnabled(false);
        btnSalvar.setEnabled(false);
        btnCancelar.setEnabled(false);
        ((CardLayout) painelConteudo.getLayout()).show(painelConteudo, "ORIENTATIVO");
    }

    private void preencherFormulario(Usuario u) {
        ((CardLayout) painelConteudo.getLayout()).show(painelConteudo, "FORMULARIO");
        usuarioAtual = u;
        campoId.setText(String.valueOf(u.getId()));
        campoId.setEditable(false);
        campoId.setBackground(AppTheme.COR_LINHA_PAR);
        campoNome.setText(u.getNome());
        campoLogin.setText(u.getLogin());
        campoSenha.setText(u.getSenha());
        setFormEnabled(false);
        btnSalvar.setEnabled(false);
        btnCancelar.setEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        campoNome.setEnabled(enabled);
        campoLogin.setEnabled(enabled);
        campoSenha.setEnabled(enabled);
    }

    // -------------------------------------------------------------------------
    // Implementação de ModuloAcoes — delegação da toolbar de TelaMenu
    // -------------------------------------------------------------------------

    @Override
    public void acaoNovo() {
        ((CardLayout) painelConteudo.getLayout()).show(painelConteudo, "FORMULARIO");
        campoId.setText("");
        campoId.setEditable(true);
        campoId.setBackground(AppTheme.COR_PAINEL);
        campoNome.setText("");
        campoLogin.setText("");
        campoSenha.setText("");
        usuarioAtual = null;
        setFormEnabled(true);
        campoId.requestFocus();
        btnSalvar.setEnabled(true);
        btnCancelar.setEnabled(true);
    }

    @Override
    public void acaoSalvar() {
        String senha = new String(campoSenha.getPassword());
        try {
            Usuario u = usuarioAtual != null ? usuarioAtual : new Usuario();
            u.setNome(campoNome.getText());
            u.setLogin(campoLogin.getText());
            u.setSenha(senha);

            if (usuarioAtual == null) {
                String idTexto = campoId.getText().trim();
                if (!idTexto.isEmpty()) u.setId(Integer.parseInt(idTexto));
                int idGerado = service.inserir(u);
                u.setId(idGerado);
                Mensagem.sucesso(this, "Usuário cadastrado com sucesso!");
            } else {
                service.atualizar(u);
                Mensagem.sucesso(this, "Usuário atualizado com sucesso!");
            }
            definirEstadoInicial();

        } catch (IllegalArgumentException ex) {
            Mensagem.erro(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao salvar usuário:\n" + ex.getMessage());
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

    private void selecionarDaTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) modeloTabela.getValueAt(row, 0);
        try {
            Usuario u = service.buscarPorId(id);
            if (u != null) {
                usuarioAtual = u;
                btnEditarPesquisa.setEnabled(true);
                btnExcluirPesquisa.setEnabled(true);
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar usuário:\n" + ex.getMessage());
        }
    }

    /** Chamado pelo popup "Meu Perfil" da topbar de TelaMenu. */
    public void editarUsuarioDireto(Usuario u) {
        usuarioAtual = u;
        editarSelecionado();
    }

    private void editarSelecionado() {
        if (usuarioAtual == null) return;
        preencherFormulario(usuarioAtual);
        setFormEnabled(true);
        campoNome.requestFocus();
        btnSalvar.setEnabled(true);
        btnCancelar.setEnabled(true);
        abas.setSelectedIndex(0);
    }

    private void excluirSelecionado() {
        if (usuarioAtual == null) return;
        if (!Mensagem.confirmar(this, Mensagem.get("confirm.excluir"))) return;
        try {
            service.excluir(usuarioAtual.getId());
            Mensagem.sucesso(this, "Usuário excluído com sucesso!");
            usuarioAtual = null;
            btnEditarPesquisa.setEnabled(false);
            btnExcluirPesquisa.setEnabled(false);
            carregarTabela(campoBusca.getText());
            definirEstadoInicial();
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao excluir usuário:\n" + ex.getMessage());
        }
    }

    private void carregarTabela(String termo) {
        modeloTabela.setRowCount(0);
        try {
            List<Usuario> lista = service.buscarPorLoginOuNome(
                termo == null ? "" : termo);
            for (Usuario u : lista) {
                modeloTabela.addRow(new Object[]{u.getId(), u.getNome(), u.getLogin()});
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
