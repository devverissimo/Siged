/**
 * Classe: UsuarioDAO
 * Objetivo: Responsável por todas as operações de persistência da entidade
 *           Usuário no banco de dados MySQL via JDBC puro. Oferece CRUD
 *           completo e busca por login utilizada na autenticação do sistema.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import br.codrive.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public int inserir(Usuario u) {
        boolean comId = u.getId() > 0;
        String sql = comId
            ? "INSERT INTO usuario (id, nome, login, senha) VALUES (?, ?, ?, ?)"
            : "INSERT INTO usuario (nome, login, senha) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (comId) {
                ps.setInt(1, u.getId());
                ps.setString(2, u.getNome());
                ps.setString(3, u.getLogin());
                ps.setString(4, u.getSenha());
            } else {
                ps.setString(1, u.getNome());
                ps.setString(2, u.getLogin());
                ps.setString(3, u.getSenha());
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return comId ? u.getId() : -1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    public void atualizar(Usuario u) {
        String sql = "UPDATE usuario SET nome = ?, login = ?, senha = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenha());
            ps.setInt(4, u.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, nome, login, senha FROM usuario WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por id: " + e.getMessage(), e);
        }
        return null;
    }

    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT id, nome, login, senha FROM usuario WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por login: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT id, nome, login, senha FROM usuario ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Usuario> buscarPorLoginOuNome(String termo) {
        String sql = "SELECT id, nome, login, senha FROM usuario " +
                     "WHERE login LIKE ? OR nome LIKE ? ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + termo + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean existePorLogin(String login) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade de login: " + e.getMessage(), e);
        }
        return false;
    }

    // Usado na validação de edição: ignora o próprio registro
    public boolean existePorLoginExcluindoId(String login, int idAtual) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setInt(2, idAtual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade de login: " + e.getMessage(), e);
        }
        return false;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("login"),
            rs.getString("senha")
        );
    }
}
