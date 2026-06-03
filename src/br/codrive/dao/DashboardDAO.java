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