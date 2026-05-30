package coup.model;

import java.io.Serializable;

public class Jogador implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private int saldo = 2;
    private JogadorCartas jogadorCartas = new JogadorCartas();
    private boolean statusAtivo = true;

    public Jogador(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Jogador() {}

    public void perderCarta(Carta cartaParaMorrer) {
        if (cartaParaMorrer != null && cartaParaMorrer.isStatusAtiva()) {
            cartaParaMorrer.setStatusAtiva(false);
            atualizarStatus();
        } else {
            throw new IllegalArgumentException("Esta carta não é válida ou já está morta.");
        }
    }

    public void atualizarStatus() {
        boolean temCartaAtiva = false;
        for (Carta carta : jogadorCartas.getCartas()) {
            if (carta.isStatusAtiva()) {
                temCartaAtiva = true;
                break;
            }
        }
        this.statusAtivo = temCartaAtiva;
    }

    // getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getSaldo() { return saldo; }
    public void setSaldo(int saldo) { this.saldo = saldo; }
    public JogadorCartas getJogadorCartas() { return jogadorCartas; }
    public void setJogadorCartas(JogadorCartas jogadorCartas) { this.jogadorCartas = jogadorCartas; }
    public boolean isStatusAtivo() { return statusAtivo; }
    public void setStatusAtivo(boolean statusAtivo) { this.statusAtivo = statusAtivo; }
}