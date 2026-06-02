/**
 * Classe: TelaListagem
 * Objetivo: JInternalFrame de listagem de todas as movimentações registradas.
 *           Permite ordenar os dados por data (↑/↓), produto (A-Z) ou tipo
 *           via JComboBox; a tabela é recarregada automaticamente ao mudar.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.dao.MovimentacaoDAO;
import br.codrive.model.Movimentacao;
import br.codrive.service.MovimentacaoService;
import br.codrive.util.AppTheme;
import br.codrive.util.Formatador;
import br.codrive.util.Mensagem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class TelaListagem extends JInternalFrame {

    private JComboBox<String>  comboOrdenacao;
    private JLabel             lblTotal;
    private JTable             tabela;
    private DefaultTableModel  modeloTabela;

    private final MovimentacaoService service = new MovimentacaoService();

    // Índice do combo → critério de ordenação do DAO (ordem importa)
    private static final String[] CRITERIOS = {
        MovimentacaoDAO.ORDEM_DATA_DESC,   // 0 — padrão: mais recentes primeiro
        MovimentacaoDAO.ORDEM_DATA_ASC,    // 1
        MovimentacaoDAO.ORDEM_PRODUTO_AZ,  // 2
        MovimentacaoDAO.ORDEM_TIPO         // 3
    };

    public TelaListagem() {
        super(Mensagem.get("titulo.listagem"), true, true, true, true);
        setSize(820, 560);
        setMinimumSize(new Dimension(660, 420));
        construirInterface();
        carregarListagem();
    }

    // -------------------------------------------------------------------------
    // Construção da interface
    // -------------------------------------------------------------------------

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBackground(AppTheme.COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        raiz.add(construirPainelControle(), BorderLayout.NORTH);
        raiz.add(construirPainelTabela(),   BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private JPanel construirPainelControle() {
        JPanel painel = new JPanel(new BorderLayout(8, 0));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        // Esquerda: label + combo
        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        esquerda.setBackground(AppTheme.COR_FUNDO);

        JLabel lblOrdenar = new JLabel(Mensagem.get("ordenacao.label"));
        lblOrdenar.setFont(AppTheme.FONTE_BOLD);

        comboOrdenacao = new JComboBox<>(new String[]{
            Mensagem.get("ordenacao.data.desc"),
            Mensagem.get("ordenacao.data.asc"),
            Mensagem.get("ordenacao.produto.az"),
            Mensagem.get("ordenacao.tipo")
        });
        comboOrdenacao.setFont(AppTheme.FONTE_DADOS);
        comboOrdenacao.setPreferredSize(
            new Dimension(210, comboOrdenacao.getPreferredSize().height));

        // Recarrega automaticamente ao trocar ordenação
        comboOrdenacao.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) carregarListagem();
        });

        esquerda.add(lblOrdenar);
        esquerda.add(comboOrdenacao);

        // Direita: contador de registros
        lblTotal = new JLabel(" ");
        lblTotal.setFont(AppTheme.FONTE_LABEL);
        lblTotal.setForeground(AppTheme.COR_TEXTO);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(lblTotal,  BorderLayout.EAST);
        return painel;
    }

    private JPanel construirPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(AppTheme.COR_FUNDO);

        modeloTabela = new DefaultTableModel(new String[]{
            Mensagem.get("tabela.data"),
            Mensagem.get("tabela.produto"),
            Mensagem.get("tabela.tipo"),
            Mensagem.get("tabela.quantidade"),
            Mensagem.get("tabela.observacao")
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 3 ? Integer.class : String.class;
            }
        };

        tabela = new JTable(modeloTabela);
        AppTheme.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Larguras fixas para colunas de conteúdo previsível
        tabela.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(0).setMaxWidth(110);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(75);
        tabela.getColumnModel().getColumn(2).setMaxWidth(90);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(3).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA));

        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------------------------
    // Carga de dados
    // -------------------------------------------------------------------------

    private void carregarListagem() {
        modeloTabela.setRowCount(0);
        try {
            String criterio = CRITERIOS[comboOrdenacao.getSelectedIndex()];
            List<Movimentacao> lista = service.listarOrdenado(criterio);

            for (Movimentacao m : lista) {
                String tipo = "SAIDA".equals(m.getTipo())
                    ? Mensagem.get("tipo.saida")
                    : Mensagem.get("tipo.entrada");
                modeloTabela.addRow(new Object[]{
                    Formatador.formatarData(m.getData()),
                    m.getNomeProduto() != null ? m.getNomeProduto() : "-",
                    tipo,
                    m.getQuantidade(),
                    m.getObservacao() != null ? m.getObservacao() : ""
                });
            }

            lblTotal.setText(lista.size() + " registro(s) encontrado(s)  ");

        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar listagem:\n" + ex.getMessage());
        }
    }
}
