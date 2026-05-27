package coup.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coup.factory.IJogoFactory;

public class Baralho {
	
	// Inicialização direta para evitar NullPointerException
	List<Personagem> personagensLista = new ArrayList<>();
	List<Carta> baralhoCartas = new ArrayList<>();
	
	public Baralho(List<Personagem> personagensLista, List<Carta> baralhoCartas) {
		this.personagensLista = personagensLista;
		this.baralhoCartas = baralhoCartas;
	}

	public Baralho() { 
		// Construtor vazio agora é seguro pois as listas já foram inicializadas acima
	}

	public List<Carta> gerarBaralho(int quantJogadores, IJogoFactory tipoJogo) {
        // Limpa a lista para garantir que não acumula se for chamado mais de uma vez
		personagensLista.clear(); 
		baralhoCartas.clear();

		personagensLista.add(new Assassino());
		personagensLista.add(new Capitao());
		personagensLista.add(new Condessa());
		personagensLista.add(new Duque());
		
		// DICA: Como você passou o tipoJogo (Factory), você pode adicionar a lógica 
		// para colocar o Embaixador ou Inquisidor aqui. Exemplo:
		if (tipoJogo.getClass().getSimpleName().equals("FactoryVersaoOriginal")) {
			personagensLista.add(new Embaixador());
		} else {
			personagensLista.add(new Inquisidor());
		}
		
		int quantCartasPorPersonagem;
		
		// 3 cartas para cada personagem
		if (quantJogadores <= 6) {
			quantCartasPorPersonagem = 3;
			
			for (int i = 0; i < quantCartasPorPersonagem; i++) {
				for (Personagem p : personagensLista) {
					baralhoCartas.add(new Carta(p));					
				}
			}
		} else {
			// Adicionar lógica para 7-8 jogadores futuramente
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
