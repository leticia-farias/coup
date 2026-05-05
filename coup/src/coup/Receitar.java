package coup;

import coup.jogador.Jogador;

public class Receitar implements Acao {

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

	// capitão: pega 2 moedas de outro jogador
	@Override
	public void executar(Jogador autor, Jogador alvo) {
		autor.setSaldo(autor.getSaldo() + 2);
		alvo.setSaldo(alvo.getSaldo() - 2);
	}

}