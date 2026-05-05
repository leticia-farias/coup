package coup;

import java.util.ArrayList;
import java.util.List;

import coup.jogador.Jogador;
import coup.personagens.Assassino;
import coup.personagens.Capitao;
import coup.personagens.Condessa;
import coup.personagens.Duque;
import coup.personagens.Embaixador;
import coup.personagens.Inquisidor;
import coup.personagens.Personagem;

public class App {

	public static void main(String[] args) {

		Console console = new Console();

		// verifica quant de jogadores
		int quantJogadores = console.pedirQuantidadeJogadores();

		// adiciona jogadores
		List<Jogador> jogadoresList = new ArrayList<Jogador>();
		for (int i = 0; i < quantJogadores; i++) {
			String nome = console.pedirNomeJogador(i);
			jogadoresList.add(new Jogador(i, nome));
		}

		// separa quant de cartas de cada personagem com base na quant de jogadores
		Baralho baralho = new Baralho();

		List<Personagem> personagensLista = new ArrayList<>();
		personagensLista.add(new Assassino());
		personagensLista.add(new Capitao());
		personagensLista.add(new Condessa());
		personagensLista.add(new Duque());

		// pergunta qual a versão do jogo
		if (console.pedirVersãoJogo() == 1) {
			personagensLista.add(new Embaixador());
		} else {
			personagensLista.add(new Inquisidor());
		}

		List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, personagensLista);

		// embaralha cartas
		baralhoCartas = baralho.embaralharCartas(baralhoCartas);

		// distribui duas cartas para cada jogador
		baralho.distribuirCartas(jogadoresList, baralhoCartas);

		/* verificao */
		for (Jogador jogador : jogadoresList) {
			System.out.println("jogador: " + jogador.getNome());
			System.out.println("cartas: ");
			for (Carta carta : jogador.getJogadorCartas().getCartas()) {
				System.out.println(carta.getPersonagem().getNome());
			}
		}

		while (jogadoresList.size() > 1) {
			Personagem supostoPersonagem = null;
			for (Jogador jogador : jogadoresList) {

				switch (console.perguntarAcao(jogador, jogadoresList)) {
					case 1:
						console.informarDuque(jogador);
						supostoPersonagem = new Duque();
						break;

					default:
						break;
				}

				switch (console.perguntarRespostaAcao(jogador, supostoPersonagem, jogadoresList, true)) {
					case 1:
						console.selecionarContestador(jogadoresList, jogador, supostoPersonagem);
						// contestar(jogadorDuvidando, jogador, supostoPersonagem);
						break;

					case 2:
						System.out.println("Ninguém contestou, jogo segue");
						break;

					case 3:
						System.out.println("Ninguém contestou, jogo segue");
						break;

					default:
						break;
				}
			}

		}
	}

}
