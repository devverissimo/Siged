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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TelaListagem extends JInternalFrame implements ModuloAcoes {

    private JComboBox<String>      comboOrdenacao;
    private JLabel                 lblTotal;
    private JTable                 tabela;
    private DefaultTableModel      modeloTabela;
    private JTextField             campoDe;
    private JTextField             campoAte;
    private List<Movimentacao>     listaAtual = new ArrayList<>();

    private final String               nomeUsuario;
    private final MovimentacaoService  service = new MovimentacaoService();

    private static final String[] CRITERIOS = {
        MovimentacaoDAO.ORDEM_DATA_DESC,
        MovimentacaoDAO.ORDEM_DATA_ASC,
        MovimentacaoDAO.ORDEM_PRODUTO_AZ,
        MovimentacaoDAO.ORDEM_TIPO
    };

    public TelaListagem(String nomeUsuario) {
        super(Mensagem.get("titulo.listagem"), true, true, true, true);
        this.nomeUsuario = nomeUsuario != null ? nomeUsuario : "Sistema";
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

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setBackground(AppTheme.COR_FUNDO);
        norte.add(construirPainelControle());
        norte.add(construirPainelFiltro());

        raiz.add(norte,                   BorderLayout.NORTH);
        raiz.add(construirPainelTabela(), BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private JPanel construirPainelControle() {
        JPanel painel = new JPanel(new BorderLayout(8, 0));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

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
        comboOrdenacao.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) carregarListagem();
        });

        esquerda.add(lblOrdenar);
        esquerda.add(comboOrdenacao);

        lblTotal = new JLabel(" ");
        lblTotal.setFont(AppTheme.FONTE_LABEL);
        lblTotal.setForeground(AppTheme.COR_TEXTO);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        direita.setBackground(AppTheme.COR_FUNDO);

        JButton btnAtualizar = new JButton("ATUALIZAR");
        AppTheme.estilizarBotaoSecundario(btnAtualizar);
        btnAtualizar.addActionListener(e -> carregarListagem());

        JButton btnExportar = new JButton("EXPORTAR .TXT");
        AppTheme.estilizarBotaoSecundario(btnExportar);
        btnExportar.addActionListener(e -> exportarRelatorio());

        direita.add(lblTotal);
        direita.add(btnAtualizar);
        direita.add(btnExportar);

        painel.add(esquerda, BorderLayout.WEST);
        painel.add(direita,  BorderLayout.EAST);
        return painel;
    }

    private JPanel construirPainelFiltro() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        painel.setBackground(AppTheme.COR_FUNDO);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.COR_BORDA),
            BorderFactory.createEmptyBorder(4, 0, 0, 0)
        ));

        JLabel lblDe = new JLabel("DE:");
        lblDe.setFont(AppTheme.FONTE_BOLD);

        campoDe = new JTextField(10);
        campoDe.setFont(AppTheme.FONTE_DADOS);
        campoDe.setToolTipText("DD/MM/AAAA");

        JLabel lblAte = new JLabel("ATÉ:");
        lblAte.setFont(AppTheme.FONTE_BOLD);

        campoAte = new JTextField(10);
        campoAte.setFont(AppTheme.FONTE_DADOS);
        campoAte.setToolTipText("DD/MM/AAAA");

        JButton btnFiltrar = new JButton("FILTRAR");
        AppTheme.estilizarBotaoPrimario(btnFiltrar);
        btnFiltrar.addActionListener(e -> filtrarPorPeriodo());

        JButton btnLimpar = new JButton("LIMPAR FILTRO");
        AppTheme.estilizarBotaoSecundario(btnLimpar);
        btnLimpar.addActionListener(e -> limparFiltro());

        painel.add(lblDe);
        painel.add(campoDe);
        painel.add(lblAte);
        painel.add(campoAte);
        painel.add(btnFiltrar);
        painel.add(btnLimpar);

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
            listaAtual = lista;
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

    private void filtrarPorPeriodo() {
        LocalDate dataDe;
        LocalDate dataAte;
        try {
            dataDe = Formatador.parseData(campoDe.getText());
        } catch (DateTimeParseException e) {
            Mensagem.erro(this, "Data DE inválida. Use o formato DD/MM/AAAA.");
            campoDe.requestFocus();
            return;
        }
        try {
            dataAte = Formatador.parseData(campoAte.getText());
        } catch (DateTimeParseException e) {
            Mensagem.erro(this, "Data ATÉ inválida. Use o formato DD/MM/AAAA.");
            campoAte.requestFocus();
            return;
        }
        if (dataDe.isAfter(dataAte)) {
            Mensagem.erro(this, "A data DE não pode ser posterior à data ATÉ.");
            return;
        }

        modeloTabela.setRowCount(0);
        try {
            List<Movimentacao> lista = service.listarPorPeriodo(
                java.sql.Date.valueOf(dataDe),
                java.sql.Date.valueOf(dataAte)
            );
            listaAtual = lista;
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
            Mensagem.erro(this, "Erro ao filtrar listagem:\n" + ex.getMessage());
        }
    }

    private void exportarRelatorio() {
        if (listaAtual.isEmpty()) {
            Mensagem.erro(this, "Não há registros para exportar.");
            return;
        }

        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String nomeArquivo = "relatorio_movimentacoes_" + dataHoje + ".txt";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatório");
        chooser.setSelectedFile(new File(nomeArquivo));
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo de texto (*.txt)", "txt"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File arquivo = chooser.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".txt")) {
            arquivo = new File(arquivo.getAbsolutePath() + ".txt");
        }

        String dataHoraGeracao = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String sep1 = "=".repeat(70);
        String sep2 = "-".repeat(70);
        String fmt  = "%-12s%-28s%-10s%-6s%s%n";

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {

            pw.println(sep1);
            pw.println("SIGED - CoDrive | Relatório de Movimentações");
            pw.println("Gerado em: " + dataHoraGeracao);
            pw.println("Usuário: " + nomeUsuario);
            pw.println(sep1);
            pw.printf(fmt, "DATA", "PRODUTO", "TIPO", "QTD", "RESPONSÁVEL");
            pw.println(sep2);

            for (Movimentacao m : listaAtual) {
                String tipo    = "SAIDA".equals(m.getTipo()) ? "SAÍDA" : "ENTRADA";
                String produto = m.getNomeProduto() != null ? m.getNomeProduto() : "-";
                pw.printf(fmt,
                    Formatador.formatarData(m.getData()),
                    truncar(produto, 27),
                    tipo,
                    String.valueOf(m.getQuantidade()),
                    nomeUsuario);
            }

            pw.println(sep2);
            pw.println("Total de registros: " + listaAtual.size());
            pw.println(sep1);

        } catch (IOException ex) {
            Mensagem.erro(this, "Erro ao salvar o arquivo:\n" + ex.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Relatório exportado com sucesso!\n" + arquivo.getAbsolutePath(),
            "Exportação concluída",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String truncar(String texto, int limite) {
        if (texto == null) return "";
        return texto.length() <= limite ? texto : texto.substring(0, limite - 2) + "..";
    }

    private void limparFiltro() {
        campoDe.setText("");
        campoAte.setText("");
        carregarListagem();
    }

    // -------------------------------------------------------------------------
    // Implementação de ModuloAcoes — delegação da toolbar de TelaMenu
    // -------------------------------------------------------------------------

    @Override public void acaoNovo()      { /* não aplicável neste módulo */ }
    @Override public void acaoSalvar()    { /* não aplicável neste módulo */ }
    @Override public void acaoEditar()    { /* não aplicável neste módulo */ }
    @Override public void acaoExcluir()   { /* não aplicável neste módulo */ }
    @Override public void acaoPesquisar() { carregarListagem(); }
}
