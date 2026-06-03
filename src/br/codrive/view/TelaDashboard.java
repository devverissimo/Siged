/**
 * Classe: TelaDashboard
 * Objetivo: JInternalFrame exibido automaticamente ao iniciar o sistema.
 *           Apresenta quatro indicadores: total de produtos, total de categorias,
 *           produtos com estoque zerado e última movimentação registrada.
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
import java.awt.*;

public class TelaDashboard extends JInternalFrame {

    private static final Color COR_LABEL_MUTED = new Color(0x6B, 0x72, 0x80);

    private final DashboardDAO dao = new DashboardDAO();

    public TelaDashboard() {
        super("Dashboard", true, true, true, true);
        setSize(580, 360);
        construirInterface();
    }

    // -------------------------------------------------------------------------
    // Construção da interface
    // -------------------------------------------------------------------------

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(AppTheme.COR_FUNDO);

        raiz.add(construirCabecalho(), BorderLayout.NORTH);
        raiz.add(construirCards(),     BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private JPanel construirCabecalho() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        painel.setBackground(AppTheme.COR_MENU);

        JLabel lbl = new JLabel("PAINEL DE CONTROLE");
        lbl.setFont(AppTheme.FONTE_TITULO);
        lbl.setForeground(AppTheme.COR_PRIMARIA);
        painel.add(lbl);
        return painel;
    }

    private JPanel construirCards() {
        int totalProdutos   = 0;
        int totalCategorias = 0;
        int estoqueZerado   = 0;
        Movimentacao ultima = null;

        try {
            totalProdutos   = dao.contarProdutos();
            totalCategorias = dao.contarCategorias();
            estoqueZerado   = dao.contarEstoqueZerado();
            ultima          = dao.buscarUltimaMovimentacao();
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar dashboard:\n" + ex.getMessage());
        }

        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setBackground(AppTheme.COR_FUNDO);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        grid.add(criarCardNumero("PRODUTOS",       String.valueOf(totalProdutos),   "cadastrados"));
        grid.add(criarCardNumero("CATEGORIAS",     String.valueOf(totalCategorias), "cadastradas"));
        grid.add(criarCardNumero("ESTOQUE ZERADO", String.valueOf(estoqueZerado),   "produto(s) sem estoque"));
        grid.add(criarCardMovimentacao(ultima));

        return grid;
    }

    // -------------------------------------------------------------------------
    // Construção dos cards
    // -------------------------------------------------------------------------

    private JPanel criarCardNumero(String titulo, String numero, String descricao) {
        JPanel card = criarCard();

        JLabel lblTitulo = labelMuted(titulo, AppTheme.FONTE_BOLD);
        JLabel lblNumero = new JLabel(numero, SwingConstants.LEFT);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 38));
        lblNumero.setForeground(AppTheme.COR_PRIMARIA);
        JLabel lblDesc = labelMuted(descricao, AppTheme.FONTE_LABEL);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblNumero, BorderLayout.CENTER);
        card.add(lblDesc,   BorderLayout.SOUTH);
        return card;
    }

    private JPanel criarCardMovimentacao(Movimentacao m) {
        JPanel card = criarCard();

        JLabel lblTitulo = labelMuted("ÚLTIMA MOVIMENTAÇÃO", AppTheme.FONTE_BOLD);

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

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(corpo,     BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private JPanel criarCard() {
        JPanel card = AppTheme.criarPainelCard();
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
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