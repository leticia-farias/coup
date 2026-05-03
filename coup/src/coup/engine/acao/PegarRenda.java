package coup.engine.acao;

import coup.domain.JogoEstado;
import coup.domain.model.Jogador;

public class PegarRenda implements Acao {

	
	@Override
	public boolean requerAlvo() {
		return false;
	}

	@Override
	public boolean podeSerbloqueado() {
		return false;
	}

	@Override
	public boolean podeSerContestada() {
		return false;
	}

	// renda: pega 1 moeda do jogo
	@Override
	public void executar(Jogador autor, Jogador alvo, JogoEstado estadoJogo) {
		autor.setSaldo(autor.getSaldo() + 1);		
	}

}
