package coup.view;

import java.util.List;

import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Personagem;

public interface IJogoView {
	
	public int pedirQuantidadeJogadores();
	public String pedirNomeJogador(int index);
	public int pedirVersaoJogo();
	public int perguntarAcao(Jogador jogador);
	
	public int perguntarRespostaAcao(
            Jogador jogadorAutor,
            Personagem supostoPersonagem,
            List<Jogador> jogadoresLista,
            boolean acaoPodeSerContestada,
            boolean acaoPodeSerBloqueada
    );
	
	public Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista);
	
	public void mostrarCartas();
	public void pedirAcao();
	public void mostrarLog(String mensagem);

	public int perguntarModo();
	public int perguntarOpcaoHerença();
	public Carta pedirDescarteEmbaixador(Jogador jogador);
	public Carta pedirCartaParaDescarte(Jogador jogador);
	
	public void mostrarSaldos(List<Jogador> jogadoresList);
	
	// novos metodos do inquisidor
	public int pedirHabilidadeInquisidor(Jogador jogador);
	public Carta pedirCartaParaMostrar(Jogador jogador);
	public boolean decidirTrocaInquisidor(Jogador inquisidor, Carta cartaMostrada);
}