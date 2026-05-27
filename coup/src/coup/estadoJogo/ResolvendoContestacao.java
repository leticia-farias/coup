package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class ResolvendoContestacao implements IEstadoJogo {
	
	private ContextoJogo contexto;
	private Jogador contestado;
	private Jogador contestador;
	private PersonagensNomes personagemContestado;

	public ResolvendoContestacao(ContextoJogo contexto, Jogador contestado, Jogador contestador,
			PersonagensNomes personagemContestado) {
		this.contexto = contexto;
		this.contestado = contestado;
		this.contestador = contestador;
		this.personagemContestado = personagemContestado;
	}

	public void resolverContestacao() {
	    boolean temCarta = contestado.getJogadorCartas().getCartas().stream()
	            .anyMatch(c -> c.isStatusAtiva() && c.getPersonagem().getNome() == personagemContestado);

	    if (temCarta) {
	        // O autor TEM a carta. O contestador mentiu/errou e perde a carta.
	        // Ação original continua (cancelarAcaoOriginal = false)
	        contexto.setEstado(new AguardandoDescarte(contexto, contestador, false));
	    } else {
	        // O autor NÃO TEM a carta. O autor perde a carta.
	        // Ação original é cancelada (cancelarAcaoOriginal = true)
	        contexto.setEstado(new AguardandoDescarte(contexto, contestado, true));
	    }
	}
	
	@Override
	public void escolherAcao(Acao acao) {
		throw new IllegalStateException("Resolvendo desafio, não é possível escolher ação no momento.");
	}

	@Override
	public void responderAcao(Jogador respondente, int resposta) {
		throw new IllegalStateException("Resolvendo desafio, n ão é possível responder a uma ação no momento.");
	}

	@Override
	public void descartarCarta(Carta carta) {		
		throw new IllegalStateException("Aguarde a transição para a fase de descarte.");
	}

}
