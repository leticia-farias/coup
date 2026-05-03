package coup.engine;

import coup.console.Console;
import coup.console.Input;
import coup.domain.JogoEstado;
import coup.domain.model.Jogador;
import coup.engine.acao.Acao;

public class JogoController {
	
	Console console = new Console();
	Input input = new Input();
	
	// obtem jogadorAtual de jogoEstado
	//pede a console qual a jogada pretendida 
	// constroi a acao correspondente 
	// se a acao for contestada, pede a consoleView se alguém contesta, se sim, delega para o resolvedorDeConflitos
	// se não houver bloqueio ou contestação fatal, chama a acao.exeutar
	// chama estado.avancarTurno
	
	public JogoController(JogoEstado estado, Console console2) {
		// TODO Auto-generated constructor stub
	}

	public void processarTurno(Jogador jogadorAtual, Acao acaoEscolhida, Jogador alvo) {
		
		if (acaoEscolhida.podeSerContestada()) {
			boolean contestacao = verificarContestacao(jogadorAtual);
			
			if (contestacao) {
				boolean blefe = resolverContestacao(jogadorAtual, acaoEscolhida);
				
				if (blefe) return;
			}
		}
		
		if (acaoEscolhida.podeSerbloqueado()) {
			boolean bloqueado = verificarBloqueio(alvo);
			
			if (bloqueado) return;
		}
		
		acaoEscolhida.executar(jogadorAtual, alvo, this.jogoEstado);
	}

	public void iniciarPartida() {
		// TODO Auto-generated method stub
		
	}

}
