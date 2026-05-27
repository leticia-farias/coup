package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoDescarte implements IEstadoJogo {

	private ContextoJogo contexto;
	private Jogador jogadorQueDescarta;
	private boolean cancelarAcaoOriginal;

	public AguardandoDescarte(ContextoJogo contexto, Jogador jogadorQueDescarta, boolean cancelarAcaoOriginal) {
		this.contexto = contexto;
		this.jogadorQueDescarta = jogadorQueDescarta;
		this.cancelarAcaoOriginal = cancelarAcaoOriginal;
	}
	
	public Jogador getJogadorQueDescarta() {
		return jogadorQueDescarta;
	}

	@Override
	public void descartarCarta(Carta cartaEscolhida) {
		cartaEscolhida.setStatusAtiva(false);
		jogadorQueDescarta.atualizarStatus();
		
		if(cancelarAcaoOriginal) {
			// fim do turno 
			contexto.setEstado(new AguardandoAcao(contexto));
		} else {
			contexto.getAcaoPendente().executar(contexto.getJogadorAutor(), contexto.getJogadorAlvo());
			contexto.setEstado(new AguardandoAcao(contexto));
		}
	}

	@Override
	public void escolherAcao(Acao acao) {
		throw new IllegalStateException("Aguardando " + jogadorQueDescarta.getNome() + " descartar uma carta.");
	}

	@Override
	public void responderAcao(Jogador respondente, int resposta) {
		throw new IllegalStateException("Aguardando " + jogadorQueDescarta.getNome() + " descartar uma carta.");

	}

}
