package coup.factory;

import coup.acoes.Acao;

public abstract class IJogoFactory {
	
	protected Acao acao;

	protected abstract Acao acoes(int opcao);
	
}
