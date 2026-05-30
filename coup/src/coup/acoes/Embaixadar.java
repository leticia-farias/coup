package coup.acoes;

import java.util.List;

import coup.model.Jogador;
import coup.model.PersonagensNomes;
import coup.estadoJogo.ContextoJogo;
import coup.estadoJogo.IEstadoJogo;
import coup.estadoJogo.AguardandoTrocaEmbaixador;

public class Embaixadar implements Acao {

	@Override
	public boolean requerAlvo() {
		return false;
	}

	@Override
	public boolean podeSerbloqueado() {
		return false;
	}

	@Override
	public boolean podeSerContestada() {
		return true;
	}

	@Override
	public boolean precisaSaldoMinino() {
		return false;
	}

	@Override
	public PersonagensNomes getPersonagemNecessario() {
		return PersonagensNomes.EMBAIXADOR;
	}

	@Override
	public void executar(Jogador autor, Jogador alvo) {
		// Fica vazio. A lógica de compra de cartas ocorre na transição de estado.
	}
	
	@Override
	public IEstadoJogo proximoEstado(ContextoJogo contexto) {
		// Puxa 2 cartas extras e coloca temporariamente na mão do jogador
		contexto.getJogadorAutor().getJogadorCartas().getCartas().add(contexto.getBaralho().comprarCarta());
		contexto.getJogadorAutor().getJogadorCartas().getCartas().add(contexto.getBaralho().comprarCarta());
		
		return new AguardandoTrocaEmbaixador(contexto, contexto.getJogadorAutor(), contexto.getBaralho());
	}

	@Override
	public List<PersonagensNomes> getPersonagensBloquadores() {
		return List.of();
	}
}