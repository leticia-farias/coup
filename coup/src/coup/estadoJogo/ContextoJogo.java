package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Baralho;
import coup.model.Jogador;

public class ContextoJogo {

	private IEstadoJogo estado;
	private Baralho baralho;
	private Jogador jogadorAutor;
	private Jogador jogadorAlvo;
	private Acao acaoPendente;
	private int aceites = 0;
	private int totalJogadoresAtivos = 0;
	
	
	public ContextoJogo(IEstadoJogo estado, Jogador jogadorInicial) {
		this.estado = estado;
		this.jogadorAutor = jogadorInicial;
	}

	public void registrarAceites() {
		this.aceites++;
		
		if (this.aceites >= totalJogadoresAtivos - 1) {
			acaoPendente.executar(jogadorAutor, jogadorAlvo);
			
			setEstado(new AguardandoRespostaAcao(this));
		}
		
		acaoPendente.executar(jogadorAutor, jogadorAlvo);

		if (acaoPendente instanceof coup.acoes.Embaixadar) {
		    setEstado(new AguardandoTrocaEmbaixador(this, jogadorAutor, baralho));
		} else {
		    // Se não for, vira o turno normalmente
		    setEstado(new AguardandoAcao(this));
		}
	}
	
	// getters e setters
	public IEstadoJogo getEstado() {
		return estado;
	}

	public void setEstado(IEstadoJogo estado) {
		this.estado = estado;
	}

	public Jogador getJogadorAutor() {
		return jogadorAutor;
	}

	public void setJogadorAutor(Jogador jogadorAtual) {
		this.jogadorAutor = jogadorAtual;
	}

	public Jogador getJogadorAlvo() {
		return jogadorAlvo;
	}

	public void setJogadorAlvo(Jogador alvo) {
		this.jogadorAlvo = alvo;
	}

	public Acao getAcaoPendente() {
		return acaoPendente;
	}

	public void setAcaoPendente(Acao acaoPendente) {
		this.acaoPendente = acaoPendente;
	}

	public int getAceites() {
		return aceites;
	}

	public void setAceites(int aceites) {
		this.aceites = aceites;
	}

	public int getTotalJogadoresAtivos() {
		return totalJogadoresAtivos;
	}
	
	public void setTotalJogadoresAtivos(int totalJogadoresAtivos) {
		this.totalJogadoresAtivos = totalJogadoresAtivos;
	}
	
}
