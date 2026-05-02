package coup.personagens;

public class Inquisidor extends Personagem {
	
	
	 public Inquisidor(PersonagensNomes nome) {
		super(PersonagensNomes.INQUISIDOR);
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }
}
