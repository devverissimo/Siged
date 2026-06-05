/**
 * Classe: TelaLogAcesso
 * Objetivo: JInternalFrame que exibe o histórico de acessos ao sistema
 *           (LOGIN/LOGOUT). Permite filtrar por usuário via JComboBox.
 * Autor: Maria Rita Veríssimo
 * Disciplina: CMP1611 — Mini-Projeto de Software — PUC Goiás
 */
package br.codrive.view;

import br.codrive.model.LogAcesso;
import br.codrive.service.LogAcessoService;
import br.codrive.util.AppTheme;
import br.codrive.util.Mensagem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TelaLogAcesso extends JInternalFrame {

    private static final DateTimeFormatter FMT_DT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Color COR_LOGIN_BG  = new Color(0xD1, 0xFA, 0xE5);
    private static final Color COR_LOGIN_FG  = new Color(0x06, 0x5F, 0x46);
    private static final Color COR_LOGOUT_BG = new Color(0xFE, 0xE2, 0xE2);
    private static final Color COR_LOGOUT_FG = new Color(0x7F, 0x1D, 0x1D);

    private JComboBox<String>  comboUsuario;
    private JLabel             lblTotal;
    private JTable             tabela;
    private DefaultTableModel  modeloTabela;

    private List<LogAcesso>    listaCompleta = new ArrayList<>();

    private final LogAcessoService service = new LogAcessoService();

    public TelaLogAcesso() {
        super("Log de Acesso", true, true, true, true);
        setSize(900, 560);
        setMinimumSize(new Dimension(700, 420));
        construirInterface();
        carregarLogs();
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

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        esquerda.setBackground(AppTheme.COR_FUNDO);

        JLabel lblFiltrar = new JLabel("USUÁRIO:");
        lblFiltrar.setFont(AppTheme.FONTE_BOLD);

        comboUsuario = new JComboBox<>();
        comboUsuario.setFont(AppTheme.FONTE_DADOS);
        comboUsuario.setPreferredSize(new Dimension(200, comboUsuario.getPreferredSize().height));
        comboUsuario.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) aplicarFiltro();
        });

        esquerda.add(lblFiltrar);
        esquerda.add(comboUsuario);

        lblTotal = new JLabel(" ");
        lblTotal.setFont(AppTheme.FONTE_LABEL);
        lblTotal.setForeground(AppTheme.COR_TEXTO);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setBackground(AppTheme.COR_FUNDO);

        JButton btnAtualizar = new JButton("ATUALIZAR");
        AppTheme.estilizarBotaoSecundario(btnAtualizar);
        btnAtualizar.addActionListener(e -> carregarLogs());

        direita.add(lblTotal);
        direita.add(btnAtualizar);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(direita,  BorderLayout.EAST);
        return painel;
    }

    private JPanel construirPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(AppTheme.COR_FUNDO);

        modeloTabela = new DefaultTableModel(
                new String[]{"DATA/HORA", "USUÁRIO", "AÇÃO"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modeloTabela);
        AppTheme.estilizarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(0).setMaxWidth(170);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabela.getColumnModel().getColumn(2).setMaxWidth(100);

        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if ("LOGIN".equals(value)) {
                        setBackground(COR_LOGIN_BG);
                        setForeground(COR_LOGIN_FG);
                    } else {
                        setBackground(COR_LOGOUT_BG);
                        setForeground(COR_LOGOUT_FG);
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(AppTheme.FONTE_TABELA);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.COR_BORDA));

        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // -------------------------------------------------------------------------
    // Carga e filtro de dados
    // -------------------------------------------------------------------------

    private void carregarLogs() {
        try {
            listaCompleta = service.listarTodos();
            reconstruirCombo();
            aplicarFiltro();
        } catch (RuntimeException ex) {
            Mensagem.erro(this, "Erro ao carregar log de acesso:\n" + ex.getMessage());
        }
    }

    private void reconstruirCombo() {
        String selecaoAtual = (String) comboUsuario.getSelectedItem();

        // Remove listener temporariamente para não disparar aplicarFiltro() a cada addItem
        ItemListener[] listeners = comboUsuario.getItemListeners();
        for (ItemListener il : listeners) comboUsuario.removeItemListener(il);

        comboUsuario.removeAllItems();
        comboUsuario.addItem("Todos os usuários");
        listaCompleta.stream()
            .map(l -> l.getUsuario().getNome())
            .distinct()
            .sorted()
            .forEach(comboUsuario::addItem);

        // Restaura listener e seleção
        for (ItemListener il : listeners) comboUsuario.addItemListener(il);

        if (selecaoAtual != null) {
            for (int i = 0; i < comboUsuario.getItemCount(); i++) {
                if (selecaoAtual.equals(comboUsuario.getItemAt(i))) {
                    comboUsuario.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void aplicarFiltro() {
        String filtro = (String) comboUsuario.getSelectedItem();
        boolean todos = filtro == null || "Todos os usuários".equals(filtro);

        List<LogAcesso> filtrada = todos
            ? listaCompleta
            : listaCompleta.stream()
                .filter(l -> filtro.equals(l.getUsuario().getNome()))
                .collect(Collectors.toList());

        modeloTabela.setRowCount(0);
        for (LogAcesso l : filtrada) {
            modeloTabela.addRow(new Object[]{
                l.getDataHora().format(FMT_DT),
                l.getUsuario().getNome(),
                l.getAcao()
            });
        }
        lblTotal.setText(filtrada.size() + " registro(s)  ");
    }
}
