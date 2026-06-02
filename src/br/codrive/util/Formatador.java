/**
 * Classe: Formatador
 * Objetivo: Centraliza a formatação e parsing de tipos de dados utilizados
 *           nas telas do sistema: moeda em Real brasileiro, datas no padrão
 *           DD/MM/AAAA e conversão de strings decimais com vírgula ou ponto.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Formatador {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale            LOCALE_BR = new Locale("pt", "BR");

    public static String formatarMoeda(double valor) {
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor);
    }

    public static String formatarData(LocalDate data) {
        if (data == null) return "";
        return data.format(FMT_DATA);
    }

    /**
     * Faz o parse de uma string no formato DD/MM/AAAA para LocalDate.
     * Lança DateTimeParseException se o formato for inválido.
     */
    public static LocalDate parseData(String dataStr) {
        return LocalDate.parse(dataStr.trim(), FMT_DATA);
    }

    /**
     * Aceita vírgula ou ponto como separador decimal — convenção brasileira.
     * Lança NumberFormatException se não for um número válido.
     */
    public static double parseDecimal(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new NumberFormatException("Valor vazio.");
        }
        return Double.parseDouble(texto.trim().replace(",", "."));
    }

    public static String formatarQuantidade(int qtd) {
        return String.valueOf(qtd);
    }

    public static String dataHoje() {
        return formatarData(LocalDate.now());
    }
}
