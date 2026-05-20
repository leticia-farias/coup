package coup.acoes;

import coup.factory.IJogoFactory;

public class FactoryVersaoOriginal extends IJogoFactory {

	@Override
	public Acao acoes(int opcao) {
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
			acao = new Ducar(); // embaixar
			break;

		case 7:
			acao = new Ducar(); // assassinar
			break;

		case 8:
			acao = new Golpear();
			break;
		}
		
		return acao;
	}

}
