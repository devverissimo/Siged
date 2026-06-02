/**
 * Classe: MovimentacaoService
 * Objetivo: Contém as regras de negócio da entidade Movimentação, incluindo
 *           a validação de estoque suficiente para SAÍDA antes de delegar
 *           o registro (INSERT + UPDATE em transação) ao MovimentacaoDAO.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.service;

import br.codrive.dao.MovimentacaoDAO;
import br.codrive.dao.ProdutoDAO;
import br.codrive.model.Movimentacao;
import br.codrive.model.Produto;

import java.util.List;

public class MovimentacaoService {

    private final MovimentacaoDAO movimentacaoDao = new MovimentacaoDAO();
    private final ProdutoDAO      produtoDao      = new ProdutoDAO();

    public void registrar(Movimentacao m) {
        validarCampos(m);

        if ("SAIDA".equals(m.getTipo())) {
            Produto produto = produtoDao.buscarPorId(m.getIdProduto());
            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado.");
            }
            if (produto.getQuantidade() < m.getQuantidade()) {
                throw new IllegalArgumentException(
                    "Estoque insuficiente. Disponível: " + produto.getQuantidade()
                    + " unidade(s). Solicitado: " + m.getQuantidade() + ".");
            }
        }

        movimentacaoDao.registrar(m);
    }

    public List<Movimentacao> listarRecentes(int limite) {
        return movimentacaoDao.listarRecentes(limite);
    }

    public List<Movimentacao> listarOrdenado(String criterio) {
        return movimentacaoDao.listarOrdenado(criterio);
    }

    private void validarCampos(Movimentacao m) {
        if (m.getIdProduto() <= 0) {
            throw new IllegalArgumentException("Selecione um PRODUTO.");
        }
        if (m.getTipo() == null || (!m.getTipo().equals("ENTRADA") && !m.getTipo().equals("SAIDA"))) {
            throw new IllegalArgumentException("Selecione o TIPO da movimentação (ENTRADA ou SAÍDA).");
        }
        if (m.getQuantidade() <= 0) {
            throw new IllegalArgumentException("O campo QUANTIDADE deve ser maior que zero.");
        }
        if (m.getData() == null) {
            throw new IllegalArgumentException("O campo DATA é obrigatório.");
        }
    }
}
