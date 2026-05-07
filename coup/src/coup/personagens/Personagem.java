package coup.personagens;

public abstract class Personagem {
	
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
