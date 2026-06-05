/**
 * Classe: TelaDashboard
 * Objetivo: JInternalFrame exibido automaticamente ao iniciar o sistema.
 *           Apresenta quatro indicadores, gráfico de movimentações mensais,
 *           seção de estoque crítico e tabela de últimas movimentações.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.dao.DashboardDAO;
import br.codrive.model.Movimentacao;
import br.codrive.util.AppTheme;
import br.codrive.util.Formatador;
import br.codrive.util.Mensagem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaDashboard extends JInternalFrame {

    private static final Color COR_LABEL_MUTED = new Color(0x6B, 0x72, 0x80);

    private static final String[] NOMES_MESES = {
        "", "JAN", "FEV", "MAR", "ABR", "MAI", "JUN",
        "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"
    };

    private final DashboardDAO dao = new DashboardDAO();
    private JPanel painelPrincipal;

    public TelaDashboard() {
        super("Dashboard", true, true, true, true);
        setSize(950, 680);
        construirInterface();
    }

    // -------------------------------------------------------------------------
    // Construção da interface
    // -------------------------------------------------------------------------

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(AppTheme.COR_FUNDO);
        raiz.add(construirCabecalho(), BorderLayout.NORTH);
        painelPrincipal = construirPrincipal();
        raiz.add(painelPrincipal, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel construirCabecalho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(AppTheme.COR_MENU);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel lbl = new JLabel("PAINEL DE CONTROLE");
        lbl.setFont(AppTheme.FONTE_TITULO);
        lbl.setForeground(AppTheme.COR_PRIMARIA);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnAtualizar = new JButton("ATUALIZAR");
        AppTheme.estilizarBotaoSecundario(btnAtualizar);
        btnAtualizar.addActionListener(e -> recarregar());

        painel.add(lbl,          BorderLayout.WEST);
        painel.add(btnAtualizar, BorderLayout.EAST);
        return painel;
    }

    private void recarregar() {
        java.awt.Container raiz = getContentPane();
        raiz.remove(painelPrincipal);
        painelPrincipal = construirPrincipal();
        raiz.add(painelPrincipal, BorderLayout.CENTER);
        raiz.revalidate();
        raiz.repaint();
    }

    private JPanel construirPrincipal() {
        int totalProdutos   = 0;
        int totalCategorias = 0;
        int estoqueZerado   = 0;
        double valorTotalEstoque     = 0.0;
        Movimentacao ultima = null;
        Map<Integer, int[]> dadosMensais        = new HashMap<>();
        List<Object[]>      estoqueCritico       = new ArrayList<>();
        List<Object[]>      ultimasMovimentacoes = new ArrayList<>();

        try {
            totalProdutos        = dao.contarProdutos();
            totalCategorias      = dao.contarCategorias();
            estoqueZerado        = dao.contarEstoqueZerado();
            valorTotalEstoque    = dao.calcularValorTotalEstoque();
            ultima               = dao.buscarUltimaMovimentacao();
            dadosMensais         = dao.buscarMovimentacoesMensais();
            estoqueCritico       = dao.buscarEstoqueCritico();
            ultimasMovimentacoes = dao.buscarUltimasMovimentacoes(10);
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar dashboard:\n" + ex.getMessage());
        }

        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painel.add(construirCards(totalProdutos, totalCategorias, estoqueZerado, ultima,
                                  valorTotalEstoque),
                   BorderLayout.NORTH);

        JPanel painelMeio = new JPanel(new BorderLayout(8, 0));
        painelMeio.setBackground(AppTheme.COR_FUNDO);
        painelMeio.add(new GraficoBarra(dadosMensais),               BorderLayout.CENTER);
        painelMeio.add(construirPainelEstoqueCritico(estoqueCritico), BorderLayout.EAST);

        painel.add(painelMeio,                                               BorderLayout.CENTER);
        painel.add(construirPainelMovimentacoes(ultimasMovimentacoes), BorderLayout.SOUTH);

        return painel;
    }

    private JPanel construirCards(int totalProdutos, int totalCategorias,
                                   int estoqueZerado, Movimentacao ultima,
                                   double valorTotalEstoque) {
        JPanel grid = new JPanel(new GridLayout(1, 5, 8, 0));
        grid.setBackground(AppTheme.COR_FUNDO);

        grid.add(criarCardNumero("PRODUTOS",       String.valueOf(totalProdutos),   "[PKG]"));
        grid.add(criarCardNumero("CATEGORIAS",     String.valueOf(totalCategorias), "[TAG]"));
        grid.add(criarCardNumero("ESTOQUE ZERADO", String.valueOf(estoqueZerado),   "[!!]"));
        grid.add(criarCardMovimentacao(ultima));
        grid.add(criarCardValor("VALOR EM ESTOQUE", Formatador.formatarMoeda(valorTotalEstoque), "[R$]"));

        return grid;
    }

    // -------------------------------------------------------------------------
    // Construção dos cards
    // -------------------------------------------------------------------------

    private JPanel criarCardNumero(String titulo, String numero, String icone) {
        JPanel card = criarCard();

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(AppTheme.COR_PAINEL);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitulo.setForeground(COR_LABEL_MUTED);

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblIcone.setForeground(AppTheme.COR_PRIMARIA);

        topRow.add(lblTitulo, BorderLayout.WEST);
        topRow.add(lblIcone,  BorderLayout.EAST);

        JLabel lblNumero = new JLabel(numero, SwingConstants.LEFT);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblNumero.setForeground(AppTheme.COR_TEXTO);

        card.add(topRow,    BorderLayout.NORTH);
        card.add(lblNumero, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardValor(String titulo, String valor, String icone) {
        JPanel card = criarCard();

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(AppTheme.COR_PAINEL);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitulo.setForeground(COR_LABEL_MUTED);

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblIcone.setForeground(AppTheme.COR_PRIMARIA);

        topRow.add(lblTitulo, BorderLayout.WEST);
        topRow.add(lblIcone,  BorderLayout.EAST);

        JLabel lblValor = new JLabel(valor, SwingConstants.LEFT);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblValor.setForeground(AppTheme.COR_TEXTO);

        card.add(topRow,   BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardMovimentacao(Movimentacao m) {
        JPanel card = criarCard();

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(AppTheme.COR_PAINEL);

        JLabel lblTitulo = new JLabel("ÚLTIMA MOVIMENTAÇÃO");
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitulo.setForeground(COR_LABEL_MUTED);

        JLabel lblIcone = new JLabel("[MOV]");
        lblIcone.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblIcone.setForeground(AppTheme.COR_PRIMARIA);

        topRow.add(lblTitulo, BorderLayout.WEST);
        topRow.add(lblIcone,  BorderLayout.EAST);

        JPanel corpo = new JPanel(new GridLayout(2, 1, 0, 6));
        corpo.setBackground(AppTheme.COR_PAINEL);

        if (m == null) {
            corpo.add(labelMuted("Nenhuma movimentação", AppTheme.FONTE_LABEL));
            corpo.add(new JLabel(""));
        } else {
            JLabel lblProduto = new JLabel(m.getNomeProduto());
            lblProduto.setFont(AppTheme.FONTE_BOLD);
            lblProduto.setForeground(AppTheme.COR_TEXTO);

            JLabel lblDetalhe = new JLabel(m.getTipo() + "  •  " + Formatador.formatarData(m.getData()));
            lblDetalhe.setFont(AppTheme.FONTE_LABEL);
            lblDetalhe.setForeground(AppTheme.COR_PRIMARIA);

            corpo.add(lblProduto);
            corpo.add(lblDetalhe);
        }

        card.add(topRow, BorderLayout.NORTH);
        card.add(corpo,  BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Seção de estoque crítico
    // -------------------------------------------------------------------------

    private static final Color COR_LINHA_ZERO    = new Color(0xFE, 0xE2, 0xE2);
    private static final Color COR_LINHA_CRITICA = new Color(0xFE, 0xF3, 0xC7);
    private static final Color COR_VERDE         = new Color(0x06, 0x5F, 0x46);

    private static final Color COR_ZEBRA_PAR     = new Color(0xF9, 0xFA, 0xFB);
    private static final Color COR_ENTRADA_BG    = new Color(0xD1, 0xFA, 0xE5);
    private static final Color COR_ENTRADA_FG    = new Color(0x06, 0x5F, 0x46);
    private static final Color COR_SAIDA_BG      = new Color(0xFE, 0xE2, 0xE2);
    private static final Color COR_SAIDA_FG      = new Color(0x7F, 0x1D, 0x1D);

    private JPanel construirPainelEstoqueCritico(List<Object[]> dados) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        painel.setPreferredSize(new Dimension(280, 0));

        JLabel lblTitulo = new JLabel("ESTOQUE CRÍTICO");
        lblTitulo.setFont(AppTheme.FONTE_BOLD);
        lblTitulo.setForeground(AppTheme.COR_PRIMARIA);
        painel.add(lblTitulo, BorderLayout.NORTH);

        if (dados.isEmpty()) {
            JLabel lblVazio = new JLabel(
                "<html><div style='text-align:center'>Nenhum produto em<br>estoque crítico</div></html>",
                SwingConstants.CENTER);
            lblVazio.setFont(AppTheme.FONTE_LABEL);
            lblVazio.setForeground(COR_VERDE);
            painel.add(lblVazio, BorderLayout.CENTER);
            return painel;
        }

        String[]   colunas = {"PRODUTO", "QTD.", "CATEGORIA"};
        Object[][] linhas  = new Object[dados.size()][3];
        for (int i = 0; i < dados.size(); i++) {
            linhas[i][0] = dados.get(i)[0];
            linhas[i][1] = dados.get(i)[1];
            linhas[i][2] = dados.get(i)[2];
        }

        JTable tabela = new JTable(linhas, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela.setFont(AppTheme.FONTE_TABELA);
        tabela.setRowHeight(22);
        tabela.setGridColor(AppTheme.COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(AppTheme.COR_PRIMARIA);
        tabela.setSelectionForeground(AppTheme.COR_BRANCO);
        tabela.getTableHeader().setBackground(AppTheme.COR_MENU);
        tabela.getTableHeader().setForeground(AppTheme.COR_BRANCO);
        tabela.getTableHeader().setFont(AppTheme.FONTE_CAB_TABELA);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setMaxWidth(55);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    int qtd = (Integer) table.getValueAt(row, 1);
                    setBackground(qtd == 0 ? COR_LINHA_ZERO : COR_LINHA_CRITICA);
                    setForeground(AppTheme.COR_TEXTO);
                }
                setFont(AppTheme.FONTE_TABELA);
                return this;
            }
        });

        int linhasVisiveis = Math.min(8, dados.size());
        tabela.setPreferredScrollableViewportSize(
            new Dimension(0, linhasVisiveis * tabela.getRowHeight()));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1));
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    // -------------------------------------------------------------------------
    // Gráfico de barras (Graphics2D — sem bibliotecas externas)
    // -------------------------------------------------------------------------

    private class GraficoBarra extends JPanel {

        private final Map<Integer, int[]> dados;
        private final int[] meses;

        GraficoBarra(Map<Integer, int[]> dados) {
            this.dados = dados;
            this.meses = calcularUltimosMeses();
            setBackground(AppTheme.COR_PAINEL);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));
        }

        private int[] calcularUltimosMeses() {
            Calendar cal = Calendar.getInstance();
            int[] m = new int[6];
            for (int i = 5; i >= 0; i--) {
                m[i] = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH é 0-based
                cal.add(Calendar.MONTH, -1);
            }
            return m;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int maxVal = 1;
            for (int mes : meses) {
                int[] d = dados.getOrDefault(mes, new int[2]);
                maxVal = Math.max(maxVal, Math.max(d[0], d[1]));
            }

            // Margens internas da área de plotagem
            int padLeft   = 48;
            int padRight  = 16;
            int padTop    = 28;
            int padBottom = 46;
            int chartW    = w - padLeft - padRight;
            int chartH    = h - padTop - padBottom;

            Color corEntrada = new Color(0xD9, 0x77, 0x06);
            Color corSaida   = new Color(0x9C, 0xA3, 0xAF);

            // Título
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(AppTheme.COR_TEXTO);
            g2.drawString("MOVIMENTAÇÕES — ÚLTIMOS 6 MESES", padLeft, padTop - 8);

            // Grid horizontal + rótulos do eixo Y
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            int gridLines = 4;
            for (int i = 0; i <= gridLines; i++) {
                int y   = padTop + chartH - i * chartH / gridLines;
                int val = i * maxVal / gridLines;
                g2.setColor(new Color(0xE5, 0xE7, 0xEB));
                g2.drawLine(padLeft, y, padLeft + chartW, y);
                g2.setColor(COR_LABEL_MUTED);
                String s  = String.valueOf(val);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(s, padLeft - fm.stringWidth(s) - 4, y + 4);
            }

            // Barras
            int groupW   = chartW / 6;
            int barW     = Math.max(4, groupW / 3);
            int barGap   = 2;
            int groupOff = (groupW - barW * 2 - barGap) / 2;

            for (int i = 0; i < 6; i++) {
                int mes = meses[i];
                int[] d = dados.getOrDefault(mes, new int[2]);
                int gx  = padLeft + i * groupW + groupOff;

                // Barra ENTRADA
                int eH = maxVal == 0 ? 0 : (int)((double) d[0] / maxVal * chartH);
                g2.setColor(corEntrada);
                g2.fillRect(gx, padTop + chartH - eH, barW, Math.max(eH, 1));

                // Barra SAÍDA
                int sH = maxVal == 0 ? 0 : (int)((double) d[1] / maxVal * chartH);
                g2.setColor(corSaida);
                g2.fillRect(gx + barW + barGap, padTop + chartH - sH, barW, Math.max(sH, 1));

                // Rótulo do mês
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.setColor(COR_LABEL_MUTED);
                String nome = NOMES_MESES[mes];
                FontMetrics fm = g2.getFontMetrics();
                int cx = gx + (barW * 2 + barGap) / 2 - fm.stringWidth(nome) / 2;
                g2.drawString(nome, cx, padTop + chartH + 14);
            }

            // Eixos
            g2.setColor(new Color(0xD1, 0xD5, 0xDB));
            g2.drawLine(padLeft, padTop,           padLeft,           padTop + chartH);
            g2.drawLine(padLeft, padTop + chartH,  padLeft + chartW,  padTop + chartH);

            // Legenda
            int ly = h - 12;
            int lx = padLeft;
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));

            g2.setColor(corEntrada);
            g2.fillRect(lx, ly - 9, 10, 10);
            g2.setColor(AppTheme.COR_TEXTO);
            g2.drawString("ENTRADA", lx + 14, ly);

            lx += 80;
            g2.setColor(corSaida);
            g2.fillRect(lx, ly - 9, 10, 10);
            g2.setColor(AppTheme.COR_TEXTO);
            g2.drawString("SAÍDA", lx + 14, ly);
        }
    }

    // -------------------------------------------------------------------------
    // Tabela de últimas movimentações
    // -------------------------------------------------------------------------

    private JPanel construirPainelMovimentacoes(List<Object[]> dados) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(AppTheme.COR_PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel lblTitulo = new JLabel("ÚLTIMAS MOVIMENTAÇÕES");
        lblTitulo.setFont(AppTheme.FONTE_BOLD);
        lblTitulo.setForeground(AppTheme.COR_TEXTO);
        painel.add(lblTitulo, BorderLayout.NORTH);

        if (dados.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhuma movimentação registrada", SwingConstants.CENTER);
            lblVazio.setFont(AppTheme.FONTE_LABEL);
            lblVazio.setForeground(COR_LABEL_MUTED);
            painel.add(lblVazio, BorderLayout.CENTER);
            return painel;
        }

        String[]   colunas = {"DATA", "PRODUTO", "TIPO", "QTD."};
        Object[][] linhas  = new Object[dados.size()][4];
        for (int i = 0; i < dados.size(); i++) {
            linhas[i][0] = dados.get(i)[0]; // String "dd/MM/yyyy"
            linhas[i][1] = dados.get(i)[1]; // nome produto
            linhas[i][2] = dados.get(i)[2]; // tipo
            linhas[i][3] = dados.get(i)[3]; // Integer qtd
        }

        JTable tabela = new JTable(linhas, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela.setFont(AppTheme.FONTE_TABELA);
        tabela.setRowHeight(22);
        tabela.setGridColor(AppTheme.COR_BORDA);
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(AppTheme.COR_PRIMARIA);
        tabela.setSelectionForeground(AppTheme.COR_BRANCO);
        tabela.getTableHeader().setBackground(AppTheme.COR_MENU);
        tabela.getTableHeader().setForeground(AppTheme.COR_BRANCO);
        tabela.getTableHeader().setFont(AppTheme.FONTE_CAB_TABELA);
        tabela.getTableHeader().setReorderingAllowed(false);

        // Zebra padrão para todas as colunas
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? COR_ZEBRA_PAR : Color.WHITE);
                    setForeground(AppTheme.COR_TEXTO);
                }
                setFont(AppTheme.FONTE_TABELA);
                return this;
            }
        });

        // Badge colorido para a coluna TIPO (índice 2)
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if ("ENTRADA".equals(value)) {
                        setBackground(COR_ENTRADA_BG);
                        setForeground(COR_ENTRADA_FG);
                    } else {
                        setBackground(COR_SAIDA_BG);
                        setForeground(COR_SAIDA_FG);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(AppTheme.FONTE_TABELA);
                return this;
            }
        });

        // Larguras das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(0).setMaxWidth(90);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(2).setMaxWidth(80);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(3).setMaxWidth(55);

        int linhasVisiveis = Math.min(6, dados.size());
        tabela.setPreferredScrollableViewportSize(
            new Dimension(0, linhasVisiveis * tabela.getRowHeight()));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1));
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private JPanel criarCard() {
        JPanel card = AppTheme.criarPainelCard();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        return card;
    }

    private JLabel labelMuted(String texto, Font fonte) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte);
        l.setForeground(COR_LABEL_MUTED);
        return l;
    }
}
