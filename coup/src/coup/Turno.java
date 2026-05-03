package coup;

import coup.domain.model.Jogador;
import coup.engine.acao.Acao;

public interface Turno {
	void tentarAcao(TurnoContexto contexto, Jogador jogador, Acao acao);
    void contestarAcao(TurnoContexto contexto, Jogador contestador);
    void aceitarAcao(TurnoContexto contexto);
    void bloquearAcao(TurnoContexto contexto, Jogador bloqueador);
}