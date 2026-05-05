package coup;

import coup.jogador.Jogador;

public class Ducar implements Acao {

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
		return true;
	}

    @Override
    public boolean precisaSaldoMinino() {
        return false;
    }

	// duque: pegua 3 moedas do jogo
	@Override
	public void executar(Jogador autor, Jogador alvo) {
		autor.setSaldo(autor.getSaldo() + 3);		
	}

}