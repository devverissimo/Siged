/**
 * Classe: Usuario
 * Objetivo: Representa a entidade Usuário do sistema, mapeando a tabela
 *           'usuario' do banco de dados. Utilizado para autenticação na
 *           TelaLogin e para o CRUD de usuários na TelaUsuario.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.model;

public class Usuario {

    private int    id;
    private String nome;
    private String login;
    private String senha;

    public Usuario() {}

    public Usuario(int id, String nome, String login, String senha) {
        this.id    = id;
        this.nome  = nome;
        this.login = login;
        this.senha = senha;
    }

    public int    getId()               { return id; }
    public void   setId(int id)         { this.id = id; }

    public String getNome()             { return nome; }
    public void   setNome(String nome)  { this.nome = nome; }

    public String getLogin()              { return login; }
    public void   setLogin(String login)  { this.login = login; }

    public String getSenha()              { return senha; }
    public void   setSenha(String senha)  { this.senha = senha; }

    @Override
    public String toString() {
        return nome + " (" + login + ")";
    }
}
