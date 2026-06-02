/**
 * Classe: Produto
 * Objetivo: Representa a entidade Produto do sistema, mapeando a tabela
 *           'produto' do banco de dados. O campo quantidade é somente leitura
 *           pela interface — alterado exclusivamente via Movimentação.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.model;

public class Produto {

    private int    id;
    private String nome;
    private double valor;
    private int    quantidade;
    private int    idCategoria;

    // Preenchido por JOIN no DAO — não persiste no banco
    private String nomeCategoria;

    public Produto() {}

    public Produto(int id, String nome, double valor, int quantidade, int idCategoria) {
        this.id          = id;
        this.nome        = nome;
        this.valor       = valor;
        this.quantidade  = quantidade;
        this.idCategoria = idCategoria;
    }

    public int    getId()                       { return id; }
    public void   setId(int id)                 { this.id = id; }

    public String getNome()                     { return nome; }
    public void   setNome(String nome)          { this.nome = nome; }

    public double getValor()                    { return valor; }
    public void   setValor(double valor)        { this.valor = valor; }

    public int    getQuantidade()               { return quantidade; }
    public void   setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public int    getIdCategoria()              { return idCategoria; }
    public void   setIdCategoria(int id)        { this.idCategoria = id; }

    public String getNomeCategoria()                    { return nomeCategoria; }
    public void   setNomeCategoria(String nomeCategoria){ this.nomeCategoria = nomeCategoria; }

    @Override
    public String toString() {
        return nome + " (Estoque: " + quantidade + ")";
    }
}
