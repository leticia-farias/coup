package coup;

import coup.domain.model.Jogador;
import coup.engine.acao.Acao;

public class AguardandoAcao implements Turno {
    
    @Override
    public void tentarAcao(TurnoContexto contexto, Jogador jogador, Acao acao) {
        contexto.setAcaoPendente(acao);
        
        /*
        if (acao.isPodeSerContestada()) {
            contexto.setEstado(new AguardandoContestacao());
        } else if (acao.isPodeSerBloqueada()) {
            contexto.setEstado(new AguardandoBloqueio());
        } else {
            contexto.setEstado(new ResolvendoAcao());
        }
        */
    }

    @Override
    public void contestarAcao(TurnoContexto contexto, Jogador contestador) {
        throw new IllegalStateException("Nenhuma ação para contestar ainda.");
    }

    @Override
    public void aceitarAcao(TurnoContexto contexto) {
        throw new IllegalStateException("Nenhuma ação para aceitar.");
    }

    @Override
    public void bloquearAcao(TurnoContexto contexto, Jogador bloqueador) {
        throw new IllegalStateException("Nenhuma ação para bloquear.");
    }
}