package coup.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaJogo extends JFrame {

    private JPanel painelMesa;
    private JPanel painelJogador;
    private JPanel painelSidebar;
    private JTextArea areaDeLog;

    public TelaJogo() {
        configurarJanela();
        inicializarComponentes();
        montarLayout();
    }

    private void configurarJanela() {
        setTitle("Coup - Gold Edition");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PaletaCoup.BG_DEEP);
    }

    private void inicializarComponentes() {
        // 1. Painel da Mesa (Oponentes)
        painelMesa = new JPanel(new BorderLayout());
        painelMesa.setBackground(PaletaCoup.BG_DEEP);
        
        JLabel lblMesa = new JLabel("MESA CENTRAL - OPONENTES", SwingConstants.CENTER);
        lblMesa.setForeground(PaletaCoup.GOLD_MID);
        lblMesa.setFont(new Font("Arial", Font.BOLD, 24));
        lblMesa.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelMesa.add(lblMesa, BorderLayout.NORTH);

        // 2. Painel do Jogador (Parte inferior)
        painelJogador = new JPanel(new BorderLayout());
        painelJogador.setBackground(PaletaCoup.BG_CARD);
        painelJogador.setBorder(new LineBorder(PaletaCoup.BORDER_DIM, 2));
        painelJogador.setPreferredSize(new Dimension(0, 200));

        JLabel lblJogador = new JLabel("SUAS CARTAS E AÇÕES", SwingConstants.CENTER);
        lblJogador.setForeground(PaletaCoup.GOLD_BRIGHT);
        lblJogador.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        painelJogador.add(lblJogador, BorderLayout.NORTH);

        // 3. Painel Lateral (Logs do Servidor)
        painelSidebar = new JPanel(new BorderLayout());
        painelSidebar.setBackground(PaletaCoup.BG_SIDEBAR);
        painelSidebar.setBorder(new LineBorder(PaletaCoup.BORDER_DIM, 1));
        painelSidebar.setPreferredSize(new Dimension(300, 0));

        JLabel lblLog = new JLabel("HISTÓRICO DO JOGO", SwingConstants.CENTER);
        lblLog.setForeground(PaletaCoup.TEXT_MUTED);
        lblLog.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        painelSidebar.add(lblLog, BorderLayout.NORTH);

        areaDeLog = new JTextArea();
        areaDeLog.setBackground(PaletaCoup.BG_SIDEBAR);
        areaDeLog.setForeground(PaletaCoup.TEXT_LOG);
        areaDeLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaDeLog.setEditable(false);
        areaDeLog.setLineWrap(true);
        areaDeLog.setWrapStyleWord(true);

        JScrollPane scrollLog = new JScrollPane(areaDeLog);
        scrollLog.setBorder(null);
        painelSidebar.add(scrollLog, BorderLayout.CENTER);
    }

    private void montarLayout() {
        add(painelMesa, BorderLayout.CENTER);
        add(painelJogador, BorderLayout.SOUTH);
        add(painelSidebar, BorderLayout.EAST);
    }

    public void adicionarLog(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            areaDeLog.append(mensagem + "\n");
            areaDeLog.setCaretPosition(areaDeLog.getDocument().getLength());
        });
    }

    // =========================================================================
    // NOVOS MÉTODOS VISUAIS PARA DESENHAR AS CARTAS
    // =========================================================================

    public void atualizarSuasCartas(List<String> cartasStr) {
        SwingUtilities.invokeLater(() -> {
            // Remove as cartas antigas da área "Center" do painelJogador
            BorderLayout layout = (BorderLayout) painelJogador.getLayout();
            Component centerComponent = layout.getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent != null) painelJogador.remove(centerComponent);

            // Cria um painel novo para enfileirar as cartas
            JPanel painelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
            painelCartas.setBackground(PaletaCoup.BG_CARD);

            for (String c : cartasStr) {
                boolean morta = c.contains("(Morta)");
                String nomeCarta = c.replace(" (Ativa)", "").replace(" (Morta)", "").trim();
                painelCartas.add(criarDesenhoCarta(nomeCarta, morta, true));
            }

            painelJogador.add(painelCartas, BorderLayout.CENTER);
            painelJogador.revalidate();
            painelJogador.repaint();
        });
    }

    public void atualizarMesa(List<String> statusSaldos, String meuNome) {
        SwingUtilities.invokeLater(() -> {
            BorderLayout layout = (BorderLayout) painelMesa.getLayout();
            Component centerComponent = layout.getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent != null) painelMesa.remove(centerComponent);

            JPanel painelOponentes = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 40));
            painelOponentes.setBackground(PaletaCoup.BG_DEEP);

            for (String s : statusSaldos) {
                // Parse rudimentar da string do servidor para extrair dados
                String[] partes = s.split(":");
                if (partes.length < 2) continue;
                String nomeOp = partes[0].trim();
                
                if (nomeOp.equalsIgnoreCase(meuNome)) continue; // Não desenha você mesmo na mesa central

                boolean eliminado = s.contains("(ELIMINADO)");
                String textoMoedas = s.substring(s.indexOf(":") + 1).split("\\|")[0].replace("(ELIMINADO)", "").trim();

                List<String> cartasMortas = new ArrayList<>();
                if (s.contains("Cartas na mesa:")) {
                    String[] mortasArr = s.substring(s.indexOf("Cartas na mesa:") + 15).split(",");
                    for (String m : mortasArr) cartasMortas.add(m.trim());
                }

                int cartasOcultas = eliminado ? 0 : (2 - cartasMortas.size());
                if (cartasOcultas < 0) cartasOcultas = 0;

                // Monta o mini-painel do oponente
                JPanel opPanel = new JPanel(new BorderLayout());
                opPanel.setBackground(PaletaCoup.BG_DEEP);

                JLabel lblNomeMoedas = new JLabel(nomeOp + " - " + textoMoedas, SwingConstants.CENTER);
                lblNomeMoedas.setForeground(eliminado ? Color.GRAY : PaletaCoup.GOLD_BRIGHT);
                lblNomeMoedas.setFont(new Font("Arial", Font.BOLD, 18));
                lblNomeMoedas.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                opPanel.add(lblNomeMoedas, BorderLayout.NORTH);

                JPanel cartasOpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                cartasOpPanel.setBackground(PaletaCoup.BG_DEEP);

                // Adiciona cartas escondidas (vivas)
                for (int i = 0; i < cartasOcultas; i++) {
                    cartasOpPanel.add(criarDesenhoCarta("COUP", false, false));
                }
                // Adiciona cartas reveladas (mortas)
                for (String morta : cartasMortas) {
                    cartasOpPanel.add(criarDesenhoCarta(morta.toUpperCase(), true, false));
                }

                opPanel.add(cartasOpPanel, BorderLayout.CENTER);
                painelOponentes.add(opPanel);
            }

            painelMesa.add(painelOponentes, BorderLayout.CENTER);
            painelMesa.revalidate();
            painelMesa.repaint();
        });
    }

    // Fabricante de Cartas Visuais
    private JPanel criarDesenhoCarta(String texto, boolean morta, boolean isMinha) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(100, 140));
        
        JLabel lblTexto = new JLabel("<html><center>" + texto + "</center></html>", SwingConstants.CENTER);
        lblTexto.setFont(new Font("Arial", Font.BOLD, 14));

        if (isMinha) {
            if (morta) {
                card.setBackground(Color.DARK_GRAY);
                card.setBorder(new LineBorder(Color.GRAY, 2));
                lblTexto.setForeground(Color.GRAY);
            } else {
                card.setBackground(PaletaCoup.ACTIVE_CARD);
                card.setBorder(new LineBorder(PaletaCoup.GOLD_BRIGHT, 2));
                lblTexto.setForeground(PaletaCoup.GOLD_BRIGHT);
            }
        } else {
            if (morta) {
                // Carta de Oponente Revelada (Morta)
                card.setBackground(PaletaCoup.BG_CARD);
                card.setBorder(new LineBorder(PaletaCoup.RED_SOFT, 2)); // Borda vermelha indicando morte
                lblTexto.setForeground(Color.LIGHT_GRAY);
            } else {
                // Carta de Oponente Oculta
                card.setBackground(PaletaCoup.BG_SIDEBAR);
                card.setBorder(new LineBorder(PaletaCoup.GOLD_DIM, 2));
                lblTexto.setForeground(PaletaCoup.GOLD_DIM); // Apenas escrito "COUP"
            }
        }

        card.add(lblTexto, BorderLayout.CENTER);
        return card;
    }
}