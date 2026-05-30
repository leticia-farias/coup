package coup.model;

import java.io.Serializable;

public abstract class Personagem implements Serializable {
    private static final long serialVersionUID = 1L;
	
	PersonagensNomes nome;
	
	public Personagem(PersonagensNomes nome) {
		this.nome = nome;
	}
	
	public PersonagensNomes getNome() {
		return nome;
	}

	public void setNome(PersonagensNomes nome) {
		this.nome = nome;
	}

}
