import cliente.Cliente;
import servidor.IJogoServidor;
import coup.view.TelaJogo;
import coup.view.PaletaCoup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AppFrame extends JFrame {

    private JTextField txtNome;
    private JButton btnConectar;

    public AppFrame() {
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Coup Gold Edition - Launcher");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout());
        getContentPane().setBackground(PaletaCoup.BG_DEEP);
    }

    private void inicializarComponentes() {
        // Painel Central
        JPanel painelCentral = new JPanel(new GridLayout(3, 1, 10, 10));
        painelCentral.setBackground(PaletaCoup.BG_DEEP);
        painelCentral.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel lblTitulo = new JLabel("COUP GOLD", SwingConstants.CENTER);
        lblTitulo.setForeground(PaletaCoup.GOLD_BRIGHT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));

        txtNome = new JTextField();
        txtNome.setBackground(PaletaCoup.BG_CARD);
        txtNome.setForeground(PaletaCoup.GOLD_BRIGHT);
        txtNome.setCaretColor(PaletaCoup.GOLD_BRIGHT);
        txtNome.setFont(new Font("Arial", Font.PLAIN, 18));
        txtNome.setHorizontalAlignment(JTextField.CENTER);
        txtNome.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PaletaCoup.BORDER_DIM),
                "Digite seu Nickname",
                0, 0, new Font("Arial", Font.PLAIN, 12), PaletaCoup.TEXT_MUTED));

        btnConectar = new JButton("CONECTAR AO SERVIDOR");
        btnConectar.setBackground(PaletaCoup.GOLD_DIM);
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFocusPainted(false);
        btnConectar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ação do botão Conectar
        btnConectar.addActionListener(e -> conectarAoServidor());

        painelCentral.add(lblTitulo);
        painelCentral.add(txtNome);
        painelCentral.add(btnConectar);

        add(painelCentral, BorderLayout.CENTER);
    }

    private void conectarAoServidor() {
        String nome = txtNome.getText().trim();
        
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, digite um nome!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            btnConectar.setText("Conectando...");
            btnConectar.setEnabled(false);

            // Inicia a TelaJogo e o Cliente
            TelaJogo telaJogo = new TelaJogo();
            Cliente cliente = new Cliente(telaJogo, nome);
            
            // Conecta ao RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IJogoServidor servidor = (IJogoServidor) registry.lookup("CoupServidor");
            servidor.entrarJogo(cliente, nome);

            telaJogo.adicionarLog("Você conectou como " + nome + ".");
            telaJogo.adicionarLog("Aguardando outros jogadores...");
            
            // Força a sincronização inicial
            cliente.sincronizarEstado();
            
            // Sucesso: Configura a tela e fecha o Menu
            telaJogo.adicionarLog("Você conectou como " + nome + ".");
            telaJogo.adicionarLog("Aguardando outros jogadores...");
            telaJogo.setVisible(true);
            
            this.dispose(); // Fecha o AppFrame (Menu)

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar no servidor: \n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            btnConectar.setText("CONECTAR AO SERVIDOR");
            btnConectar.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppFrame().setVisible(true);
        });
    }
}