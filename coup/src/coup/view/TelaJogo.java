package coup.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TelaJogo extends JFrame {

    private JTextArea areaLog = new JTextArea();

    private JButton btnDuque = new JButton("Imposto (Duque)");
    private JButton btnCapitao = new JButton("Roubar (Capitão)");
    private JButton btnEmbaixador = new JButton("Trocar");
    private JButton btnAjuda = new JButton("Ajuda externa");
    private JButton btnRenda = new JButton("Renda");

    private Consumer<Integer> aoEscolherAcao;

    public TelaJogo() {
        setTitle("Coup");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel painelAcoes = new JPanel();
        painelAcoes.setLayout(new GridLayout(5, 1));

        painelAcoes.add(btnDuque);
        painelAcoes.add(btnCapitao);
        painelAcoes.add(btnEmbaixador);
        painelAcoes.add(btnAjuda);
        painelAcoes.add(btnRenda);

        add(painelAcoes, BorderLayout.WEST);

        areaLog.setEditable(false);
        add(areaLog, BorderLayout.CENTER);

        btnDuque.addActionListener(e -> escolherAcao(1));
        btnCapitao.addActionListener(e -> escolherAcao(2));
        btnEmbaixador.addActionListener(e -> escolherAcao(3));
        btnAjuda.addActionListener(e -> escolherAcao(4));
        btnRenda.addActionListener(e -> escolherAcao(5));

        setVisible(true);
    }

    private void escolherAcao(int opcao) {
        if (aoEscolherAcao != null) {
            aoEscolherAcao.accept(opcao);
        }
    }

    public void setAoEscolherAcao(Consumer<Integer> aoEscolherAcao) {
        this.aoEscolherAcao = aoEscolherAcao;
    }

    public void adicionarLog(String texto) {
        areaLog.append(texto + "\n");
    }
}