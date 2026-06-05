/**
 * Classe: LogAcesso
 * Objetivo: Representa um registro de log de acesso ao sistema (LOGIN/LOGOUT).
 *           Mapeia a tabela log_acesso do banco de dados.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.model;

import java.time.LocalDateTime;

public class LogAcesso {

    private int           id;
    private Usuario       usuario;
    private LocalDateTime dataHora;
    private String        acao;

    public LogAcesso() {}

    public LogAcesso(Usuario usuario, LocalDateTime dataHora, String acao) {
        this.usuario  = usuario;
        this.dataHora = dataHora;
        this.acao     = acao;
    }

    public LogAcesso(int id, Usuario usuario, LocalDateTime dataHora, String acao) {
        this.id       = id;
        this.usuario  = usuario;
        this.dataHora = dataHora;
        this.acao     = acao;
    }

    public int           getId()                      { return id; }
    public void          setId(int id)                { this.id = id; }

    public Usuario       getUsuario()                 { return usuario; }
    public void          setUsuario(Usuario usuario)  { this.usuario = usuario; }

    public LocalDateTime getDataHora()                { return dataHora; }
    public void          setDataHora(LocalDateTime d) { this.dataHora = d; }

    public String        getAcao()                    { return acao; }
    public void          setAcao(String acao)         { this.acao = acao; }
}
