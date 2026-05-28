package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class Assassinar implements Acao {

    @Override
    public boolean requerAlvo() {
        return true;
    }

    @Override
    public boolean podeSerbloqueado() {
        return true; // Bloqueado pela condessa
    }

    @Override
    public boolean podeSerContestada() {
        return true;
    }

    @Override
    public boolean precisaSaldoMinino() {
        return true;
    }

    @Override
    public PersonagensNomes getPersonagemNecessario() {
        return PersonagensNomes.ASSASSINO;
    }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        if (autor.getSaldo() >= 3 && alvo != null) {
            autor.setSaldo(autor.getSaldo() - 3);
            
            System.out.println(autor.getNome() + " pagou 3 moedas para assassinar " + alvo.getNome() + ".");
        }
    }
}