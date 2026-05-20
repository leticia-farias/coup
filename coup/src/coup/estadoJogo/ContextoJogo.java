package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Jogador;

public class ContextoJogo {

	private IEstadoJogo estado;
	private Jogador jogadorAtual;
	private Acao acaoPendente;
	private Jogador jogadorAlvo;
	
	public void setEstado(IEstadoJogo novoEstado) {
		this.estado = novoEstado;
	}
	
	
}
