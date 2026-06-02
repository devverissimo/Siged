/**
 * Classe: TelaConfiguracoes
 * Objetivo: JInternalFrame de configurações do sistema. Permite ao usuário
 *           alternar entre os idiomas PT-BR e EN via JComboBox, aplicando
 *           a troca imediatamente via Mensagem.setLocale().
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.util.AppTheme;
import br.codrive.util.Mensagem;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class TelaConfiguracoes extends JInternalFrame {

    private JComboBox<String> comboIdioma;

    public TelaConfiguracoes() {
        super(Mensagem.get("titulo.configuracoes"), true, true, false, true);
        setSize(400, 200);
        setResizable(false);
        construirInterface();
    }

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout(0, 10));
        raiz.setBackground(AppTheme.COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Card com a seleção de idioma
        JPanel card = AppTheme.criarPainelCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        JLabel lblIdioma = new JLabel(Mensagem.get("config.idioma.label"));
        lblIdioma.setFont(AppTheme.FONTE_BOLD);
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        card.add(lblIdioma, g);

        comboIdioma = new JComboBox<>(new String[]{
            Mensagem.get("config.idioma.ptbr"),
            Mensagem.get("config.idioma.en")
        });
        comboIdioma.setFont(AppTheme.FONTE_DADOS);
        comboIdioma.setSelectedIndex(
            Mensagem.getLocale().getLanguage().equals("en") ? 1 : 0);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        card.add(comboIdioma, g);

        raiz.add(card, BorderLayout.CENTER);

        // Botão SALVAR
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        painelBotao.setBackground(AppTheme.COR_FUNDO);

        JButton btnSalvar = new JButton(Mensagem.get("btn.salvar"));
        AppTheme.estilizarBotaoPrimario(btnSalvar);
        btnSalvar.addActionListener(e -> acaoSalvar());
        painelBotao.add(btnSalvar);

        raiz.add(painelBotao, BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private void acaoSalvar() {
        Locale novo = comboIdioma.getSelectedIndex() == 1
            ? new Locale("en")
            : new Locale("pt", "BR");
        Mensagem.setLocale(novo);
        Mensagem.sucesso(this, Mensagem.get("config.idioma.aviso"));
    }
}
