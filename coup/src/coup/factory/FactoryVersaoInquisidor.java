package coup.factory;

import coup.acoes.Acao;
import coup.acoes.Ajudar;
import coup.acoes.Assassinar;
import coup.acoes.Capitar;
import coup.acoes.Ducar;
import coup.acoes.Golpear;
import coup.acoes.InquisidorExaminar;
import coup.acoes.InquisidorTrocar;
import coup.acoes.Receitar;
import coup.model.Baralho;

public class FactoryVersaoInquisidor implements IJogoFactory {

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
			acao = new InquisidorTrocar();
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
		case 8: 
			acao = new InquisidorExaminar();
		 	break; 	
		}
		return acao;
	}
}