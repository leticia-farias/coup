package coup.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coup.factory.IJogoFactory;

public class Baralho {
	
	List<Personagem> personagensLista;
	List<Carta> baralhoCartas;
	
	public Baralho(List<Personagem> personagensLista, List<Carta> baralhoCartas) {
		this.personagensLista = new ArrayList<>();
		this.baralhoCartas = new ArrayList<>();
	}

	public Baralho() { }

	public List<Carta> gerarBaralho(int quantJogadores, IJogoFactory tipoJogo) {
		personagensLista.add(new Assassino());
		personagensLista.add(new Capitao());
		personagensLista.add(new Condessa());
		personagensLista.add(new Duque());
		
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
	
		baralhoCartas = embaralharCartas(baralhoCartas);
		return baralhoCartas;
	}
	
	public List<Carta> embaralharCartas(List<Carta> baralho) {
		Collections.shuffle(baralho);
		
		/* verificao */
		System.out.println("---------------------");
		System.out.println("Total de cartas: " + baralho.size());
		System.out.println("Cartas:");
		for (Carta c : baralho) {
			System.out.println(c.personagem.getNome());				
		}
		return baralho;
	}
	
	public void distribuirCartas(List<Jogador> jogadoresList, List<Carta> baralhoCartas) {
		for (int i = 0; i < 2; i++) {
			for (Jogador jogador : jogadoresList) {
				Carta carta = baralhoCartas.remove(0);
				jogador.getJogadorCartas().getCartas().add(carta);
			}			
		}
		
	}
	
	public Carta comprarCarta() {
	    if (!baralhoCartas.isEmpty()) {
	        return baralhoCartas.remove(0);
	    }
	    return null;
	}

	public void devolverCarta(Carta carta) {
	    baralhoCartas.add(carta);
	    embaralharCartas(baralhoCartas); // re-embaralha sempre que uma carta volta
	}
}
