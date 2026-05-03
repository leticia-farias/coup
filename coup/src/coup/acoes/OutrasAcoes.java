package coup.acoes;

import coup.domain.model.Jogador;

public class OutrasAcoes {

	// ajuda externa: pega 2 moedas do jogo -> bloqueado pelo duque
	public void pegarAjudaExterna(Jogador jogador) {
		int saldoAtualizado = jogador.getSaldo() + 2;
		jogador.setSaldo(saldoAtualizado);
	}

	
	// deixar herança
}
