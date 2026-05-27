package coup.acoes;

import coup.factory.IJogoFactory;
import coup.model.Baralho;

public class FactoryVersaoInquisidor implements IJogoFactory {

	private Acao acao;
	
	@Override
	public Acao acoes(int opcao, Baralho baralho) {
		switch (opcao) {
		case 1:
			acao = new Receitar();
			break;

		case 2:
			acao = new Ajudar();
			break;

		case 3:
			acao = new Ducar();
			break;

		case 4:
			acao = new Capitar();
			break;

		case 5:
			acao = new Ducar();
			break;

		case 6:
			acao = new Ducar(); // inquisidar
			break;

		case 7:
			acao = new Assassinar();
			break;

		case 8:
			acao = new Golpear();
			break;
		}

		return acao;
	}

}
