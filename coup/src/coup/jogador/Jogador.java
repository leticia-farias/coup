package coup.jogador;

import java.util.Scanner;

import coup.Carta;
import coup.MenuOpcoes;
import coup.personagens.Personagem;

public class Jogador {

    int id;
    String nome;
    int saldo = 2;
    JogadorCartas jogadorCartas = new JogadorCartas();
    boolean statusAtivo = true;

    Scanner sc = new Scanner(System.in);
    MenuOpcoes menu = new MenuOpcoes();

    public Personagem escolherCartaParaMostrar() {
        while (true) {
            menu.mostrarCartas(this);
            System.out.println("DIGITE O NÚMERO DA CARTA QUE DESEJA MOSTRAR:");

            int resposta;

            try {
                resposta = sc.nextInt();
            } catch (Exception e) {
                System.out.println("Entrada inválida. Digite apenas o número da carta.");
                sc.next();
                continue;
            }

            if (resposta >= 0 && resposta < jogadorCartas.getCartas().size()) {
                Carta cartaEscolhida = jogadorCartas.getCartas().get(resposta);

                if (cartaEscolhida.isStatusAtiva()) {
                    return cartaEscolhida.getPersonagem();
                } else {
                    System.out.println("Essa carta já está morta. Escolha outra.");
                }
            } else {
                System.out.println("Número inválido. Escolha uma carta da lista.");
            }
        }
    }

    public void escolherCartaParaMorrer() {
        menu.mostrarCartas(this);
        System.out.println("DIGITE O NÚMERO DE QUAL CARTA VOCÊ DESEJA MATAR:");
        int r = sc.nextInt();

        if (r >= 0 && r < jogadorCartas.getCartas().size()) {
            Carta cartaEscolhida = jogadorCartas.getCartas().get(r);

            if (cartaEscolhida.isStatusAtiva()) {
                cartaEscolhida.setStatusAtiva(false);
                System.out.println(nome + " perdeu a carta " + cartaEscolhida.getPersonagem().getNome());
            } else {
                System.out.println("Essa carta já está morta.");
                return;
            }
        } else {
            System.out.println("Carta inválida.");
            return;
        }

        atualizarStatus();
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

    public Jogador(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Jogador() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public JogadorCartas getJogadorCartas() {
        return jogadorCartas;
    }

    public void setJogadorCartas(JogadorCartas jogadorCartas) {
        this.jogadorCartas = jogadorCartas;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }
}