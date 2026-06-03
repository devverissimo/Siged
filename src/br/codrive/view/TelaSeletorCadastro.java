/**
 * Classe: TelaSeletorCadastro
 * Objetivo: JInternalFrame com três botões card para selecionar qual módulo
 *           de cadastro abrir (Categoria, Produto ou Usuário). Abre o módulo
 *           escolhido já em modo de novo cadastro e fecha a si mesmo.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.util.AppTheme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaSeletorCadastro extends JInternalFrame {

    private final TelaMenu menu;

    public TelaSeletorCadastro(TelaMenu menu) {
        super("Cadastros — Selecione o módulo", true, true, true, true);
        this.menu = menu;
        setSize(700, 340);
        construirInterface();
    }

    // -------------------------------------------------------------------------
    // Construção da interface
    // -------------------------------------------------------------------------

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(AppTheme.COR_FUNDO);

        // Cabeçalho
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        cabecalho.setBackground(AppTheme.COR_MENU);
        JLabel lblTitulo = new JLabel("SELECIONE O CADASTRO");
        lblTitulo.setFont(AppTheme.FONTE_TITULO);
        lblTitulo.setForeground(AppTheme.COR_PRIMARIA);
        cabecalho.add(lblTitulo);

        // Grade de cards
        JPanel grade = new JPanel(new GridLayout(1, 3, 16, 0));
        grade.setBackground(AppTheme.COR_FUNDO);
        grade.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        grade.add(criarCard("CATEGORIA",
            "Gerenciar categorias\nde produtos",
            () -> abrirModulo("categoria")));
        grade.add(criarCard("PRODUTO",
            "Gerenciar produtos\ndo estoque",
            () -> abrirModulo("produto")));
        grade.add(criarCard("USUÁRIO",
            "Gerenciar usuários\ndo sistema",
            () -> abrirModulo("usuario")));

        raiz.add(cabecalho, BorderLayout.NORTH);
        raiz.add(grade,     BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel criarCard(String titulo, String descricao, Runnable acao) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.COR_PAINEL);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Border bordaNormal  = BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1);
        Border bordaHover   = BorderFactory.createLineBorder(AppTheme.COR_PRIMARIA, 2);
        card.setBorder(bordaNormal);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(6, 12, 6, 12);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(AppTheme.COR_PRIMARIA);
        g.gridy = 0;
        card.add(lblTitulo, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.COR_BORDA);
        g.gridy = 1; g.insets = new Insets(4, 12, 4, 12);
        card.add(sep, g);

        for (String linha : descricao.split("\n")) {
            JLabel lblLinha = new JLabel(linha, SwingConstants.CENTER);
            lblLinha.setFont(AppTheme.FONTE_LABEL);
            lblLinha.setForeground(new Color(0x6B, 0x72, 0x80));
            g.gridy++;
            g.insets = new Insets(2, 12, 2, 12);
            card.add(lblLinha, g);
        }

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0xFF, 0xF7, 0xED));
                card.setBorder(bordaHover);
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(AppTheme.COR_PAINEL);
                card.setBorder(bordaNormal);
            }
            @Override public void mouseClicked(MouseEvent e) { acao.run(); }
        });

        return card;
    }

    // -------------------------------------------------------------------------
    // Ações
    // -------------------------------------------------------------------------

    private void abrirModulo(String tipo) {
        dispose();
        switch (tipo) {
            case "categoria": menu.abrirCategoria(); break;
            case "produto":   menu.abrirProduto();   break;
            default:          menu.abrirUsuario();   break;
        }
        JInternalFrame f = menu.getDesktop().getSelectedFrame();
        if (f instanceof ModuloAcoes) ((ModuloAcoes) f).acaoNovo();
    }
}
