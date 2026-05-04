package coup.personagens;

public class Inquisidor extends Personagem {
	
	
	 public Inquisidor(PersonagensNomes nome) {
		super(PersonagensNomes.INQUISIDOR);
	}

	public Inquisidor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void acao() {
		 System.out.println("pegar 3 moedas");
	 }
}
