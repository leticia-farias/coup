package coup.acoes;

import coup.jogador.Jogador;

public class Golpe {

	// golpe de estado: paga 7 moedas para matar qualquer personagem !! Jogador com
	// mais de 10 moedas é obrigado a fazer golpe de estado
	public void golpear(Jogador jogador) {
		int saldoAtualizado = jogador.getSaldo() - 7;
		jogador.setSaldo(saldoAtualizado);
	}

}
