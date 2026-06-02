/**
 * Classe: UsuarioService
 * Objetivo: Contém as regras de negócio da entidade Usuário, como validação
 *           de campos obrigatórios, duplicidade de login e autenticação
 *           utilizada pela TelaLogin para controle de acesso ao sistema.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.service;

import br.codrive.dao.UsuarioDAO;
import br.codrive.model.Usuario;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public int inserir(Usuario u) {
        validar(u);
        if (dao.existePorLogin(u.getLogin().trim())) {
            throw new IllegalArgumentException(
                "Já existe um usuário com o login \"" + u.getLogin().trim() + "\".");
        }
        u.setLogin(u.getLogin().trim());
        u.setNome(u.getNome().trim());
        return dao.inserir(u);
    }

    public void atualizar(Usuario u) {
        validar(u);
        if (dao.existePorLoginExcluindoId(u.getLogin().trim(), u.getId())) {
            throw new IllegalArgumentException(
                "Já existe outro usuário com o login \"" + u.getLogin().trim() + "\".");
        }
        u.setLogin(u.getLogin().trim());
        u.setNome(u.getNome().trim());
        dao.atualizar(u);
    }

    public void excluir(int id) {
        dao.excluir(id);
    }

    public Usuario buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    public List<Usuario> buscarPorLoginOuNome(String termo) {
        if (termo == null || termo.isBlank()) return dao.listarTodos();
        return dao.buscarPorLoginOuNome(termo.trim());
    }

    /**
     * Retorna o Usuario autenticado ou null se login/senha inválidos.
     * A TelaLogin trata o retorno null como falha de autenticação.
     */
    public Usuario autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isEmpty()) {
            return null;
        }
        Usuario u = dao.buscarPorLogin(login.trim());
        if (u == null) return null;
        return u.getSenha().equals(senha) ? u : null;
    }

    private void validar(Usuario u) {
        if (u.getNome() == null || u.getNome().isBlank()) {
            throw new IllegalArgumentException("O campo NOME é obrigatório.");
        }
        if (u.getLogin() == null || u.getLogin().isBlank()) {
            throw new IllegalArgumentException("O campo LOGIN é obrigatório.");
        }
        if (u.getLogin().trim().length() > 50) {
            throw new IllegalArgumentException("O login deve ter no máximo 50 caracteres.");
        }
        if (u.getSenha() == null || u.getSenha().isEmpty()) {
            throw new IllegalArgumentException("O campo SENHA é obrigatório.");
        }
    }
}
