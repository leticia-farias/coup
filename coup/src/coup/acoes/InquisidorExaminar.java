package coup.acoes;

import java.util.List;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class InquisidorExaminar implements Acao {
    @Override public boolean requerAlvo()         { return true; }
    @Override public boolean podeSerbloqueado()    { return false; }
    @Override public boolean podeSerContestada()   { return true; }
    @Override public boolean precisaSaldoMinino()  { return false; }
    @Override public PersonagensNomes getPersonagemNecessario() { return PersonagensNomes.INQUISIDOR; }
    @Override public List<PersonagensNomes> getPersonagensBloquadores() { return List.of(); }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        // Toda a lógica fica no estado AguardandoDecisaoInquisidor,
        // pois requer interação com a view (exibir carta privada, perguntar força)
    }
}