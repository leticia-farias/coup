package coup.estadoJogo;

import java.util.List;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class ResolvendoContestacao implements IEstadoJogo {
	
	private ContextoJogo contexto;
	private Jogador contestado;
	private Jogador contestador;
	private PersonagensNomes personagemContestado;

	public ResolvendoContestacao(ContextoJogo contexto, Jogador contestado, Jogador contestador,
			PersonagensNomes personagemContestado) {
		this.contexto = contexto;
		this.contestado = contestado;
		this.contestador = contestador;
		this.personagemContestado = personagemContestado;
	}

	public void resolverContestacao() {
	    boolean temCarta = contestado.getJogadorCartas().getCartas().stream()
	            .anyMatch(c -> c.isStatusAtiva() && c.getPersonagem().getNome() == personagemContestado);

	    if (temCarta) {
	        // Contestador errou — perde carta, ação original continua
	        contexto.setEstado(new AguardandoDescarte(contexto, contestador, false));
	    } else {
	        // Contestado não tinha a carta — perde carta, ação cancelada
	        // Se era Embaixador, devolve as cartas extras ao baralho antes de cancelar
	        if (personagemContestado == coup.model.PersonagensNomes.EMBAIXADOR) {
	            List<Carta> cartas = contestado.getJogadorCartas().getCartas();
	            // Remove as últimas 2 cartas adicionadas (as que vieram do baralho)
	            while (cartas.size() > 2) {
	                Carta extra = cartas.remove(cartas.size() - 1);
	                contexto.getBaralho().devolverCarta(extra);
	            }
	        }
	        contexto.setEstado(new AguardandoDescarte(contexto, contestado, true));
	    }
	}
	
	@Override
	public void escolherAcao(Acao acao) {
		throw new IllegalStateException("Resolvendo desafio, não é possível escolher ação no momento.");
	}

	@Override
	public void responderAcao(Jogador respondente, int resposta) {
		throw new IllegalStateException("Resolvendo desafio, n ão é possível responder a uma ação no momento.");
	}

	@Override
	public void descartarCarta(Carta carta) {		
		throw new IllegalStateException("Aguarde a transição para a fase de descarte.");
	}

}
