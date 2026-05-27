package coup.acoes;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class Ducar implements Acao {
    @Override public boolean requerAlvo() { return false; }
    @Override public boolean podeSerbloqueado() { return false; }
    @Override public boolean podeSerContestada() { return true; }
    @Override public boolean precisaSaldoMinino() { return false; }
    @Override public PersonagensNomes getPersonagemNecessario() { return PersonagensNomes.DUQUE; }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        // Apenas a regra de negócio
        autor.setSaldo(autor.getSaldo() + 3); 
    }
}