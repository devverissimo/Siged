/**
 * Classe: Main
 * Objetivo: Ponto de entrada da aplicação SIGED CoDrive. Aplica o tema visual
 *           antes de qualquer componente Swing ser criado e exibe a TelaLogin
 *           na EDT (Event Dispatch Thread) conforme boas práticas do Swing.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive;

import br.codrive.util.AppTheme;
import br.codrive.view.TelaLogin;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        AppTheme.aplicar();
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
