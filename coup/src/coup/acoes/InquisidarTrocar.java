package coup.acoes;

import coup.model.Baralho;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class InquisidarTrocar implements Acao {
    private Baralho baralho;

    public InquisidarTrocar(Baralho baralho) {
        this.baralho = baralho;
    }

    @Override public boolean requerAlvo() { 
        return false; 
    }

    @Override public boolean podeSerbloqueado() { 
        return false; 
    }

    @Override public boolean podeSerContestada() {
         return true; 
        }

    @Override public boolean precisaSaldoMinino() { 
        return false;
     }

    @Override public PersonagensNomes getPersonagemNecessario() {
         return PersonagensNomes.INQUISIDOR; 
        }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        // Puxa 1 carta extra e coloca temporariamente na mão
        autor.getJogadorCartas().getCartas().add(baralho.comprarCarta());
    }
}

