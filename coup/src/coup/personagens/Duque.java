package coup.personagens;

public class Duque extends Personagem {

	public Duque() {
		super(PersonagensNomes.DUQUE);
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }

}
