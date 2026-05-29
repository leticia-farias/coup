package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Baralho;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoTrocaInquisidor implements IEstadoJogo {

    private ContextoJogo contexto;
    private Jogador autor;
    private Baralho baralho;

    public AguardandoTrocaInquisidor(ContextoJogo contexto, Jogador autor, Baralho baralho) {
        this.contexto = contexto;
        this.autor = autor;
        this.baralho = baralho;
    }

    public Jogador getAutor() { return autor; }

    // Chamado uma única vez: o jogador devolve 1 carta e fica com 1
    @Override
    public void descartarCarta(Carta carta) {
        autor.getJogadorCartas().getCartas().remove(carta);
        baralho.devolverCarta(carta);
        contexto.setEstado(new AguardandoAcao(contexto));
    }

    @Override public void escolherAcao(Acao acao) {
        throw new IllegalStateException("Aguardando troca do Inquisidor.");
    }
    @Override public void responderAcao(Jogador respondente, int resposta) {
        throw new IllegalStateException("Aguardando troca do Inquisidor.");
    }
}