package coup;

import coup.jogador.Jogador;

public class MenuOpcoes {

    public void mostrarCartas(Jogador jogador) {
        System.out.println("---------------------");
        System.out.println(jogador.getNome() + ", você tem as seguintes cartas:");

        for (int i = 0; i < jogador.getJogadorCartas().getCartas().size(); i++) {
            Carta carta = jogador.getJogadorCartas().getCartas().get(i);

            String status = carta.isStatusAtiva() ? "ativa" : "morta";

            System.out.println(i + " - " + carta.getPersonagem().getNome() + " (" + status + ")");
        }

        System.out.println("---------------------");
    }
}

