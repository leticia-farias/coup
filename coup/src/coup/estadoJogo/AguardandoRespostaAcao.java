package coup.estadoJogo;

import java.util.HashSet;
import java.util.Set;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoRespostaAcao implements IEstadoJogo {

	private ContextoJogo contexto;

	public AguardandoRespostaAcao(ContextoJogo contexto) {
		this.contexto = contexto;
	}

	@Override
	public void escolherAcao(Acao acao) {
		throw new IllegalStateException("Ação já foi escolhida. Aguardando respostas.");
	}

	@Override
	public void responderAcao(Jogador respondente, int resposta) {
		// contestacao
		if (resposta == 1) {
			// TODO: resolver problema com construtor
//			contexto.setEstado(new ResolvendoContestacao(contexto, respondente));
			
		// bloqueio
		} else if (resposta == 2) {
			// TODO: resolver problema com construtor
//			contexto.setEstado(new AguardandoRespostaBloqueio(contexto, respondente));
			
		// aceite
		} else {
			contexto.registrarAceites();
		}

	}

	@Override
	public void descartarCarta(Carta carta) {
		throw new IllegalStateException("Não é o momento de descartas cartas.");
	}

}
