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

    private static final Color COR_SIDEBAR = new Color(0x1F, 0x29, 0x37);
    private final List<JButton> itensNav   = new ArrayList<>();
    private JButton itemAtivo;
    private JButton navDashboard, navCadastros, navMovimentacao,
                    navListagem, navConfiguracoes;
    private JTextField campoBuscaTopbar;

    public TelaMenu(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarJanela();
        construirTopbar();
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
    // Topbar
    // -------------------------------------------------------------------------
    private void construirTopbar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(AppTheme.COR_MENU);
        topbar.setPreferredSize(new Dimension(0, 50));
        topbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.COR_BORDA));

        // ESQUERDA — saudação
        JPanel painelSaudacao = new JPanel();
        painelSaudacao.setLayout(new BoxLayout(painelSaudacao, BoxLayout.Y_AXIS));
        painelSaudacao.setBackground(AppTheme.COR_MENU);
        painelSaudacao.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel lblBemVindo = new JLabel("BEM-VINDO");
        lblBemVindo.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblBemVindo.setForeground(new Color(0x9C, 0xA3, 0xAF));
        lblBemVindo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNome = new JLabel(usuarioLogado.getNome().toUpperCase());
        lblNome.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNome.setForeground(AppTheme.COR_BRANCO);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelSaudacao.add(lblBemVindo);
        painelSaudacao.add(lblNome);

        // CENTRO — campo de busca com placeholder via paintComponent
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        painelBusca.setBackground(AppTheme.COR_MENU);

        campoBuscaTopbar = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0x9C, 0xA3, 0xAF));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left + 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString("Buscar produto...", x, y);
                    g2.dispose();
                }
            }
        };
        campoBuscaTopbar.setPreferredSize(new Dimension(350, 30));
        campoBuscaTopbar.setFont(AppTheme.FONTE_DADOS);
        campoBuscaTopbar.setBackground(Color.WHITE);
        campoBuscaTopbar.setForeground(new Color(0x1F, 0x29, 0x37));
        campoBuscaTopbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        campoBuscaTopbar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    pesquisarNaTelaProduto(campoBuscaTopbar.getText().trim());
            }
        });
        campoBuscaTopbar.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { campoBuscaTopbar.repaint(); }
            @Override public void focusLost (FocusEvent e)  { campoBuscaTopbar.repaint(); }
        });
        JButton btnBuscar = new JButton("BUSCAR");
        btnBuscar.setBackground(AppTheme.COR_PRIMARIA);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFont(AppTheme.FONTE_BOLD);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setOpaque(true);
        btnBuscar.setPreferredSize(new Dimension(80, 30));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> pesquisarNaTelaProduto(campoBuscaTopbar.getText().trim()));

        painelBusca.add(campoBuscaTopbar);
        painelBusca.add(btnBuscar);

        // DIREITA — botão avatar redondo com popup
        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 7));
        painelDireita.setBackground(AppTheme.COR_MENU);

        final String iniciais = extrairIniciais(usuarioLogado.getNome());
        JButton btnAvatar = new JButton(iniciais) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.COR_PRIMARIA);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(txt, x, y);
                g2.dispose();
            }
        };
        btnAvatar.setContentAreaFilled(false);
        btnAvatar.setBorderPainted(false);
        btnAvatar.setFocusPainted(false);
        btnAvatar.setOpaque(false);
        btnAvatar.setPreferredSize(new Dimension(36, 36));
        btnAvatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAvatar.addActionListener(e -> mostrarMenuUsuario(btnAvatar));
        painelDireita.add(btnAvatar);

        topbar.add(painelSaudacao, BorderLayout.WEST);
        topbar.add(painelBusca,    BorderLayout.CENTER);
        topbar.add(painelDireita,  BorderLayout.EAST);

        getContentPane().add(topbar, BorderLayout.NORTH);
    }

    private void mostrarMenuUsuario(JButton origem) {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem itemPerfil = new JMenuItem("Meu Perfil");
        itemPerfil.setFont(AppTheme.FONTE_LABEL);
        itemPerfil.addActionListener(e -> {
            abrirUsuario();
            JInternalFrame f = desktop.getSelectedFrame();
            if (f instanceof TelaUsuario) ((TelaUsuario) f).editarUsuarioDireto(usuarioLogado);
        });

        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.setFont(AppTheme.FONTE_LABEL);
        itemSair.addActionListener(e -> { dispose(); new TelaLogin().setVisible(true); });

        popup.add(itemPerfil);
        popup.addSeparator();
        popup.add(itemSair);
        popup.show(origem, 0, origem.getHeight());
    }

    private void pesquisarNaTelaProduto(String termo) {
        if (termo == null || termo.isBlank()) return;
        System.out.println("[BUSCA] Iniciando busca por: " + termo);
        setItemAtivo(navCadastros);
        abrirProduto();
        for (JInternalFrame f : desktop.getAllFrames()) {
            if (f instanceof TelaProduto && !f.isClosed()) {
                System.out.println("[BUSCA] TelaProduto encontrada, disparando pesquisarDireto");
                ((TelaProduto) f).pesquisarDireto(termo);
                return;
            }
        }
        System.out.println("[BUSCA] TelaProduto nao encontrada no desktop");
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) return "?";
        String[] partes = nome.trim().toUpperCase().split("\\s+");
        if (partes.length >= 2) return "" + partes[0].charAt(0) + partes[partes.length - 1].charAt(0);
        return partes[0].length() >= 2 ? partes[0].substring(0, 2) : partes[0].substring(0, 1);
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
