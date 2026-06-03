/**
 * Classe: ProdutoDAO
 * Objetivo: Responsável por todas as operações de persistência da entidade
 *           Produto no banco de dados MySQL via JDBC puro. Todas as queries
 *           fazem JOIN com categoria para popular o campo nomeCategoria.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import br.codrive.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private static final String SELECT_BASE =
        "SELECT p.id, p.nome, p.valor, p.quantidade, p.id_categoria, " +
        "       c.nome AS nome_categoria " +
        "FROM produto p " +
        "LEFT JOIN categoria c ON c.id = p.id_categoria ";

    // Novo produto sempre começa com quantidade = 0 (regra de negócio)
    public int inserir(Produto p) {
        boolean comId = p.getId() > 0;
        String sql = comId
            ? "INSERT INTO produto (id, nome, valor, id_categoria) VALUES (?, ?, ?, ?)"
            : "INSERT INTO produto (nome, valor, id_categoria) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (comId) {
                ps.setInt(1, p.getId());
                ps.setString(2, p.getNome());
                ps.setDouble(3, p.getValor());
                ps.setInt(4, p.getIdCategoria());
            } else {
                ps.setString(1, p.getNome());
                ps.setDouble(2, p.getValor());
                ps.setInt(3, p.getIdCategoria());
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return comId ? p.getId() : -1;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir produto: " + e.getMessage(), e);
        }
    }

    // Atualiza apenas nome, valor e categoria — quantidade é exclusiva da movimentação
    public void atualizar(Produto p) {
        String sql = "UPDATE produto SET nome = ?, valor = ?, id_categoria = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setDouble(2, p.getValor());
            ps.setInt(3, p.getIdCategoria());
            ps.setInt(4, p.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir produto: " + e.getMessage(), e);
        }
    }

    public Produto buscarPorId(int id) {
        String sql = SELECT_BASE + "WHERE p.id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por id: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Produto> listarTodos() {
        String sql = SELECT_BASE + "ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Produto> buscarPorNome(String nome) {
        String sql = SELECT_BASE + "WHERE p.nome LIKE ? ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por nome: " + e.getMessage(), e);
        }
        return lista;
    }

    // Usado na busca "POR CATEGORIA" da TelaProduto (digita nome parcial da categoria)
    public List<Produto> buscarPorNomeCategoria(String nomeCategoria) {
        String sql = SELECT_BASE + "WHERE c.nome LIKE ? ORDER BY p.nome";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nomeCategoria + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean existePorNome(String nome) {
        String sql = "SELECT COUNT(*) FROM produto WHERE nome = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade de produto: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean existePorNomeExcluindoId(String nome, int idAtual) {
        String sql = "SELECT COUNT(*) FROM produto WHERE nome = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.setInt(2, idAtual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicidade de produto: " + e.getMessage(), e);
        }
        return false;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getDouble("valor"),
            rs.getInt("quantidade"),
            rs.getInt("id_categoria")
        );
        p.setNomeCategoria(rs.getString("nome_categoria"));
        return p;
    }
}
