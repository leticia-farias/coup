package coup.engine.acao;

import coup.domain.JogoEstado;
import coup.domain.model.Jogador;

public class Capitar implements Acao {


	@Override
	public boolean requerAlvo() {
		return true;
	}

	// bloqueado por outro capitão ou embaixador/inquisidor
	@Override
	public boolean podeSerbloqueado() {
		return true;
	}

	@Override
	public boolean podeSerContestada() {
		return true;
	}

	// capitão: pega 2 moedas de outro jogador
	@Override
	public void executar(Jogador autor, Jogador alvo, JogoEstado estadoJogo) {
		autor.setSaldo(autor.getSaldo() + 2);
		alvo.setSaldo(alvo.getSaldo() - 2);
	}

}
