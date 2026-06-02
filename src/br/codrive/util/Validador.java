/**
 * Classe: Validador
 * Objetivo: Fornece DocumentFilters para campos numéricos em Swing e métodos
 *           estáticos de validação de entrada utilizados pelas telas do sistema.
 *           Previne digitação de caracteres inválidos antes do submit do form.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.util;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Validador {

    // -------------------------------------------------------------------------
    // DocumentFilter — apenas dígitos inteiros (para campo Quantidade)
    // -------------------------------------------------------------------------
    public static class FiltroInteiro extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // -------------------------------------------------------------------------
    // DocumentFilter — dígitos + um separador decimal (para campo Valor)
    // -------------------------------------------------------------------------
    public static class FiltroDecimal extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {
            if (string != null && resultadoValido(fb, offset, 0, string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            if (text != null && resultadoValido(fb, offset, length, text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean resultadoValido(FilterBypass fb, int offset, int length, String insert) {
            try {
                String atual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String resultado = atual.substring(0, offset) + insert + atual.substring(offset + length);
                // Aceita vazio, dígitos e no máximo um separador decimal (. ou ,)
                return resultado.matches("\\d*[.,]?\\d{0,2}");
            } catch (BadLocationException e) {
                return false;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares para aplicar filtros nos JTextFields
    // -------------------------------------------------------------------------

    public static void aplicarFiltroInteiro(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroInteiro());
    }

    public static void aplicarFiltroDecimal(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroDecimal());
    }

    // -------------------------------------------------------------------------
    // Validações de conteúdo
    // -------------------------------------------------------------------------

    public static boolean isDataValida(String data) {
        if (data == null || !data.matches("\\d{2}/\\d{2}/\\d{4}")) return false;
        try {
            Formatador.parseData(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInteiroValido(String texto) {
        if (texto == null || texto.isBlank()) return false;
        try {
            Integer.parseInt(texto.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
