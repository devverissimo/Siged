/**
 * Classe: TelaLogin
 * Objetivo: Primeira janela exibida pelo sistema. Coleta login e senha do
 *           usuário, valida contra o banco via UsuarioService e, em caso de
 *           sucesso, abre a TelaMenu e encerra a si mesma.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Usuario;
import br.codrive.service.UsuarioService;
import br.codrive.util.AppTheme;
import br.codrive.util.Mensagem;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private JTextField     campoLogin;
    private JPasswordField campoSenha;
    private JButton        btnEntrar;

    private final UsuarioService service = new UsuarioService();

    public TelaLogin() {
        configurarJanela();
        construirInterface();
        configurarAcoes();
    }

    private void configurarJanela() {
        setTitle("SIGED v1.0 — Sistema de Gerenciamento de Estoque para Distribuidoras [CoDrive]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 400);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construirInterface() {
        // Painel externo com fundo escuro (#374151)
        JPanel painelExterno = new JPanel(new BorderLayout());
        painelExterno.setBackground(AppTheme.COR_MENU);
        painelExterno.setBorder(BorderFactory.createEmptyBorder(36, 48, 20, 48));

        // Card branco centralizado
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.COR_PAINEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.COR_BORDA, 1),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));

        GridBagConstraints g = new GridBagConstraints();

        // --- Título ---
        JLabel lblTitulo = new JLabel("SIGED v1.0", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(AppTheme.COR_PRIMARIA);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(0, 0, 2, 0);
        card.add(lblTitulo, g);

        // --- Subtítulo ---
        JLabel lblSub = new JLabel(Mensagem.get("login.subtitulo"), SwingConstants.CENTER);
        lblSub.setFont(AppTheme.FONTE_LABEL);
        lblSub.setForeground(AppTheme.COR_TEXTO);
        g.gridy = 1; g.insets = new Insets(0, 0, 0, 0);
        card.add(lblSub, g);

        // --- Separador ---
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.COR_BORDA);
        g.gridy = 2; g.insets = new Insets(12, 0, 16, 0);
        card.add(sep, g);

        // --- Label LOGIN ---
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        g.anchor = GridBagConstraints.EAST;
        g.insets = new Insets(6, 4, 6, 8);
        JLabel lblLogin = new JLabel(Mensagem.get("lbl.login") + ":");
        lblLogin.setFont(AppTheme.FONTE_BOLD);
        g.gridx = 0; g.gridy = 3;
        card.add(lblLogin, g);

        // --- Campo LOGIN ---
        campoLogin = new JTextField(15);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0; g.anchor = GridBagConstraints.WEST;
        card.add(campoLogin, g);

        // --- Label SENHA ---
        g.fill = GridBagConstraints.NONE; g.weightx = 0;
        g.anchor = GridBagConstraints.EAST;
        JLabel lblSenha = new JLabel(Mensagem.get("lbl.senha") + ":");
        lblSenha.setFont(AppTheme.FONTE_BOLD);
        g.gridx = 0; g.gridy = 4;
        card.add(lblSenha, g);

        // --- Campo SENHA ---
        campoSenha = new JPasswordField(15);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0; g.anchor = GridBagConstraints.WEST;
        card.add(campoSenha, g);

        // --- Espaçador ---
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        g.fill = GridBagConstraints.NONE; g.weightx = 0;
        g.insets = new Insets(4, 0, 4, 0);
        card.add(Box.createVerticalStrut(8), g);

        // --- Botão ENTRAR ---
        btnEntrar = new JButton(Mensagem.get("btn.entrar"));
        AppTheme.estilizarBotaoPrimario(btnEntrar);
        btnEntrar.setPreferredSize(new Dimension(0, 36));
        g.gridy = 6; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(4, 0, 0, 0);
        card.add(btnEntrar, g);

        // --- Label acesso restrito ---
        JLabel lblAcesso = new JLabel(
            "Acesso restrito — contate o administrador do sistema",
            SwingConstants.CENTER);
        lblAcesso.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblAcesso.setForeground(new Color(0x9C, 0xA3, 0xAF));
        g.gridy = 7; g.insets = new Insets(10, 0, 0, 0);
        card.add(lblAcesso, g);

        // --- Rodapé (fora do card) ---
        JLabel lblRodape = new JLabel("CMP1611 — PUC Goiás", SwingConstants.CENTER);
        lblRodape.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblRodape.setForeground(new Color(0x9C, 0xA3, 0xAF));
        lblRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        painelExterno.add(card, BorderLayout.CENTER);
        painelExterno.add(lblRodape, BorderLayout.SOUTH);
        setContentPane(painelExterno);
    }

    private void configurarAcoes() {
        btnEntrar.addActionListener(e -> acaoEntrar());
        campoSenha.addActionListener(e -> acaoEntrar());
        campoLogin.addActionListener(e -> campoSenha.requestFocus());
    }

    private void acaoEntrar() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            Mensagem.erro(this, Mensagem.get("login.campos.obrigatorios"));
            return;
        }

        try {
            Usuario usuario = service.autenticar(login, senha);
            if (usuario == null) {
                Mensagem.erro(this, Mensagem.get("login.credenciais.invalidas"));
                campoSenha.setText("");
                campoLogin.requestFocus();
            } else {
                new TelaMenu(usuario).setVisible(true);
                dispose();
            }
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao conectar com o banco de dados:\n" + ex.getMessage());
        }
    }
}
