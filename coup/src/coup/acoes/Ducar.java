package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

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

    @Override
    public PersonagensNomes getPersonagemNecessario() {
        return PersonagensNomes.DUQUE;
    }

	// duque: pegua 3 moedas do jogo
    @Override
    public void executar(Jogador autor, Jogador alvo) {
        System.out.println(autor.getNome() + " usa Duque (Imposto) e ganha 3 moedas.");
        autor.setSaldo(autor.getSaldo() + 3);
    }
}
