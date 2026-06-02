/**
 * Classe: CategoriaDAO
 * Objetivo: Responsável por todas as operações de persistência da entidade
 *           Categoria no banco de dados MySQL via JDBC puro. Oferece CRUD
 *           completo e queries de busca utilizadas pela camada Service e View.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import br.codrive.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public int inserir(Categoria c) {
        String sql = "INSERT INTO categoria (nome) VALUES (?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNome());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir categoria: " + e.getMessage(), e);
        }
        return -1;
    }

    public void atualizar(Categoria c) {
        String sql = "UPDATE categoria SET nome = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNome());
            ps.setInt(2, c.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir categoria: " + e.getMessage(), e);
        }
    }

    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, nome FROM categoria WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria por id: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Categoria> listarTodos() {
        String sql = "SELECT id, nome FROM categoria ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Categoria> buscarPorNome(String nome) {
        String sql = "SELECT id, nome FROM categoria WHERE nome LIKE ? ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categorias por nome: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean existePorNome(String nome) {
        String sql = "SELECT COUNT(*) FROM categoria WHERE nome = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade de categoria: " + e.getMessage(), e);
        }
        return false;
    }

    // Usado na validação de edição: ignora o próprio registro
    public boolean existePorNomeExcluindoId(String nome, int idAtual) {
        String sql = "SELECT COUNT(*) FROM categoria WHERE nome = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setInt(2, idAtual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade de categoria: " + e.getMessage(), e);
        }
        return false;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("nome"));
    }
}
