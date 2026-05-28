package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;

public interface IEstadoJogo {

	public void escolherAcao(Acao acao);
    public void responderAcao(Jogador respondente, int resposta);
    public void descartarCarta(Carta carta);
	
}
