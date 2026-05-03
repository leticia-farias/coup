package coup.engine.regras;

import coup.domain.model.Jogador;
import coup.domain.model.Personagem;

public class ResolucaoContestacao {

public boolean contestar(Jogador jogadorDuvidando, Jogador jogadorContestado, Personagem personagemContestado) {
		
		// SE TIVER AS DUAS CARTAS AINDA
		if (jogadorContestado.getJogadorCartas().isStatusCompleto()) {
			System.out.println(
					jogadorContestado.getNome() + " foi contestado e agora está escolhendo qual carta mostrar");
		}
		
		Personagem personagemMostrado = jogadorContestado.escolherCartaParaMostrar();
		
		if (personagemContestado.equals(personagemMostrado)) {
			jogadorDuvidando.escolherCartaParaMorrer();
		} else {
			jogadorContestado.escolherCartaParaMorrer();
		}
		return true;
	}
}
