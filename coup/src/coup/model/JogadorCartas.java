package coup.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JogadorCartas implements Serializable {
    private static final long serialVersionUID = 1L;

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
