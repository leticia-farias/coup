package coup.acoes;

import coup.jogador.Jogador;
import coup.personagens.Personagem;

public class Contestacao {

	public boolean contestar(Jogador jogadorDuvidando, Jogador jogadorContestado, Personagem personagemContestado) {
		
		// SE TIVER AS DUAS CARTAS AINDA
		if (jogadorContestado.getJogadorCartas().isStatusCompleto()) {
			System.out.println(
					jogadorContestado.getNome() + " foi contestado e agora está escolhendo qual carta mostrar");
		}
		
		Personagem personagemMostrado = jogadorContestado.escolherCartaParaMostrar();
		
		if (personagemContestado.getNome() == personagemMostrado.getNome()) {
			jogadorDuvidando.escolherCartaParaMorrer();
		} else {
			jogadorContestado.escolherCartaParaMorrer();
		}
		return true;
	}

}
