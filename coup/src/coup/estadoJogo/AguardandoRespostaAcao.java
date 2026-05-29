package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

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
		
		if (resposta == 1) { // 1 - Contestar
			PersonagensNomes personagemNecessario = contexto.getAcaoPendente().getPersonagemNecessario();
			contexto.setEstado(new ResolvendoContestacao(contexto, contexto.getJogadorAutor(), respondente, personagemNecessario));
			
		} else if (resposta == 3) {
		    // Lê o personagem que foi gravado no contexto pelo controller
		    PersonagensNomes personagemBloqueador = contexto.getPersonagemBloqueio();
		    contexto.setEstado(new AguardandoRespostaBloqueio(contexto, respondente, personagemBloqueador));	
		} else { // 2 - Aceitar
			contexto.registrarAceites();
		}

	}

	@Override
	public void descartarCarta(Carta carta) {
		throw new IllegalStateException("Não é o momento de descartar cartas.");
	}

}