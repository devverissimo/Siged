/**
 * Classe: DashboardDAO
 * Objetivo: Queries agregadas usadas exclusivamente pelo TelaDashboard.
 *           Retorna contagens e a última movimentação registrada.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import br.codrive.model.Movimentacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public int contarProdutos() {
        return contar("SELECT COUNT(*) FROM produto");
    }

    public int contarCategorias() {
        return contar("SELECT COUNT(*) FROM categoria");
    }

    public int contarEstoqueZerado() {
        return contar("SELECT COUNT(*) FROM produto WHERE quantidade = 0");
    }

    public Movimentacao buscarUltimaMovimentacao() {
        String sql =
            "SELECT m.id, m.tipo, m.quantidade, m.data, m.observacao, " +
            "       m.id_produto, p.nome AS nome_produto " +
            "FROM movimentacao m " +
            "JOIN produto p ON p.id = m.id_produto " +
            "ORDER BY m.id DESC LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Movimentacao m = new Movimentacao(
                    rs.getInt("id"),
                    rs.getString("tipo"),
                    rs.getInt("quantidade"),
                    rs.getDate("data").toLocalDate(),
                    rs.getString("observacao"),
                    rs.getInt("id_produto")
                );
                m.setNomeProduto(rs.getString("nome_produto"));
                return m;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar última movimentação: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Object[]> buscarUltimasMovimentacoes(int limite) {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT m.data, p.nome, m.tipo, m.quantidade " +
            "FROM movimentacao m " +
            "JOIN produto p ON p.id = m.id_produto " +
            "ORDER BY m.id DESC " +
            "LIMIT ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String data = rs.getDate(1).toLocalDate()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    lista.add(new Object[]{
                        data,
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4)
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar últimas movimentações: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Object[]> buscarEstoqueCritico() {
        List<Object[]> lista = new ArrayList<>();
        String sql =
            "SELECT p.nome, p.quantidade, c.nome " +
            "FROM produto p " +
            "JOIN categoria c ON p.id_categoria = c.id " +
            "WHERE p.quantidade <= 5 " +
            "ORDER BY p.quantidade ASC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString(1),
                    rs.getInt(2),
                    rs.getString(3)
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estoque crítico: " + e.getMessage(), e);
        }
        return lista;
    }

    public Map<Integer, int[]> buscarMovimentacoesMensais() {
        Map<Integer, int[]> dados = new HashMap<>();
        String sql =
            "SELECT MONTH(data), tipo, SUM(quantidade) " +
            "FROM movimentacao " +
            "WHERE data >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
            "GROUP BY MONTH(data), tipo";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int    mes   = rs.getInt(1);
                String tipo  = rs.getString(2);
                int    total = rs.getInt(3);
                dados.computeIfAbsent(mes, k -> new int[2]);
                if ("ENTRADA".equals(tipo)) dados.get(mes)[0] += total;
                else                        dados.get(mes)[1] += total;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar movimentações mensais: " + e.getMessage(), e);
        }
        return dados;
    }

    public double calcularValorTotalEstoque() {
        String sql = "SELECT SUM(quantidade * valor) FROM produto WHERE quantidade > 0";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular valor total do estoque: " + e.getMessage(), e);
        }
        return 0.0;
    }

    private int contar(String sql) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar contagem: " + e.getMessage(), e);
        }
        return 0;
    }
}