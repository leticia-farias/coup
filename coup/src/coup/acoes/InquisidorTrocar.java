package coup.acoes;

import java.util.List;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class InquisidorTrocar implements Acao {
    @Override public boolean requerAlvo()         { return false; }
    @Override public boolean podeSerbloqueado()    { return false; }
    @Override public boolean podeSerContestada()   { return true; }
    @Override public boolean precisaSaldoMinino()  { return false; }
    @Override public PersonagensNomes getPersonagemNecessario() { return PersonagensNomes.INQUISIDOR; }
    @Override public List<PersonagensNomes> getPersonagensBloquadores() { return List.of(); }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        // A lógica de compra e devolução fica no estado AguardandoTrocaInquisidor
        // executar() é chamado apenas para confirmar que a ação passou sem contestação
    }
}