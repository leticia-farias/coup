package coup.acoes;

import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class InquisidarEspionar implements Acao {
   
    @Override public boolean requerAlvo() { 
        return true; 
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
         return PersonagensNomes.INQUISIDOR ; 
    }
     
     @Override
    public void executar(Jogador autor, Jogador alvo) {
        // A lógica de forçar a troca ocorrerá no Controller após os blefes
    }
}
