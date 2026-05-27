
package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class Assassinar implements Acao {

    @Override
    public boolean requerAlvo() {
        return true;
    }

    // bloqueado pela condessa
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
        return true;
    }

    // personagem necessário
    @Override
    public PersonagensNomes getPersonagemNecessario() {
        return PersonagensNomes.ASSASSINO;
    }

    // assassino: paga 3 moedas e mata carta de outro jogador
    @Override
    public void executar(Jogador autor, Jogador alvo) {
    	if (autor.getSaldo() < 3) {
            System.out.println("Saldo insuficiente para assassinar.");
            return;
        }
        autor.setSaldo(autor.getSaldo() - 3);
        System.out.println(autor.getNome() + " assassinou uma carta de " + alvo.getNome());
        alvo.escolherCartaParaMorrer();
    }
}
