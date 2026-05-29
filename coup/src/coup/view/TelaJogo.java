package coup.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import coup.model.MascoteTradutor;
import coup.model.PersonagensNomes;

public class TelaJogo extends JFrame {

    private JPanel painelMesa;
    private JPanel painelJogador;
    private JPanel painelSuasCartas;
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
        // 1. Painel da Mesa (Onde ficam os adversários)
        painelMesa = new JPanel(new BorderLayout());
        painelMesa.setBackground(PaletaCoup.BG_DEEP);
        
        JLabel lblMesa = new JLabel("MESA CENTRAL - OPONENTES", SwingConstants.CENTER);
        lblMesa.setForeground(PaletaCoup.GOLD_MID);
        lblMesa.setFont(new Font("Arial", Font.BOLD, 24));
        lblMesa.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelMesa.add(lblMesa, BorderLayout.NORTH);

        // 2. Painel do Jogador (Cartas e Ações na parte inferior)
        painelJogador = new JPanel(new BorderLayout());
        painelJogador.setBackground(PaletaCoup.BG_CARD);
        painelJogador.setBorder(new LineBorder(PaletaCoup.BORDER_DIM, 2));
        painelJogador.setPreferredSize(new Dimension(0, 200));

        painelSuasCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        painelSuasCartas.setBackground(PaletaCoup.BG_CARD);
        painelSuasCartas.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(PaletaCoup.GOLD_DIM, 1, true), "SUAS CARTAS",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 12), PaletaCoup.GOLD_BRIGHT));
        
        painelJogador.add(painelSuasCartas, BorderLayout.CENTER);

        // 3. Painel Lateral (Logs do Servidor e Histórico)
        painelSidebar = new JPanel(new BorderLayout());
        painelSidebar.setBackground(PaletaCoup.BG_SIDEBAR);
        painelSidebar.setBorder(new LineBorder(PaletaCoup.BORDER_DIM, 1));
        painelSidebar.setPreferredSize(new Dimension(300, 0));

        JPanel containerLog = new JPanel(new BorderLayout());
        containerLog.setBackground(PaletaCoup.BG_SIDEBAR);
        JLabel lblLog = new JLabel("HISTÓRICO DO JOGO", SwingConstants.CENTER);
        lblLog.setForeground(PaletaCoup.TEXT_MUTED);
        lblLog.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        containerLog.add(lblLog, BorderLayout.NORTH);

        areaDeLog = new JTextArea();
        areaDeLog.setBackground(PaletaCoup.BG_SIDEBAR);
        areaDeLog.setForeground(PaletaCoup.TEXT_LOG);
        areaDeLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaDeLog.setEditable(false);
        areaDeLog.setLineWrap(true);
        areaDeLog.setWrapStyleWord(true);

        JScrollPane scrollLog = new JScrollPane(areaDeLog);
        scrollLog.setBorder(null);
        scrollLog.getViewport().setBackground(PaletaCoup.BG_SIDEBAR);
        containerLog.add(scrollLog, BorderLayout.CENTER);

        // 3.2 Cola de Personagens (Lore TechSphere)
        JPanel painelCola = new JPanel(new GridLayout(6, 1));
        painelCola.setBackground(PaletaCoup.BG_SIDEBAR);
        painelCola.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(PaletaCoup.BORDER_DIM, 1, true), "PERSONAGENS",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 10), PaletaCoup.GOLD_DIM));
        
        adicionarDicaPersonagem(painelCola, MascoteTradutor.getNomeMascote(PersonagensNomes.DUQUE), MascoteTradutor.getDescricao(PersonagensNomes.DUQUE));
        adicionarDicaPersonagem(painelCola, MascoteTradutor.getNomeMascote(PersonagensNomes.ASSASSINO), MascoteTradutor.getDescricao(PersonagensNomes.ASSASSINO));
        adicionarDicaPersonagem(painelCola, MascoteTradutor.getNomeMascote(PersonagensNomes.CAPITAO), MascoteTradutor.getDescricao(PersonagensNomes.CAPITAO));
        adicionarDicaPersonagem(painelCola, MascoteTradutor.getNomeMascote(PersonagensNomes.EMBAIXADOR), MascoteTradutor.getDescricao(PersonagensNomes.EMBAIXADOR));
        adicionarDicaPersonagem(painelCola, MascoteTradutor.getNomeMascote(PersonagensNomes.CONDESSA), MascoteTradutor.getDescricao(PersonagensNomes.CONDESSA));
        adicionarDicaPersonagem(painelCola, MascoteTradutor.getNomeMascote(PersonagensNomes.INQUISIDOR), MascoteTradutor.getDescricao(PersonagensNomes.INQUISIDOR));

        painelSidebar.add(containerLog, BorderLayout.CENTER);
        painelSidebar.add(painelCola, BorderLayout.SOUTH);
    }

    private void montarLayout() {
        add(painelMesa, BorderLayout.CENTER);
        add(painelJogador, BorderLayout.SOUTH);
        add(painelSidebar, BorderLayout.EAST);
    }

    // MÉTODO: Permite que o Cliente escreva na barra lateral
    public void adicionarLog(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            areaDeLog.append(mensagem + "\n");
            areaDeLog.setCaretPosition(areaDeLog.getDocument().getLength());
        });
    }

    // =========================================================================
    // MÉTODOS VISUAIS PARA DESENHAR AS CARTAS
    // =========================================================================

    public void atualizarSuasCartas(List<String> cartasStr) {
        SwingUtilities.invokeLater(() -> {
            painelSuasCartas.removeAll();
            for (String c : cartasStr) {
                boolean morta = c.contains("(Morta)");
                String nomeCarta = c.replace(" (Ativa)", "").replace(" (Morta)", "").trim();
                painelSuasCartas.add(criarDesenhoCarta(nomeCarta, morta, true));
            }
            painelSuasCartas.revalidate();
            painelSuasCartas.repaint();
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
                String[] partes = s.split(":");
                if (partes.length < 2 || partes[0].trim().equalsIgnoreCase(meuNome)) continue;

                boolean eliminado = s.contains("(ELIMINADO)");
                String textoMoedas = s.substring(s.indexOf(":") + 1).split("\\|")[0].replace("(ELIMINADO)", "").trim();

                List<String> cartasMortas = new ArrayList<>();
                if (s.contains("Cartas na mesa:")) {
                    String[] mortasArr = s.substring(s.indexOf("Cartas na mesa:") + 15).split(",");
                    for (String m : mortasArr) cartasMortas.add(m.trim());
                }

                JPanel opPanel = new JPanel(new BorderLayout());
                opPanel.setOpaque(false);
                JLabel lbl = new JLabel(partes[0].trim() + " (" + textoMoedas + ")", SwingConstants.CENTER);
                lbl.setForeground(PaletaCoup.GOLD_BRIGHT);
                opPanel.add(lbl, BorderLayout.NORTH);

                JPanel cartasOpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                cartasOpPanel.setOpaque(false);

                // Desenha cartas escondidas ou reveladas
                int vivas = eliminado ? 0 : (2 - cartasMortas.size());
                for (int i = 0; i < vivas; i++) cartasOpPanel.add(criarDesenhoCarta("COUP", false, false));
                for (String m : cartasMortas) cartasOpPanel.add(criarDesenhoCarta(m.toUpperCase(), true, false));

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
        
        String nomeExibicao = texto;
        try {
            PersonagensNomes p = PersonagensNomes.valueOf(texto.toUpperCase());
            nomeExibicao = MascoteTradutor.getNomeMascote(p);
        } catch (Exception e) {}

        JLabel lblTexto = new JLabel("<html><center>" + nomeExibicao + "</center></html>", SwingConstants.CENTER);
        lblTexto.setFont(new Font("Arial", Font.BOLD, 12));

        if (isMinha) {
            card.setBackground(morta ? Color.DARK_GRAY : PaletaCoup.ACTIVE_CARD);
            card.setBorder(new LineBorder(morta ? Color.GRAY : PaletaCoup.GOLD_BRIGHT, 2));
            lblTexto.setForeground(morta ? Color.GRAY : PaletaCoup.GOLD_BRIGHT);
        } else {
            card.setBackground(morta ? PaletaCoup.BG_CARD : PaletaCoup.BG_SIDEBAR);
            card.setBorder(new LineBorder(morta ? PaletaCoup.RED_SOFT : PaletaCoup.GOLD_DIM, 2));
            lblTexto.setForeground(morta ? Color.LIGHT_GRAY : PaletaCoup.GOLD_DIM);
        }
        card.add(lblTexto, BorderLayout.CENTER);
        return card;
    }

    private void adicionarDicaPersonagem(JPanel painel, String nome, String desc) {
        JLabel lbl = new JLabel("<html><b><font color='#F0C060'>" + nome + "</font></b> <font color='#CBA08A'>— " + desc + "</font></html>");
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
        painel.add(lbl);
    }
}