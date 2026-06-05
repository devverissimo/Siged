/**
 * Classe: LogAcessoDAO
 * Objetivo: Persistência dos registros de log de acesso (LOGIN/LOGOUT).
 *           Faz JOIN com usuario para retornar o objeto completo.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import br.codrive.model.LogAcesso;
import br.codrive.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogAcessoDAO {

    private static final String SELECT_BASE =
        "SELECT l.id, l.data_hora, l.acao, " +
        "       u.id AS uid, u.nome, u.login " +
        "FROM log_acesso l " +
        "JOIN usuario u ON u.id = l.id_usuario ";

    public void registrar(LogAcesso log) {
        String sql = "INSERT INTO log_acesso (id_usuario, data_hora, acao) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, log.getUsuario().getId());
            ps.setTimestamp(2, Timestamp.valueOf(log.getDataHora()));
            ps.setString(3, log.getAcao());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar log de acesso: " + e.getMessage(), e);
        }
    }

    public List<LogAcesso> listarPorUsuario(int idUsuario) {
        String sql = SELECT_BASE + "WHERE l.id_usuario = ? ORDER BY l.data_hora DESC";
        List<LogAcesso> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar log por usuário: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<LogAcesso> listarTodos() {
        String sql = SELECT_BASE + "ORDER BY l.data_hora DESC";
        List<LogAcesso> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar log de acessos: " + e.getMessage(), e);
        }
        return lista;
    }

    private LogAcesso mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(
            rs.getInt("uid"),
            rs.getString("nome"),
            rs.getString("login"),
            ""
        );
        return new LogAcesso(
            rs.getInt("id"),
            u,
            rs.getTimestamp("data_hora").toLocalDateTime(),
            rs.getString("acao")
        );
    }
}
