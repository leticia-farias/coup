package coup.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baralho {
	
	List<Carta> baralhoCartas = new ArrayList<>();
	public Baralho(List<Carta> cartas) {
		// TODO Auto-generated constructor stub
	}

	public List<Carta> gerarBaralho(int quantJogadores, List<Personagem> personagensLista) {
		//int quantCartas;
		int quantCartasPorPersonagem;
		// int quantPersonagens = 4;
		
		// 3 cartas para cada personagem
		if (quantJogadores <= 6) {
			quantCartasPorPersonagem = 3;
			//quantCartas = quantPersonagens * quantCartasPorPersonagem;
			
			for (int i = 0; i < quantCartasPorPersonagem; i++) {
				for (Personagem p : personagensLista) {
					baralhoCartas.add(new Carta(p));					
				}
			}
			
		} else if (quantJogadores <= 8) {
			// 4
			
		} else {
			// 5 cartas
		}
	
		//embaralharCartas(baralhoCartas);
		return baralhoCartas;
	}

	public Object comprarTopo() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
