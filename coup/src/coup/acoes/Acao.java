package coup.acoes;

import java.util.List;

import coup.estadoJogo.AguardandoAcao;
import coup.estadoJogo.ContextoJogo;
import coup.estadoJogo.IEstadoJogo;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public interface Acao {
	
	// regras que toda ação precisa implementar
	boolean requerAlvo();
	boolean podeSerbloqueado();
	boolean podeSerContestada();
	boolean precisaSaldoMinino(); // para casos que precisam de um valor minimo de moedas
	
	void executar(Jogador autor, Jogador alvo);
	List<PersonagensNomes> getPersonagensBloquadores();

	// obs.: representa a interface comando do command pattern
	// as classes de cada ação (ex. ducar) representa os comandos concretos

	    PersonagensNomes getPersonagemNecessario();

	 // Método padrão: a maioria das ações volta para AguardandoAcao
	    default IEstadoJogo proximoEstado(ContextoJogo contexto) {
	        return new AguardandoAcao(contexto);
	    }
}