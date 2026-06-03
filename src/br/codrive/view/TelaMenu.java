/**
 * Classe: TelaMenu
 * Objetivo: Frame principal do sistema SIGED. Contém JMenuBar escura,
 *           toolbar com atalhos F2-F8, sidebar de módulos com hover laranja,
 *           JDesktopPane central para as JInternalFrames e barra de status.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Usuario;
import br.codrive.util.AppTheme;
import br.codrive.util.Mensagem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

public class TelaMenu extends JFrame {

    private final Usuario    usuarioLogado;
    private JDesktopPane     desktop;
    private JLabel           lblStatusModulo;

    private JButton btnNovo, btnSalvar, btnEditar, btnExcluir, btnPesquisar;

    private static final Color COR_SIDEBAR = new Color(0x1F, 0x29, 0x37);
    private final List<JButton> itensNav   = new ArrayList<>();
    private JButton itemAtivo;
    private JButton navDashboard, navCadastros, navMovimentacao,
                    navListagem, navConfiguracoes;

    public TelaMenu(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarJanela();
        construirMenuBar();
        construirToolbar();
        construirConteudo();
        construirStatusBar();
        SwingUtilities.invokeLater(this::abrirDashboard);
    }

    // -------------------------------------------------------------------------
    // Configuração da janela
    // -------------------------------------------------------------------------
    private void configurarJanela() {
        setTitle("SIGED v1.0 — Sistema de Gerenciamento de Estoque para Distribuidoras [CoDrive]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // JMenuBar escura
    // -------------------------------------------------------------------------
    private void construirMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(AppTheme.COR_MENU);
        menuBar.setBorderPainted(false);
        menuBar.setOpaque(true);

        JMenuItem mCadastros = item(Mensagem.get("menu.cadastros"), e -> abrirSeletorCadastro());

        JMenu mMov = menu(Mensagem.get("menu.movimentacao"));
        mMov.add(item(Mensagem.get("titulo.movimentacao"), e -> abrirMovimentacao()));

        JMenu mList = menu(Mensagem.get("menu.listagem"));
        mList.add(item(Mensagem.get("titulo.listagem"), e -> abrirListagem()));

        JMenu mUsr = menu(Mensagem.get("menu.usuarios"));
        mUsr.add(item(Mensagem.get("titulo.usuario"), e -> abrirUsuario()));

        JMenu mConf = menu(Mensagem.get("menu.configuracoes"));
        mConf.add(item(Mensagem.get("titulo.configuracoes"), e -> abrirConfiguracoes()));

        menuBar.add(mCadastros);
        menuBar.add(mMov);
        menuBar.add(mList);
        menuBar.add(mUsr);
        menuBar.add(mConf);
        setJMenuBar(menuBar);
    }

    private JMenu menu(String texto) {
        JMenu m = new JMenu(texto);
        m.setForeground(AppTheme.COR_BRANCO);
        m.setFont(AppTheme.FONTE_BOLD);
        m.setOpaque(false);
        return m;
    }

    private JMenuItem item(String texto, ActionListener al) {
        JMenuItem mi = new JMenuItem(texto);
        mi.setBackground(AppTheme.COR_MENU);
        mi.setForeground(AppTheme.COR_BRANCO);
        mi.setFont(AppTheme.FONTE_LABEL);
        mi.addActionListener(al);
        return mi;
    }

    // -------------------------------------------------------------------------
    // Toolbar F2-F8
    // -------------------------------------------------------------------------
    private void construirToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setBackground(new Color(0x2D, 0x3A, 0x4A));
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.COR_BORDA));

        btnNovo      = botaoToolbar(Mensagem.get("btn.novo"),      e -> acaoNovo());
        btnSalvar    = botaoToolbar(Mensagem.get("btn.salvar"),    e -> acaoSalvar());
        btnEditar    = botaoToolbar(Mensagem.get("btn.editar"),    e -> acaoEditar());
        btnExcluir   = botaoToolbar(Mensagem.get("btn.excluir"),   e -> acaoExcluir());
        btnPesquisar = botaoToolbar(Mensagem.get("btn.pesquisar"), e -> acaoPesquisar());

        toolbar.add(btnNovo);
        toolbar.add(btnSalvar);
        toolbar.add(btnEditar);
        toolbar.add(btnExcluir);
        toolbar.add(btnPesquisar);

        getContentPane().add(toolbar, BorderLayout.NORTH);
    }

    private JButton botaoToolbar(String texto, ActionListener al) {
        JButton btn = new JButton(texto);
        AppTheme.estilizarBotaoToolbar(btn);
        btn.addActionListener(al);
        return btn;
    }

    // -------------------------------------------------------------------------
    // Área central: sidebar + JDesktopPane
    // -------------------------------------------------------------------------
    private void construirConteudo() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x2D, 0x3A, 0x4A)));

        // Logo
        JPanel painelLogo = new JPanel();
        painelLogo.setLayout(new BoxLayout(painelLogo, BoxLayout.Y_AXIS));
        painelLogo.setBackground(COR_SIDEBAR);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(16, 14, 12, 14));
        painelLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JLabel lblSiged = new JLabel("SIGED");
        lblSiged.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSiged.setForeground(AppTheme.COR_PRIMARIA);
        lblSiged.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCodrive = new JLabel("CoDrive");
        lblCodrive.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCodrive.setForeground(new Color(0xD9, 0x77, 0x06));
        lblCodrive.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelLogo.add(lblSiged);
        painelLogo.add(lblCodrive);
        sidebar.add(painelLogo);

        JSeparator sepLogo = new JSeparator();
        sepLogo.setForeground(new Color(0x2D, 0x3A, 0x4A));
        sepLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sepLogo);
        sidebar.add(Box.createVerticalStrut(6));

        // Itens de navegação
        navDashboard     = itemSidebar("Dashboard");
        navCadastros     = itemSidebar("Cadastros");
        navMovimentacao  = itemSidebar("Movimentação");
        navListagem      = itemSidebar("Listagem");
        navConfiguracoes = itemSidebar("Configurações");

        navDashboard.addActionListener(e     -> { setItemAtivo(navDashboard);     abrirDashboard();        });
        navCadastros.addActionListener(e     -> { setItemAtivo(navCadastros);     abrirSeletorCadastro();  });
        navMovimentacao.addActionListener(e  -> { setItemAtivo(navMovimentacao);  abrirMovimentacao();     });
        navListagem.addActionListener(e      -> { setItemAtivo(navListagem);      abrirListagem();         });
        navConfiguracoes.addActionListener(e -> { setItemAtivo(navConfiguracoes); abrirConfiguracoes();    });

        sidebar.add(navDashboard);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(labelSecao("CADASTROS"));
        sidebar.add(navCadastros);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(labelSecao("OPERAÇÕES"));
        sidebar.add(navMovimentacao);
        sidebar.add(navListagem);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(labelSecao("SISTEMA"));
        sidebar.add(navConfiguracoes);
        sidebar.add(Box.createVerticalGlue());

        // Rodapé
        JSeparator sepRodape = new JSeparator();
        sepRodape.setForeground(new Color(0x2D, 0x3A, 0x4A));
        sepRodape.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sepRodape);

        JLabel lblNomeUser = new JLabel("  " + usuarioLogado.getNome());
        lblNomeUser.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblNomeUser.setForeground(new Color(0x9C, 0xA3, 0xAF));
        lblNomeUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        lblNomeUser.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));
        sidebar.add(lblNomeUser);

        JButton btnSair = new JButton("  SAIR");
        btnSair.setBackground(new Color(0x7F, 0x1D, 0x1D));
        btnSair.setForeground(AppTheme.COR_BRANCO);
        btnSair.setFont(AppTheme.FONTE_BOLD);
        btnSair.setFocusPainted(false);
        btnSair.setBorderPainted(false);
        btnSair.setOpaque(true);
        btnSair.setHorizontalAlignment(SwingConstants.LEFT);
        btnSair.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnSair.setBackground(new Color(0x99, 0x1B, 0x1B)); }
            @Override public void mouseExited (MouseEvent e) { btnSair.setBackground(new Color(0x7F, 0x1D, 0x1D)); }
        });
        btnSair.addActionListener(e -> { dispose(); new TelaLogin().setVisible(true); });
        sidebar.add(btnSair);
        sidebar.add(Box.createVerticalStrut(8));

        // Desktop
        desktop = new JDesktopPane();
        desktop.setBackground(AppTheme.COR_DESKTOP);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.add(sidebar, BorderLayout.WEST);
        conteudo.add(desktop, BorderLayout.CENTER);

        getContentPane().add(conteudo, BorderLayout.CENTER);
    }

    private JButton itemSidebar(String texto) {
        JButton btn = new JButton("   " + texto);
        btn.setBackground(COR_SIDEBAR);
        btn.setForeground(AppTheme.COR_BRANCO);
        btn.setFont(AppTheme.FONTE_LABEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != itemAtivo) btn.setBackground(new Color(0x37, 0x41, 0x51));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != itemAtivo) btn.setBackground(COR_SIDEBAR);
            }
        });
        itensNav.add(btn);
        return btn;
    }

    private JLabel labelSecao(String texto) {
        JLabel l = new JLabel("  " + texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(new Color(0x6B, 0x72, 0x80));
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 2, 0));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        return l;
    }

    private void setItemAtivo(JButton btn) {
        for (JButton b : itensNav) b.setBackground(COR_SIDEBAR);
        itemAtivo = btn;
        if (btn != null) btn.setBackground(AppTheme.COR_PRIMARIA);
    }

    // -------------------------------------------------------------------------
    // Barra de status inferior
    // -------------------------------------------------------------------------
    private void construirStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(AppTheme.COR_MENU);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.COR_BORDA),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel lblUsuario = label(
            Mensagem.get("status.usuario") + ": " + usuarioLogado.getNome()
            + " (" + usuarioLogado.getLogin() + ")");

        lblStatusModulo = label(Mensagem.get("status.modulo") + ": —");
        lblStatusModulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDireita = label(
            Mensagem.get("status.banco.conectado") + "  |  " + Mensagem.get("status.idioma"));

        statusBar.add(lblUsuario,      BorderLayout.WEST);
        statusBar.add(lblStatusModulo, BorderLayout.CENTER);
        statusBar.add(lblDireita,      BorderLayout.EAST);

        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(AppTheme.FONTE_LABEL);
        l.setForeground(AppTheme.COR_BRANCO);
        return l;
    }

    // -------------------------------------------------------------------------
    // Abertura de módulos
    // -------------------------------------------------------------------------
    void abrirSeletorCadastro() { abrirInternalFrame(new TelaSeletorCadastro(this)); }
    void abrirCategoria()    { abrirInternalFrame(new TelaCategoria()); }
    void abrirProduto()      { abrirInternalFrame(new TelaProduto()); }
    void abrirMovimentacao() { abrirInternalFrame(new TelaMovimentacao()); }
    void abrirListagem()     { abrirInternalFrame(new TelaListagem()); }
    void abrirUsuario()      { abrirInternalFrame(new TelaUsuario()); }
    void abrirConfiguracoes(){ abrirInternalFrame(new TelaConfiguracoes()); }

    void abrirDashboard() {
        setItemAtivo(navDashboard);
        for (JInternalFrame f : desktop.getAllFrames()) {
            if (f instanceof TelaDashboard && !f.isClosed()) {
                try { f.setSelected(true); f.toFront(); }
                catch (PropertyVetoException ex) { /* ignora */ }
                return;
            }
        }
        TelaDashboard dash = new TelaDashboard();
        dash.setVisible(true);
        desktop.add(dash);
        try { dash.setMaximum(true); } catch (Exception ex) { /* ignora */ }
        try { dash.setSelected(true); }
        catch (PropertyVetoException ex) { /* ignora */ }
        atualizarStatusModulo(dash.getTitle());
    }

    // -------------------------------------------------------------------------
    // Ações da toolbar — delegadas via ModuloAcoes para o módulo ativo
    // -------------------------------------------------------------------------
    void acaoNovo() {
        JInternalFrame f = desktop.getSelectedFrame();
        if (f instanceof ModuloAcoes) ((ModuloAcoes) f).acaoNovo();
    }

    void acaoSalvar() {
        JInternalFrame f = desktop.getSelectedFrame();
        if (f instanceof ModuloAcoes) ((ModuloAcoes) f).acaoSalvar();
    }

    void acaoEditar() {
        JInternalFrame f = desktop.getSelectedFrame();
        if (f instanceof ModuloAcoes) ((ModuloAcoes) f).acaoEditar();
    }

    void acaoExcluir() {
        JInternalFrame f = desktop.getSelectedFrame();
        if (f instanceof ModuloAcoes) ((ModuloAcoes) f).acaoExcluir();
    }

    void acaoPesquisar() {
        JInternalFrame f = desktop.getSelectedFrame();
        if (f instanceof ModuloAcoes) ((ModuloAcoes) f).acaoPesquisar();
    }

    // -------------------------------------------------------------------------
    // Utilitários públicos usados pelas JInternalFrames
    // -------------------------------------------------------------------------

    /** Abre uma JInternalFrame em cascata, evitando duplicatas da mesma classe. */
    public void abrirInternalFrame(JInternalFrame frame) {
        for (JInternalFrame f : desktop.getAllFrames()) {
            if (f.getClass() == frame.getClass() && !f.isClosed()) {
                try { f.setSelected(true); f.toFront(); }
                catch (PropertyVetoException ex) { /* ignora */ }
                return;
            }
        }
        // Posicionamento em cascata (máximo 8 níveis antes de reiniciar)
        int offset = (desktop.getAllFrames().length % 8) * 28;
        frame.setLocation(offset, offset);

        frame.setVisible(true);
        desktop.add(frame);
        try { frame.setMaximum(true); } catch (Exception ex) { /* ignora */ }
        try { frame.setSelected(true); }
        catch (PropertyVetoException ex) { /* ignora */ }
        atualizarStatusModulo(frame.getTitle());
    }

    /** Atualiza o campo "Módulo:" na barra de status. */
    public void atualizarStatusModulo(String nomeModulo) {
        lblStatusModulo.setText(Mensagem.get("status.modulo") + ": " + nomeModulo);
    }

    public JDesktopPane getDesktop() { return desktop; }
}
