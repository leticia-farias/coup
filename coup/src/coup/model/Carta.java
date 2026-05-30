package coup.model;

import java.io.Serializable;

public class Carta implements Serializable {
    private static final long serialVersionUID = 1L;
	
	// int id;
	Personagem personagem;
	boolean statusAtiva = true;

	public Carta(Personagem personagem) {
		this.personagem = personagem;
	}
	
	/*
	public Carta(int id, Personagem personagem) {
		this.id = id;
		this.personagem = personagem;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	 */
	
	public Personagem getPersonagem() {
		return personagem;
	}

	public void setPersonagem(Personagem personagem) {
		this.personagem = personagem;
	}
	
	public boolean isStatusAtiva() {
		return statusAtiva;
	}
	
	public void setStatusAtiva(boolean statusAtiva) {
		this.statusAtiva = statusAtiva;
	}

}
