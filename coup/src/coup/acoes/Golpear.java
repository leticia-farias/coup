package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

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

    // não pode blefar
    @Override
    public PersonagensNomes getPersonagemNecessario() {
        return null;
    }

    @Override
    public void executar(Jogador autor, Jogador alvo) {

        if (alvo == null) {
            System.out.println("Nenhum alvo selecionado.");
            return;
        }

        if (autor.getSaldo() < 7) {
            System.out.println("Saldo insuficiente para golpe.");
            return;
        }

        // paga 7 moedas
        autor.setSaldo(autor.getSaldo() - 7);

        System.out.println(autor.getNome() + " deu um GOLPE em " + alvo.getNome());

        // alvo perde uma carta
        alvo.escolherCartaParaMorrer();
    }
}

