package coup.acoes;

import coup.jogador.Jogador;

public class AcoesPersonagens {

	// duque: pegua 3 moedas do jogo
	public void ducar(Jogador jogador) {
		int saldoAtualizado = jogador.getSaldo() + 3;
		jogador.setSaldo(saldoAtualizado);
	}

	// capitão: pega 2 moedas de outro jogador -> bloqueado por outro capitão ou
	// embaixador
	public void capitar(Jogador jogador, Jogador jogadorEscolhido) {
		if (bloquearCapitao()) {

		}
		int saldoAtualizado = jogador.getSaldo() + 2;
		jogador.setSaldo(saldoAtualizado);

		int saldoAtualizadoJEscolhido = jogadorEscolhido.getSaldo() - 2;
		jogadorEscolhido.setSaldo(saldoAtualizadoJEscolhido);
	}

	public boolean bloquearCapitao() {
		return true;
	}

	public void inquisitarAlguem(Jogador jogador, Jogador jogadorEscolhido) {

	}

	public void inquisitarBaralho(Jogador jogador, Jogador jogadorEscolhido) {

	}

	// embaixador: pega duas cartas do baralho e decide quais quer ficar
	public void embaixar() {
		// ou embaixadar?

	}

	// assassino: paga 3 moedas para matar algum personagem de outro jogador ->
	// bloqueado pela condessa
	public void assassinar() {

	}

}
