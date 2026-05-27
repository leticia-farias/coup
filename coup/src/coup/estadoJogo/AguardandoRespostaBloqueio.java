package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class AguardandoRespostaBloqueio implements IEstadoJogo {

	private ContextoJogo contexto;
	private Jogador bloqueador;
	private PersonagensNomes personagemBloqueador;
	
	public AguardandoRespostaBloqueio(ContextoJogo contexto, Jogador bloqueador,
			PersonagensNomes personagemBloqueador) {
		this.contexto = contexto;
		this.bloqueador = bloqueador;
		this.personagemBloqueador = personagemBloqueador;
	}
	
	@Override
	public void responderAcao(Jogador respondente, int resposta) {
		if (resposta == 1) {
			// jogador original contesta bloqueio
			contexto.setEstado(new ResolvendoContestacao(contexto, respondente, bloqueador, personagemBloqueador));
		} else if (resposta == 2) {
			// jogador da ação original aceitou o bloqueio
			contexto.setEstado(new AguardandoAcao(contexto));
		}
	}

	@Override
	public void escolherAcao(Acao acao) {
		throw new IllegalStateException("Ação bloqueada, aguardando respostas ao bloqueio");			
	}

	@Override
	public void descartarCarta(Carta carta) {
		throw new IllegalStateException("Não é o momento de descarte.");
	}

}
