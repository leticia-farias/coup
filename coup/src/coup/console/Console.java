package coup.console;

public class Console {
	Input input = new Input();
	
	public int pedirQuantidadeJogadores() {
		separarMensagem();
		System.out.println("Digite número de jogadores:");		
		int quant = input.receberInt();
		return quant;
	}
	
	public String pedirNomeJogador(int index) {
		separarMensagem();
		System.out.println("Digite o nome do " + (index + 1) + "º jogador:");
		String nome = input.receberString();
		return nome;
	}
	
	public void separarMensagem() {
		System.out.println("---------------------");		
	}
	
	// pedir acaoAoJogador devolve id da acao escolhida
	
	// pedir alvo
	
	// perguntar se contesta
	
	// pedir carta para perder

}
