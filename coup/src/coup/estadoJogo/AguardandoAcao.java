package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoAcao implements IEstadoJogo {

	private ContextoJogo contexto;
	private Acao acaoPendente;
	private int jogadoresConcordantes = 0;
	private int totalVotos;
	private Jogador jogadorAtual;

	public AguardandoAcao(ContextoJogo contexto, Acao acaoPendente, int totalAtivos) {
		this.contexto = contexto;
		this.acaoPendente = acaoPendente;
		this.totalVotos = totalAtivos - 1;
	}
	
	public AguardandoAcao(ContextoJogo contexto) {
		this.contexto = contexto;
	}

	@Override
	public void escolherAcao(Acao acao) {
		// TODO: implementar método de escolher ação
	}

	@Override
	public void responderAcao(Jogador respondente, int resposta) {
		throw new IllegalStateException("Não é possível responder uma ação, pois ainda está sendo escolhida");
	}

	@Override
	public void descartarCarta(Carta carta) {
		throw new IllegalStateException("Não é o momento de descartar cartas");
	}
	
	

	
}
