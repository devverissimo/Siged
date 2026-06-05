/**
 * Classe: LogAcessoService
 * Objetivo: Regras de negócio para log de acesso. O método registrar() é
 *           chamado na TelaLogin (LOGIN) e na TelaMenu (LOGOUT).
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.service;

import br.codrive.dao.LogAcessoDAO;
import br.codrive.model.LogAcesso;
import br.codrive.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public class LogAcessoService {

    private final LogAcessoDAO dao = new LogAcessoDAO();

    public void registrar(Usuario usuario, String acao) {
        dao.registrar(new LogAcesso(usuario, LocalDateTime.now(), acao));
    }

    public List<LogAcesso> listarPorUsuario(int idUsuario) {
        return dao.listarPorUsuario(idUsuario);
    }

    public List<LogAcesso> listarTodos() {
        return dao.listarTodos();
    }
}
