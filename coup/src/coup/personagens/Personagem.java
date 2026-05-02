package coup.personagens;

public abstract class Personagem {
	
	PersonagensNomes nome;
	
	public Personagem(PersonagensNomes nome) {
		this.nome = nome;
	}

	public abstract void acao();

	public PersonagensNomes getNome() {
		return nome;
	}

	public void setNome(PersonagensNomes nome) {
		this.nome = nome;
	}

}
