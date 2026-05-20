package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

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
        return false;
    }

    @Override
    public boolean precisaSaldoMinino() {
        return false;
    }

    @Override
    public PersonagensNomes getPersonagemNecessario() {
        return null;
    }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        autor.setSaldo(autor.getSaldo() + 1);
        System.out.println(autor.getNome() + " recebeu 1 moeda de renda.");
    }
}
