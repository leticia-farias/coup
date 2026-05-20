package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Jogador;

public class AguardandoAcao implements IEstadoJogo {

	private ContextoJogo contexto;
	
	public AguardandoAcao(ContextoJogo contexto) {
		this.contexto = contexto;
	}

	@Override
	public void aguardandoAcao(Acao acao) {
		// chamar ui para mostrar opcoes
		// contexto.setEstado(new AguardandoRespostaAcao());
	}

	@Override
	public void aguardandoRespostaAcao() {
		throw new IllegalStateException("Ação ainda não foi escolhida");
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

	
}
