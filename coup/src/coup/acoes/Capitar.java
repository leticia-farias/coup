
package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class Capitar implements Acao {

    @Override
    public boolean requerAlvo() {
        return true;
    }

    // bloqueado por Capitão ou Embaixador/Inquisidor
    @Override
    public boolean podeSerbloqueado() {
        return true;
    }

    @Override
    public boolean podeSerContestada() {
        return true;
    }

    @Override
    public boolean precisaSaldoMinino() {
        return false;
    }

    //  personagem necessário
    @Override
    public PersonagensNomes getPersonagemNecessario() {
        return PersonagensNomes.CAPITAO;
    }

    // capitão: pega até 2 moedas de outro jogador
    @Override
    public void executar(Jogador autor, Jogador alvo) {
        if (alvo == null) {
            System.out.println("Nenhum alvo selecionado.");
            return;
        }

        int valorRoubo = Math.min(2, alvo.getSaldo());

        autor.setSaldo(autor.getSaldo() + valorRoubo);
        alvo.setSaldo(alvo.getSaldo() - valorRoubo);

        System.out.println(autor.getNome() + " roubou " + valorRoubo + " moedas de " + alvo.getNome());
    }
}
