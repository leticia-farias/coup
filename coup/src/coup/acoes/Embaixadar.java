package coup.acoes;

import coup.model.Baralho;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class Embaixadar implements Acao {

	private Baralho baralho;

    public Embaixadar(Baralho baralho) {
        this.baralho = baralho;
    }

    @Override
    public boolean requerAlvo() { return false; }

    @Override
    public boolean podeSerbloqueado() { return false; }

    @Override
    public boolean podeSerContestada() { return true; }

    @Override
    public boolean precisaSaldoMinino() { return false; }

    @Override
    public PersonagensNomes getPersonagemNecessario() { return PersonagensNomes.EMBAIXADOR; }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        // Puxa 2 cartas extras e coloca temporariamente na mão do jogador
        autor.getJogadorCartas().getCartas().add(baralho.comprarCarta());
        autor.getJogadorCartas().getCartas().add(baralho.comprarCarta());
    }
}
