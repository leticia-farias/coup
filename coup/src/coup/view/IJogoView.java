package coup.view;

import java.util.List;

import coup.model.Jogador;

public interface IJogoView {
	
	public int pedirQuantidadeJogadores();
	public String pedirNomeJogador(int index);
	public int pedirVersaoJogo();
	public int perguntarAcao(Jogador jogador, List<Jogador> jogadoresLista);
	public Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista);
	
	public void mostrarCartas();
	public void pedirAcao();
	public void mostrarLog();

}
