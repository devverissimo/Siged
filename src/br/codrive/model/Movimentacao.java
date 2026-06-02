/**
 * Classe: Movimentacao
 * Objetivo: Representa a entidade Movimentação do sistema, mapeando a tabela
 *           'movimentacao' do banco de dados. Registra entradas e saídas de
 *           produtos e aciona a atualização do estoque via Service.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.model;

import java.time.LocalDate;

public class Movimentacao {

    private int       id;
    private String    tipo;       // "ENTRADA" ou "SAIDA"
    private int       quantidade;
    private LocalDate data;
    private String    observacao;
    private int       idProduto;

    // Preenchido por JOIN no DAO — não persiste no banco
    private String nomeProduto;

    public Movimentacao() {}

    public Movimentacao(int id, String tipo, int quantidade,
                        LocalDate data, String observacao, int idProduto) {
        this.id         = id;
        this.tipo       = tipo;
        this.quantidade = quantidade;
        this.data       = data;
        this.observacao = observacao;
        this.idProduto  = idProduto;
    }

    public int    getId()                       { return id; }
    public void   setId(int id)                 { this.id = id; }

    public String getTipo()                     { return tipo; }
    public void   setTipo(String tipo)          { this.tipo = tipo; }

    public int    getQuantidade()               { return quantidade; }
    public void   setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public LocalDate getData()                  { return data; }
    public void      setData(LocalDate data)    { this.data = data; }

    public String getObservacao()                     { return observacao; }
    public void   setObservacao(String observacao)    { this.observacao = observacao; }

    public int  getIdProduto()                  { return idProduto; }
    public void setIdProduto(int idProduto)     { this.idProduto = idProduto; }

    public String getNomeProduto()                      { return nomeProduto; }
    public void   setNomeProduto(String nomeProduto)    { this.nomeProduto = nomeProduto; }
}
