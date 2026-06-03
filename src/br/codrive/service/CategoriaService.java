/**
 * Classe: CategoriaService
 * Objetivo: Contém as regras de negócio da entidade Categoria, como validação
 *           de campos obrigatórios e verificação de duplicidade antes de
 *           delegar as operações de persistência ao CategoriaDAO.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.service;

import br.codrive.dao.CategoriaDAO;
import br.codrive.model.Categoria;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class CategoriaService {

    private final CategoriaDAO dao = new CategoriaDAO();

    public int inserir(Categoria c) {
        validarNome(c.getNome());
        if (c.getId() > 0 && dao.buscarPorId(c.getId()) != null) {
            throw new IllegalArgumentException(
                "Código " + c.getId() + " já está em uso. Informe outro código ou deixe em branco.");
        }
        if (dao.existePorNome(c.getNome().trim())) {
            throw new IllegalArgumentException(
                "Já existe uma categoria com o nome \"" + c.getNome().trim() + "\".");
        }
        c.setNome(c.getNome().trim());
        return dao.inserir(c);
    }

    public void atualizar(Categoria c) {
        validarNome(c.getNome());
        if (dao.existePorNomeExcluindoId(c.getNome().trim(), c.getId())) {
            throw new IllegalArgumentException(
                "Já existe outra categoria com o nome \"" + c.getNome().trim() + "\".");
        }
        c.setNome(c.getNome().trim());
        dao.atualizar(c);
    }

    public void excluir(int id) {
        try {
            dao.excluir(id);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalArgumentException(
                    "Não é possível excluir: existem produtos vinculados a esta categoria.", e);
            }
            throw e;
        }
    }

    public Categoria buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Categoria> listarTodos() {
        return dao.listarTodos();
    }

    public List<Categoria> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return dao.listarTodos();
        return dao.buscarPorNome(nome.trim());
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O campo NOME é obrigatório.");
        }
        if (nome.trim().length() > 100) {
            throw new IllegalArgumentException("O nome da categoria deve ter no máximo 100 caracteres.");
        }
    }
}
