package coup;

import coup.jogador.Jogador;

public class Golpear implements Acao {

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

	@Override
	public void executar(Jogador autor, Jogador alvo) {
		
	}

}