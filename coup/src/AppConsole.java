import coup.controller.JogoController;
import coup.view.Console;
import coup.view.IJogoView;

public class AppConsole {

	public static void main(String[] args) {
		
		// iniciar servidor e cliente
		
		// instanciar views
		IJogoView view = new Console();
		
		// instanciar controller
		JogoController controller = new JogoController(view);

		// dar start

		
    }
}