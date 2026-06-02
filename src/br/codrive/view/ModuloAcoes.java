/**
 * Interface: ModuloAcoes
 * Objetivo: Contrato implementado pelas JInternalFrames que suportam as ações
 *           da toolbar da TelaMenu (F2-F8). Permite que TelaMenu delegue as
 *           ações para o módulo ativo sem depender de tipos concretos.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

public interface ModuloAcoes {
    void acaoNovo();
    void acaoSalvar();
    void acaoEditar();
    void acaoExcluir();
    void acaoPesquisar();
}
