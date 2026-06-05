/**
 * Classe: MovimentacaoDAO
 * Objetivo: Responsável por todas as operações de persistência da entidade
 *           Movimentação. O método registrar executa INSERT na movimentacao
 *           e UPDATE no estoque do produto dentro de uma única transação JDBC.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.dao;

import br.codrive.model.Movimentacao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    // Critérios válidos para listarOrdenado — mapeados internamente para SQL seguro
    public static final String ORDEM_DATA_ASC   = "DATA_ASC";
    public static final String ORDEM_DATA_DESC  = "DATA_DESC";
    public static final String ORDEM_PRODUTO_AZ = "PRODUTO_AZ";
    public static final String ORDEM_TIPO       = "TIPO";

    private static final String SELECT_BASE =
        "SELECT m.id, m.tipo, m.quantidade, m.data, m.observacao, " +
        "       m.id_produto, m.id_usuario, " +
        "       p.nome AS nome_produto, " +
        "       u.nome AS nome_responsavel " +
        "FROM movimentacao m " +
        "JOIN produto p ON p.id = m.id_produto " +
        "LEFT JOIN usuario u ON u.id = m.id_usuario ";

    /**
     * Registra uma movimentação e atualiza o estoque do produto
     * em uma única transação — commit ou rollback completo.
     */
    public void registrar(Movimentacao m) {
        String sqlInsert = "INSERT INTO movimentacao " +
                           "(tipo, quantidade, data, observacao, id_produto, id_usuario) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";

        String sqlUpdate = "ENTRADA".equals(m.getTipo())
            ? "UPDATE produto SET quantidade = quantidade + ? WHERE id = ?"
            : "UPDATE produto SET quantidade = quantidade - ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setString(1, m.getTipo());
                psInsert.setInt(2, m.getQuantidade());
                psInsert.setDate(3, Date.valueOf(m.getData()));
                psInsert.setString(4, m.getObservacao());
                psInsert.setInt(5, m.getIdProduto());
                if (m.getIdUsuario() > 0) psInsert.setInt(6, m.getIdUsuario());
                else                      psInsert.setNull(6, Types.INTEGER);
                psInsert.executeUpdate();
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, m.getQuantidade());
                psUpdate.setInt(2, m.getIdProduto());
                psUpdate.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignora */ }
            }
            throw new RuntimeException("Erro ao registrar movimentação: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignora */ }
            }
        }
    }

    // Retorna as N movimentações mais recentes — para o histórico na TelaMovimentacao
    public List<Movimentacao> listarRecentes(int limite) {
        String sql = SELECT_BASE + "ORDER BY m.id DESC LIMIT ?";
        List<Movimentacao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar movimentações recentes: " + e.getMessage(), e);
        }
        return lista;
    }

    // Listagem com ordenação segura para a TelaListagem
    public List<Movimentacao> listarOrdenado(String criterio) {
        String orderBy;
        switch (criterio) {
            case ORDEM_DATA_ASC:   orderBy = "m.data ASC,  m.id ASC";   break;
            case ORDEM_PRODUTO_AZ: orderBy = "p.nome ASC,  m.data DESC"; break;
            case ORDEM_TIPO:       orderBy = "m.tipo ASC,  m.data DESC"; break;
            case ORDEM_DATA_DESC:
            default:               orderBy = "m.data DESC, m.id DESC";
        }

        String sql = SELECT_BASE + "ORDER BY " + orderBy;
        List<Movimentacao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar movimentações: " + e.getMessage(), e);
        }
        return lista;
    }

    // Listagem filtrada por período — para a barra de filtros da TelaListagem
    public List<Movimentacao> listarPorPeriodo(Date dataInicio, Date dataFim) {
        String sql = SELECT_BASE +
            "WHERE m.data BETWEEN ? AND ? " +
            "ORDER BY m.data DESC, m.id DESC";
        List<Movimentacao> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, dataInicio);
            ps.setDate(2, dataFim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar movimentações por período: " + e.getMessage(), e);
        }
        return lista;
    }

    private Movimentacao mapear(ResultSet rs) throws SQLException {
        Movimentacao m = new Movimentacao(
            rs.getInt("id"),
            rs.getString("tipo"),
            rs.getInt("quantidade"),
            rs.getDate("data").toLocalDate(),
            rs.getString("observacao"),
            rs.getInt("id_produto")
        );
        m.setIdUsuario(rs.getInt("id_usuario"));
        m.setNomeProduto(rs.getString("nome_produto"));
        m.setNomeResponsavel(rs.getString("nome_responsavel"));
        return m;
    }
}
