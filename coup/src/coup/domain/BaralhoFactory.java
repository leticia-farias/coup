package coup.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coup.domain.model.Baralho;
import coup.domain.model.Carta;
import coup.domain.model.Personagem;

public class BaralhoFactory {
	
	// baralho com embaixador
	public static Baralho criarBaralhoPadrao(int quantidadeJogadores) {
		
		int quantCartasPorPersonagem;
		
		if (quantidadeJogadores <= 6) {
			quantCartasPorPersonagem = 3;
			
		} else if (quantidadeJogadores <= 8) {
			quantCartasPorPersonagem = 4;
			
		} else {
			quantCartasPorPersonagem = 5;
		}
			
		List<Carta> cartas = new ArrayList<>();
		
		for (int i = 0; i < quantCartasPorPersonagem; i++) {
			cartas.add(new Carta(Personagem.ASSASSINO));
			cartas.add(new Carta(Personagem.CAPITAO));
			cartas.add(new Carta(Personagem.CONDESSA));
			cartas.add(new Carta(Personagem.DUQUE));
			cartas.add(new Carta(Personagem.EMBAIXADOR));
		}
        
        Collections.shuffle(cartas);
        return new Baralho(cartas);
    }
}
