package coup.view;

import java.util.List;

import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Personagem;
import coup.model.PersonagensNomes;

public interface IJogoView {
	
	int pedirQuantidadeJogadores();
	String pedirNomeJogador(int index);
	int pedirVersaoJogo();
	int perguntarAcao(Jogador jogador);
	
	int perguntarRespostaAcao(
            Jogador jogadorAutor,
            Personagem supostoPersonagem,
            List<Jogador> jogadoresLista,
            boolean acaoPodeSerContestada,
            boolean acaoPodeSerBloqueada
    );
	
	Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista);
	
	void mostrarCartas();
	void pedirAcao();
	void mostrarLog(String mensagem);

	int perguntarModo();
	int perguntarOpcaoHerença();
	Carta pedirDescarteEmbaixador(Jogador jogador);
	Carta pedirCartaParaDescarte(Jogador jogador);
	
	void mostrarSaldos(List<Jogador> jogadoresList);
	
	PersonagensNomes perguntarPersonagemBloqueio(Jogador bloqueador, List<PersonagensNomes> personagensValidos);
}
