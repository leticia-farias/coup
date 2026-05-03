package coup.domain;

import java.util.ArrayList;
import java.util.List;

import coup.console.Console;
import coup.domain.model.Baralho;
import coup.domain.model.Jogador;

public class JogoFactory {
	
	public static JogoEstado criarNovoJogo(int quantidadeJogadores, Console console) {
		
		Baralho baralho = BaralhoFactory.criarBaralhoPadrao(quantidadeJogadores);
		
		List<Jogador> jogadoresList = new ArrayList<Jogador>();
		
		
		for (int i = 0; i < quantidadeJogadores; i++) {
			String nome = console.pedirNomeJogador(i);
			Jogador jogador = new Jogador(i, nome);
			
			jogador.adicionarMoedas(2);
			
			jogador.adicionarCarta(baralho.comprarTopo());
			jogador.adicionarCarta(baralho.comprarTopo());
			
			jogadoresList.add(new Jogador(i, nome));
		}

        return new JogoEstado(jogadoresList, baralho);
	}

}
