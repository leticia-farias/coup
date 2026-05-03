package coup.jogador;

import java.util.ArrayList;
import java.util.List;

import coup.domain.model.Carta;

public class JogadorCartas {

	List<Carta> cartas = new ArrayList<>();
	boolean statusCompleto = true;
	
	public List<Carta> getCartas() {
		return cartas;
	}
	
	public void setCartas(List<Carta> cartas) {
		this.cartas = cartas;
	}
	
	public boolean isStatusCompleto() {
		return statusCompleto;
	}
	
	public void setStatusCompleto(boolean statusCompleto) {
		this.statusCompleto = statusCompleto;
	}
	
}
