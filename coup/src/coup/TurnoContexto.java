package coup;

import coup.domain.model.Jogador;
import coup.engine.acao.Acao;

public class TurnoContexto {
	private Turno estadoAtual;
	private Acao acaoPendente;
	private Jogador jogadorAtual;

	public TurnoContexto(Jogador jogadorAtual) {
		this.jogadorAtual = jogadorAtual;
		//this.estadoAtual = new AguardandoAcao();
	}

	public void setEstado(Turno novoEstado) {
		this.estadoAtual = novoEstado;
	}

	public void setAcaoPendente(Acao acao) {
		this.acaoPendente = acao;
	}

	public Acao getAcaoPendente() {
		return acaoPendente;
	}

	public void tentarAcao(Acao acao) {
		estadoAtual.tentarAcao(this, jogadorAtual, acao);
	}

	public void contestarAcao(Jogador contestador) {
		estadoAtual.contestarAcao(this, contestador);
	}

	public void aceitarAcao() {
		estadoAtual.aceitarAcao(this);
	}
}