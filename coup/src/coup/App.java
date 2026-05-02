package coup;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import coup.jogador.Jogador;
import coup.personagens.Assassino;
import coup.personagens.Capitao;
import coup.personagens.Condessa;
import coup.personagens.Duque;
import coup.personagens.Embaixador;
import coup.personagens.Personagem;

public class App {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		// verifica quant de jogadores
		int quantJogadores;
		System.out.println("Digite número de jogadores:");
		quantJogadores = sc.nextInt();

		// adiciona jogadores
		List<Jogador> jogadoresList = new ArrayList<Jogador>();
		String nome;
		for (int i = 0; i < quantJogadores; i++) {
			System.out.println("Digite o nome do " + (i + 1) + "º jogador:");
			nome = sc.next();
			jogadoresList.add(new Jogador(i, nome));
		}

		// separa quant de cartas de cada personagem com base na quant de jogadores
		Baralho baralho = new Baralho();
		
		List<Personagem> personagensLista = new ArrayList<>();
		// fazer jogo variado com inquisidor
		personagensLista.add(new Assassino());
		personagensLista.add(new Capitao());
		personagensLista.add(new Condessa());
		personagensLista.add(new Duque());
		personagensLista.add(new Embaixador());
		List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, personagensLista);

		// embaralha cartas
		baralhoCartas = baralho.embaralharCartas(baralhoCartas);

		// distribui duas cartas para cada jogador
		baralho.distribuirCartas(jogadoresList, baralhoCartas);

		/* verificao */
		for (Jogador jogador : jogadoresList) {
			System.out.println("---------------------");
			System.out.println("jogador: " + jogador.getNome());
			System.out.println("cartas: ");
			for (Carta carta : jogador.getJogadorCartas().getCartas()) {
				System.out.println(carta.getPersonagem().getNome());
			}
		}

		// 1º jogador escolhe a ação:
		MenuOpcoes menuOpcoes = new MenuOpcoes();
		while (jogadoresList.size() > 1) {
			for (Jogador jogador : jogadoresList) {
				menuOpcoes.mostrarMenuAcoes(jogador, personagensLista, jogadoresList, sc);
			}
		}

		sc.close();
	}

}
