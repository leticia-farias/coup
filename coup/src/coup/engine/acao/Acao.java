package coup.engine.acao;

import coup.domain.JogoEstado;
import coup.domain.model.Jogador;

public interface Acao {
	
	// regras que toda ação precisa implementar
	boolean requerAlvo();
	boolean podeSerbloqueado();
	boolean podeSerContestada();
	boolean precisaSaldoMinino(); // para casos que precisam de um valor minimo de moedas
	
	void executar(Jogador autor, Jogador alvo, JogoEstado estadoJogo);

	// obs.: representa a interface comando do command pattern
	// as classes de cada ação (ex. ducar) representa os comandos concretos
}
