package coup.acoes;

import coup.factory.IJogoFactory;
import coup.model.Baralho;

public class FactoryVersaoOriginal implements IJogoFactory {
	
	@Override
	public Acao acoes(int opcao, Baralho baralho) {
		Acao acao = null;
		switch (opcao) {
		case 1:
			acao = new Ducar();
			break;
		case 2:
			acao = new Capitar();
			break;
		case 3:
			acao = new Embaixadar(baralho);
			break;
		case 4:
			acao = new Ajudar();
			break;
		case 5:
			acao = new Receitar();
			break;
		case 6:
			acao = new Assassinar();
			break;
		case 7:
			acao = new Golpear();
			break;
		}
		return acao;
	}
}