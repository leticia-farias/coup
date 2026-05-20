package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Jogador;

public interface IEstadoJogo {

	// public void turnoIniciado();
	public void aguardandoAcao(Acao acao);
	
	public void aguardandoRespostaAcao();
	
	public void aguardandoRespostaBloqueio();
	
	public void resolvendoDesafio();
	
	public void aguardandoDescarte();
	
	public void executandoAcao(Acao acao);
	
	public void trocaTurno(Jogador jogador);
	
}
