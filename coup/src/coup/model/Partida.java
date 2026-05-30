package coup.model;

import java.util.List;
import coup.factory.IJogoFactory;

public class Partida {

    private List<Jogador> jogadores;
    private Baralho baralho;
    private IJogoFactory versaoJogo;
    private int indexJogadorAtual = -1;

    public Partida(List<Jogador> jogadores, Baralho baralho, IJogoFactory versaoJogo) {
        this.jogadores = jogadores;
        this.baralho = baralho;
        this.versaoJogo = versaoJogo;
    }

    public Jogador avancarTurno() {
        do {
            indexJogadorAtual = (indexJogadorAtual + 1) % jogadores.size();
        } while (!jogadores.get(indexJogadorAtual).isStatusAtivo());
        
        return jogadores.get(indexJogadorAtual);
    }

    public boolean isFimDeJogo() {
        return contarJogadoresVivos() <= 1;
    }

    public Jogador getVencedor() {
        return jogadores.stream().filter(Jogador::isStatusAtivo).findFirst().orElse(null);
    }

    public int contarJogadoresVivos() {
        return (int) jogadores.stream().filter(Jogador::isStatusAtivo).count();
    }

    public List<Jogador> getJogadores() { return jogadores; }
    public Baralho getBaralho() { return baralho; }
    public IJogoFactory getVersaoJogo() { return versaoJogo; }
}