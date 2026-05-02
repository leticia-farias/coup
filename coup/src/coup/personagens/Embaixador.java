package coup.personagens;

public class Embaixador extends Personagem {
	
	
	 public Embaixador() {
		super(PersonagensNomes.EMBAIXADOR);
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }

}
