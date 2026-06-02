/**
 * Classe: ProdutoService
 * Objetivo: Contém as regras de negócio da entidade Produto, como validação
 *           de campos obrigatórios, valor positivo e duplicidade de nome,
 *           delegando as operações de persistência ao ProdutoDAO.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.service;

import br.codrive.dao.ProdutoDAO;
import br.codrive.model.Produto;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ProdutoService {

    private final ProdutoDAO dao = new ProdutoDAO();

    public int inserir(Produto p) {
        validar(p);
        if (dao.existePorNome(p.getNome().trim())) {
            throw new IllegalArgumentException(
                "Já existe um produto com o nome \"" + p.getNome().trim() + "\".");
        }
        p.setNome(p.getNome().trim());
        return dao.inserir(p);
    }

    public void atualizar(Produto p) {
        validar(p);
        if (dao.existePorNomeExcluindoId(p.getNome().trim(), p.getId())) {
            throw new IllegalArgumentException(
                "Já existe outro produto com o nome \"" + p.getNome().trim() + "\".");
        }
        p.setNome(p.getNome().trim());
        dao.atualizar(p);
    }

    public void excluir(int id) {
        try {
            dao.excluir(id);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalArgumentException(
                    "Não é possível excluir: existem movimentações vinculadas a este produto.", e);
            }
            throw e;
        }
    }

    public Produto buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Produto> listarTodos() {
        return dao.listarTodos();
    }

    public List<Produto> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return dao.listarTodos();
        return dao.buscarPorNome(nome.trim());
    }

    public List<Produto> buscarPorNomeCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.isBlank()) return dao.listarTodos();
        return dao.buscarPorNomeCategoria(nomeCategoria.trim());
    }

    private void validar(Produto p) {
        if (p.getNome() == null || p.getNome().isBlank()) {
            throw new IllegalArgumentException("O campo NOME é obrigatório.");
        }
        if (p.getNome().trim().length() > 150) {
            throw new IllegalArgumentException("O nome do produto deve ter no máximo 150 caracteres.");
        }
        if (p.getValor() <= 0) {
            throw new IllegalArgumentException("O campo VALOR deve ser maior que zero.");
        }
        if (p.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("Selecione uma CATEGORIA para o produto.");
        }
    }
}
