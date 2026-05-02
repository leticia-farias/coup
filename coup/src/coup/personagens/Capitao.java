package coup.personagens;

public class Capitao extends Personagem {
	
	
	 public Capitao() {
		super(PersonagensNomes.CAPITAO);
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }

}
