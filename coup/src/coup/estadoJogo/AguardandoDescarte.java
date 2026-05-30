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
		// Passo 15 resolvido: usamos o método centralizado no modelo do Jogador
		jogadorQueDescarta.perderCarta(cartaEscolhida);
		
		// Passo 14 resolvido: delegamos o fluxo de volta para o controlador/contexto
		if(cancelarAcaoOriginal) {
			// Fim do turno (ex: autor da ação perdeu a contestação)
			contexto.setEstado(new AguardandoAcao(contexto));
		} else {
			// A ação segue o seu rumo natural (ex: alvo perdeu a contestação)
			contexto.executarAcaoPendente();
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
