package coup.engine.acao;

import coup.domain.JogoEstado;
import coup.domain.model.Jogador;

public class Golpe implements Acao {

	
	@Override
	public boolean requerAlvo() {
		return true;
	}

	@Override
	public boolean podeSerbloqueado() {
		return false;
	}

	@Override
	public boolean podeSerContestada() {
		return false;
	}
	
	@Override
	public boolean precisaSaldoMinino() {
		return true;
	}

	// renda: pega 1 moeda do jogo
	@Override
	public void executar(Jogador autor, Jogador alvo, JogoEstado estadoJogo) {
	}

}
