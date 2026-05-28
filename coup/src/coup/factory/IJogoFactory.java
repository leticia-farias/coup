package coup.factory;

import coup.acoes.Acao;
import coup.model.Baralho;

public interface IJogoFactory {
	
	public Acao acoes(int opcao, Baralho baralho);
	
}
