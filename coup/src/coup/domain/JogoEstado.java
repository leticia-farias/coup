package coup.domain;

import java.util.List;

import coup.domain.model.Baralho;
import coup.domain.model.Jogador;

public class JogoEstado {
	
	List<Jogador> jogadoresList;
	Baralho baralho;
	int indiceAtual;
	
	public JogoEstado(List<Jogador> jogadoresList, Baralho baralho) {
		this.jogadoresList = jogadoresList;
		this.baralho = baralho;
	}

	public void getJogadorAtual() {
		
	}
	
	public void avancarTurno() {
		
	}
	
	public void removerJogadorDerrotado() {
		
	}
	
}
