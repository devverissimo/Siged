/**
 * Classe: AppTheme
 * Objetivo: Define a paleta de cores, fontes e helpers de estilização do
 *           sistema SIGED. Deve ser invocado via aplicar() antes de qualquer
 *           janela Swing ser criada, garantindo visual ERP consistente.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class AppTheme {

    // -------------------------------------------------------------------------
    // Paleta de cores (context.md)
    // -------------------------------------------------------------------------
    public static final Color COR_PRIMARIA  = new Color(0xD9, 0x77, 0x06); // #D97706 — laranja queimado
    public static final Color COR_FUNDO     = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC — fundo geral
    public static final Color COR_PAINEL    = Color.WHITE;                  // #FFFFFF — cards/formulários
    public static final Color COR_TEXTO     = new Color(0x1F, 0x29, 0x37); // #1F2937 — texto principal
    public static final Color COR_BORDA     = new Color(0xE5, 0xE7, 0xEB); // #E5E7EB — bordas/separadores
    public static final Color COR_MENU      = new Color(0x37, 0x41, 0x51); // #374151 — menu/sidebar
    public static final Color COR_BRANCO    = Color.WHITE;
    public static final Color COR_LINHA_PAR = new Color(0xF1, 0xF5, 0xF9); // linhas alternadas da JTable
    public static final Color COR_DESKTOP   = new Color(0x1E, 0x29, 0x3B); // fundo do JDesktopPane

    // -------------------------------------------------------------------------
    // Fontes
    // -------------------------------------------------------------------------
    public static final Font FONTE_DADOS      = new Font("Courier New", Font.PLAIN, 12); // campos e tabelas
    public static final Font FONTE_TABELA     = new Font("Courier New", Font.PLAIN, 11);
    public static final Font FONTE_LABEL      = new Font("SansSerif",   Font.PLAIN, 12); // labels e menus
    public static final Font FONTE_BOLD       = new Font("SansSerif",   Font.BOLD,  12);
    public static final Font FONTE_CAB_TABELA = new Font("SansSerif",   Font.BOLD,  11); // header da JTable
    public static final Font FONTE_TITULO     = new Font("SansSerif",   Font.BOLD,  14);

    private AppTheme() {}

    // -------------------------------------------------------------------------
    // Aplicação global via UIManager — chamar em main() antes de qualquer JFrame
    // -------------------------------------------------------------------------
    public static void aplicar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback: mantém L&F padrão da JVM
        }

        // Remove gradientes do Metal L&F (botões planos, estilo ERP)
        UIManager.put("Button.gradient",       null);
        UIManager.put("ToggleButton.gradient", null);
        UIManager.put("ScrollBar.gradient",    null);
        UIManager.put("CheckBox.gradient",     null);
        UIManager.put("RadioButton.gradient",  null);

        // --- Painéis e backgrounds ---
        UIManager.put("Panel.background",      COR_FUNDO);
        UIManager.put("OptionPane.background", COR_FUNDO);
        UIManager.put("Desktop.background",    COR_DESKTOP);

        // --- Campos de texto (monospace — Courier New) ---
        UIManager.put("TextField.background",        COR_PAINEL);
        UIManager.put("TextField.foreground",        COR_TEXTO);
        UIManager.put("TextField.font",              FONTE_DADOS);
        UIManager.put("PasswordField.font",          FONTE_DADOS);
        UIManager.put("TextArea.font",               FONTE_DADOS);
        UIManager.put("FormattedTextField.font",     FONTE_DADOS);
        UIManager.put("ComboBox.font",               FONTE_DADOS);
        UIManager.put("List.font",                   FONTE_DADOS);

        // --- Labels, botões e checkboxes (sans-serif) ---
        UIManager.put("Label.font",            FONTE_LABEL);
        UIManager.put("Label.foreground",      COR_TEXTO);
        UIManager.put("Button.font",           FONTE_BOLD);
        UIManager.put("CheckBox.font",         FONTE_LABEL);
        UIManager.put("RadioButton.font",      FONTE_LABEL);

        // --- Abas ---
        UIManager.put("TabbedPane.font",       FONTE_BOLD);
        UIManager.put("TabbedPane.background", COR_FUNDO);
        UIManager.put("TabbedPane.selected",   COR_PAINEL);

        // --- MenuBar escura (#374151) ---
        UIManager.put("MenuBar.background",           COR_MENU);
        UIManager.put("MenuBar.foreground",           COR_BRANCO);
        UIManager.put("MenuBar.border",               BorderFactory.createEmptyBorder());
        UIManager.put("Menu.background",              COR_MENU);
        UIManager.put("Menu.foreground",              COR_BRANCO);
        UIManager.put("Menu.selectionBackground",     COR_PRIMARIA);
        UIManager.put("Menu.selectionForeground",     COR_BRANCO);
        UIManager.put("Menu.font",                    FONTE_BOLD);
        UIManager.put("MenuItem.background",          COR_MENU);
        UIManager.put("MenuItem.foreground",          COR_BRANCO);
        UIManager.put("MenuItem.selectionBackground", COR_PRIMARIA);
        UIManager.put("MenuItem.selectionForeground", COR_BRANCO);
        UIManager.put("MenuItem.font",                FONTE_LABEL);
        UIManager.put("PopupMenu.background",         COR_MENU);
        UIManager.put("PopupMenu.border",             BorderFactory.createLineBorder(COR_BORDA));

        // --- JInternalFrame ---
        UIManager.put("InternalFrame.background",              COR_FUNDO);
        UIManager.put("InternalFrame.activeTitleBackground",   COR_PRIMARIA);
        UIManager.put("InternalFrame.activeTitleForeground",   COR_BRANCO);
        UIManager.put("InternalFrame.inactiveTitleBackground", COR_MENU);
        UIManager.put("InternalFrame.inactiveTitleForeground", COR_BRANCO);
        UIManager.put("InternalFrame.titleFont",               FONTE_BOLD);

        // --- ScrollPane ---
        UIManager.put("ScrollPane.background",  COR_FUNDO);
        UIManager.put("Viewport.background",    COR_PAINEL);

        // --- JTable (complementado por estilizarTabela()) ---
        UIManager.put("Table.font",                FONTE_TABELA);
        UIManager.put("Table.foreground",          COR_TEXTO);
        UIManager.put("Table.background",          COR_PAINEL);
        UIManager.put("Table.gridColor",           COR_BORDA);
        UIManager.put("Table.selectionBackground", COR_PRIMARIA);
        UIManager.put("Table.selectionForeground", COR_BRANCO);
        UIManager.put("TableHeader.background",    COR_MENU);
        UIManager.put("TableHeader.foreground",    COR_BRANCO);
        UIManager.put("TableHeader.font",          FONTE_CAB_TABELA);
    }

    // -------------------------------------------------------------------------
    // Estilização individual de componentes
    // -------------------------------------------------------------------------

    /**
     * Aplica cabeçalho escuro, linhas alternadas e fonte monospace na JTable.
     * Chamar após criar a JTable e definir o model.
     */
    public static void estilizarTabela(JTable tabela) {
        tabela.setFont(FONTE_TABELA);
        tabela.setForeground(COR_TEXTO);
        tabela.setBackground(COR_PAINEL);
        tabela.setRowHeight(22);
        tabela.setGridColor(COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(COR_PRIMARIA);
        tabela.setSelectionForeground(COR_BRANCO);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabela.getTableHeader().setReorderingAllowed(false);

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(COR_MENU);
        header.setForeground(COR_BRANCO);
        header.setFont(FONTE_CAB_TABELA);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? COR_PAINEL : COR_LINHA_PAR);
                    setForeground(COR_TEXTO);
                }
                setFont(FONTE_TABELA);
                return this;
            }
        });
    }

    /** Botão laranja — ações primárias: SALVAR, REGISTRAR, ENTRAR. */
    public static void estilizarBotaoPrimario(JButton botao) {
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(COR_BRANCO);
        botao.setFont(FONTE_BOLD);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Botão escuro — toolbar ([F2] NOVO, [F3] SALVAR, etc.) e ações secundárias. */
    public static void estilizarBotaoToolbar(JButton botao) {
        botao.setBackground(COR_MENU);
        botao.setForeground(COR_BRANCO);
        botao.setFont(FONTE_BOLD);
        botao.setFocusPainted(false);
        botao.setBorderPainted(true);
        botao.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        botao.setOpaque(true);
        botao.setMargin(new Insets(4, 10, 4, 10));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Painel branco com borda fina — estilo "card" para formulários. */
    public static JPanel criarPainelCard() {
        JPanel painel = new JPanel();
        painel.setBackground(COR_PAINEL);
        painel.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1));
        return painel;
    }
}
