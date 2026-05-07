package coup;

import coup.jogador.Jogador;
import coup.personagens.PersonagensNomes;

public class Ajudar implements Acao {

    @Override
    public boolean requerAlvo() {
        return false;
    }

    @Override
    public boolean podeSerbloqueado() {
        return true; 
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
        autor.setSaldo(autor.getSaldo() + 2);
        System.out.println(autor.getNome() + " recebeu 2 moedas de ajuda externa.");
    }
}