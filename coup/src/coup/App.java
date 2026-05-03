package coup;

import coup.console.Console;
import coup.domain.JogoEstado;
import coup.domain.JogoFactory;
import coup.engine.JogoController;

public class App {

	public static void main(String[] args) {
		Console console = new Console();
		
		int quantidadeJogadores = console.pedirQuantidadeJogadores();
		JogoEstado estado = JogoFactory.criarNovoJogo(quantidadeJogadores, console); 

		JogoController controller = new JogoController(estado, console);

		controller.iniciarPartida();
		
	}

}
