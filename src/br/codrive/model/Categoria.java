/**
 * Classe: Categoria
 * Objetivo: Representa a entidade Categoria do sistema, mapeando a tabela
 *           'categoria' do banco de dados. Serve como objeto de transporte
 *           entre as camadas DAO, Service e View.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.model;

public class Categoria {

    private int    id;
    private String nome;

    public Categoria() {}

    public Categoria(int id, String nome) {
        this.id   = id;
        this.nome = nome;
    }

    public int getId()             { return id; }
    public void setId(int id)      { this.id = id; }

    public String getNome()              { return nome; }
    public void   setNome(String nome)   { this.nome = nome; }

    @Override
    public String toString() {
        return nome;
    }
}
