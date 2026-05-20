package coup.estadoJogo;

import java.util.HashSet;
import java.util.Set;

import coup.acoes.Acao;
import coup.model.Jogador;

public class AguardandoRespostaAcao implements IEstadoJogo {
	
	private ContextoJogo contexto;
	private Acao acaoPendente;
	private Set<Jogador> jogadoresQueAceitaram;

	public AguardandoRespostaAcao(ContextoJogo contexto, Acao acaoPendente, Set<Jogador> jogadoresQueAceitaram) {
		this.contexto = contexto;
		this.acaoPendente = acaoPendente;
		this.jogadoresQueAceitaram = new HashSet<Jogador>();
	}

	@Override
	public void aguardandoAcao(Acao acao) {
		// TODO Auto-generated method stub

	}

	@Override
	public void aguardandoRespostaAcao() {
		// TODO Auto-generated method stub

	}

	@Override
	public void aguardandoRespostaBloqueio() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resolvendoDesafio() {
		// TODO Auto-generated method stub

	}

	@Override
	public void aguardandoDescarte() {
		// TODO Auto-generated method stub

	}

	@Override
	public void executandoAcao(Acao acao) {
		// TODO Auto-generated method stub

	}

	@Override
	public void trocaTurno(Jogador jogador) {
		// TODO Auto-generated method stub

	}

	private void jogadoresQueVotaram() {
		
	}
}
