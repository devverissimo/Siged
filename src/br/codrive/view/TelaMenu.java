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

public class TelaMenu extends JFrame {

    private final Usuario    usuarioLogado;
    private JDesktopPane     desktop;
    private JLabel           lblStatusModulo;

    private JButton btnNovo, btnSalvar, btnEditar, btnExcluir, btnPesquisar;

    public TelaMenu(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarJanela();
        construirMenuBar();
        construirToolbar();
        construirConteudo();
        construirStatusBar();
        registrarAtalhos();
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

        JMenu mCadastros = menu(Mensagem.get("menu.cadastros"));
        mCadastros.add(item(Mensagem.get("menu.cadastros.categoria"), e -> abrirCategoria()));
        mCadastros.add(item(Mensagem.get("menu.cadastros.produto"),   e -> abrirProduto()));

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

        btnNovo      = botaoToolbar(Mensagem.get("toolbar.novo"),      e -> acaoNovo());
        btnSalvar    = botaoToolbar(Mensagem.get("toolbar.salvar"),    e -> acaoSalvar());
        btnEditar    = botaoToolbar(Mensagem.get("toolbar.editar"),    e -> acaoEditar());
        btnExcluir   = botaoToolbar(Mensagem.get("toolbar.excluir"),   e -> acaoExcluir());
        btnPesquisar = botaoToolbar(Mensagem.get("toolbar.pesquisar"), e -> acaoPesquisar());

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
        sidebar.setBackground(AppTheme.COR_MENU);
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.COR_BORDA));

        JLabel lblModulos = new JLabel("  MÓDULOS");
        lblModulos.setFont(AppTheme.FONTE_CAB_TABELA);
        lblModulos.setForeground(new Color(0x9C, 0xA3, 0xAF));
        lblModulos.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));
        lblModulos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sidebar.add(lblModulos);

        JSeparator sepSidebar = new JSeparator();
        sepSidebar.setForeground(new Color(0x4B, 0x55, 0x63));
        sepSidebar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sepSidebar);

        sidebar.add(botaoSidebar(Mensagem.get("sidebar.categoria"),     e -> abrirCategoria()));
        sidebar.add(botaoSidebar(Mensagem.get("sidebar.produto"),       e -> abrirProduto()));
        sidebar.add(botaoSidebar(Mensagem.get("sidebar.movimentacao"),  e -> abrirMovimentacao()));
        sidebar.add(botaoSidebar(Mensagem.get("sidebar.listagem"),      e -> abrirListagem()));
        sidebar.add(botaoSidebar(Mensagem.get("sidebar.usuario"),       e -> abrirUsuario()));
        sidebar.add(botaoSidebar(Mensagem.get("sidebar.configuracoes"), e -> abrirConfiguracoes()));
        sidebar.add(Box.createVerticalGlue());

        desktop = new JDesktopPane();
        desktop.setBackground(AppTheme.COR_DESKTOP);

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.add(sidebar, BorderLayout.WEST);
        conteudo.add(desktop, BorderLayout.CENTER);

        getContentPane().add(conteudo, BorderLayout.CENTER);
    }

    private JButton botaoSidebar(String texto, ActionListener al) {
        JButton btn = new JButton("  " + texto);
        btn.setBackground(AppTheme.COR_MENU);
        btn.setForeground(AppTheme.COR_BRANCO);
        btn.setFont(AppTheme.FONTE_LABEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(AppTheme.COR_PRIMARIA); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(AppTheme.COR_MENU);    }
        });
        btn.addActionListener(al);
        return btn;
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
    // Atalhos de teclado F2-F8
    // -------------------------------------------------------------------------
    private void registrarAtalhos() {
        InputMap  im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        im.put(KeyStroke.getKeyStroke("F2"), "novo");
        im.put(KeyStroke.getKeyStroke("F3"), "salvar");
        im.put(KeyStroke.getKeyStroke("F4"), "editar");
        im.put(KeyStroke.getKeyStroke("F8"), "excluir");
        im.put(KeyStroke.getKeyStroke("F5"), "pesquisar");

        am.put("novo",      acao(this::acaoNovo));
        am.put("salvar",    acao(this::acaoSalvar));
        am.put("editar",    acao(this::acaoEditar));
        am.put("excluir",   acao(this::acaoExcluir));
        am.put("pesquisar", acao(this::acaoPesquisar));
    }

    private Action acao(Runnable r) {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent e) { r.run(); }
        };
    }

    // -------------------------------------------------------------------------
    // Abertura de módulos
    // -------------------------------------------------------------------------
    void abrirCategoria()    { abrirInternalFrame(new TelaCategoria()); }
    void abrirProduto()      { abrirInternalFrame(new TelaProduto()); }
    void abrirMovimentacao() { abrirInternalFrame(new TelaMovimentacao()); }
    void abrirListagem()     { abrirInternalFrame(new TelaListagem()); }
    void abrirUsuario()      { abrirInternalFrame(new TelaUsuario()); }
    void abrirConfiguracoes(){ abrirInternalFrame(new TelaConfiguracoes()); }

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
