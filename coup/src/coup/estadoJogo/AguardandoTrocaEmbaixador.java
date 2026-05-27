package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Baralho;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoTrocaEmbaixador implements IEstadoJogo {

    private ContextoJogo contexto;
    private Jogador autor;
    private Baralho baralho;
    private int cartasParaDevolver = 2;

    public AguardandoTrocaEmbaixador(ContextoJogo contexto, Jogador autor, Baralho baralho) {
        this.contexto = contexto;
        this.autor = autor;
        this.baralho = baralho;
    }

    @Override
    public void descartarCarta(Carta carta) {
        // Remove da mão e devolve ao baralho
        autor.getJogadorCartas().getCartas().remove(carta);
        baralho.devolverCarta(carta);
        
        cartasParaDevolver--;

        // Quando devolver as 2, encerra o turno
        if (cartasParaDevolver == 0) {
            contexto.setEstado(new AguardandoAcao(contexto));
        }
    }

    @Override
    public void escolherAcao(Acao acao) {
        throw new IllegalStateException("Aguardando troca de cartas do Embaixador.");
    }

    @Override
    public void responderAcao(Jogador respondente, int resposta) {
        throw new IllegalStateException("Aguardando troca de cartas do Embaixador.");
    }
}