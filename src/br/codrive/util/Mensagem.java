/**
 * Classe: Mensagem
 * Objetivo: Centraliza a exibição de diálogos JOptionPane e o acesso ao
 *           ResourceBundle de internacionalização. Toda a View usa esta
 *           classe para exibir erros, sucessos e obter strings traduzidas.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.util;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Mensagem {

    private static final String BASE_NAME = "br.codrive.i18n.messages";

    private static Locale         localeAtual = new Locale("pt", "BR");
    private static ResourceBundle bundle      = carregarBundle();

    private Mensagem() {}

    // -------------------------------------------------------------------------
    // Internacionalização
    // -------------------------------------------------------------------------

    private static ResourceBundle carregarBundle() {
        try {
            return ResourceBundle.getBundle(BASE_NAME, localeAtual);
        } catch (MissingResourceException e) {
            // Fallback para PT-BR se o locale solicitado não tiver arquivo
            return ResourceBundle.getBundle(BASE_NAME, new Locale("pt", "BR"));
        }
    }

    public static void setLocale(Locale locale) {
        localeAtual = locale;
        bundle = carregarBundle();
    }

    public static Locale getLocale() {
        return localeAtual;
    }

    /**
     * Retorna a string traduzida para a chave informada.
     * Se a chave não existir, retorna a própria chave como fallback.
     */
    public static String get(String chave) {
        try {
            return bundle.getString(chave);
        } catch (MissingResourceException e) {
            return chave;
        }
    }

    // -------------------------------------------------------------------------
    // Diálogos JOptionPane
    // -------------------------------------------------------------------------

    public static void erro(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(
            parent,
            mensagem,
            get("dialogo.titulo.erro"),
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static void sucesso(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(
            parent,
            mensagem,
            get("dialogo.titulo.sucesso"),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static boolean confirmar(Component parent, String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
            parent,
            mensagem,
            get("dialogo.titulo.confirmacao"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return resposta == JOptionPane.YES_OPTION;
    }
}
