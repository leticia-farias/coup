package coup.acoes;

import java.util.List;

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
    
    @Override
    public List<PersonagensNomes> getPersonagensBloquadores() {
        return List.of(PersonagensNomes.CONDESSA);
    }
    
    @Override
    public coup.estadoJogo.IEstadoJogo proximoEstado(coup.estadoJogo.ContextoJogo contexto) {
        return new coup.estadoJogo.AguardandoDescarte(contexto, contexto.getJogadorAlvo(), false);
    }
}