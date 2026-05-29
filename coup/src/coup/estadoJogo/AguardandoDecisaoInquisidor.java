package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Baralho;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoDecisaoInquisidor implements IEstadoJogo {

    private ContextoJogo contexto;
    private Jogador inquisidor;
    private Jogador alvo;

    public AguardandoDecisaoInquisidor(ContextoJogo contexto, Jogador inquisidor, Jogador alvo) {
        this.contexto = contexto;
        this.inquisidor = inquisidor;
        this.alvo = alvo;
    }

    public Jogador getInquisidor() { return inquisidor; }
    public Jogador getAlvo()       { return alvo; }

    // Chamado pelo controller após ver a carta e tomar a decisão
    public void resolverExame(Carta cartaRevelada, boolean forcaExame, Baralho baralho) {
        if (forcaExame) {
            alvo.getJogadorCartas().getCartas().remove(cartaRevelada);
            baralho.devolverCarta(cartaRevelada);
            Carta novaCarta = baralho.comprarCarta();
            if (novaCarta != null) {
                alvo.getJogadorCartas().getCartas().add(novaCarta);
            }
        }
        contexto.setEstado(new AguardandoAcao(contexto));
    }

    @Override public void escolherAcao(Acao acao) {
        throw new IllegalStateException("Aguardando decisão do Inquisidor.");
    }
    @Override public void responderAcao(Jogador respondente, int resposta) {
        throw new IllegalStateException("Aguardando decisão do Inquisidor.");
    }
    @Override public void descartarCarta(Carta carta) {
        throw new IllegalStateException("Aguardando decisão do Inquisidor.");
    }
}