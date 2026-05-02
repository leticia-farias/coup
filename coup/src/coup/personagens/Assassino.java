package coup.personagens;

public class Assassino extends Personagem {
	
	
	 public Assassino() {
		super(PersonagensNomes.ASSASSINO);
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }
}