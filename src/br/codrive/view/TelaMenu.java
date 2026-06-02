/**
 * Classe: TelaMenu
 * Objetivo: Frame principal do sistema com JMenuBar, toolbar, sidebar,
 *           JDesktopPane e barra de status. Stub mínimo criado na Parte 9
 *           para permitir compilação da TelaLogin — implementado na Parte 10.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.Usuario;
import br.codrive.util.AppTheme;

import javax.swing.*;
import java.awt.*;

public class TelaMenu extends JFrame {

    // Stub — será substituído integralmente na Parte 10
    public TelaMenu(Usuario usuario) {
        setTitle("SIGED v1.0 — Sistema de Gerenciamento de Estoque para Distribuidoras [CoDrive]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(AppTheme.COR_DESKTOP);

        JLabel placeholder = new JLabel(
            "Bem-vindo, " + usuario.getNome() + "  —  TelaMenu será implementada na Parte 10",
            SwingConstants.CENTER
        );
        placeholder.setFont(AppTheme.FONTE_TITULO);
        placeholder.setForeground(AppTheme.COR_BRANCO);
        painel.add(placeholder, BorderLayout.CENTER);

        setContentPane(painel);
    }
}
