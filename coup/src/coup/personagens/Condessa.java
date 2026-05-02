package coup.personagens;

public class Condessa extends Personagem {
	
	public Condessa() {
		super(PersonagensNomes.CONDESSA);
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }

}
