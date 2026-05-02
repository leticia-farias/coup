package coup.acoes;

import coup.jogador.Jogador;

public class OutrasAcoes {

	// renda: pega 1 moeda do jogo
	public void pegarRenda(Jogador jogador) {
		int saldoAtualizado = jogador.getSaldo() + 1;
		jogador.setSaldo(saldoAtualizado);
	}

	// ajuda externa: pega 2 moedas do jogo -> bloqueado pelo duque
	public void pegarAjudaExterna(Jogador jogador) {
		int saldoAtualizado = jogador.getSaldo() + 2;
		jogador.setSaldo(saldoAtualizado);
	}

	
	// deixar herança
}
