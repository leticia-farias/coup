package coup;

import java.util.List;
import java.util.Scanner;

import coup.jogador.Jogador;

public class Console {

	Scanner sc = new Scanner(System.in);
	
	public int pedirQuantidadeJogadores() {
		separarMensagens();
		System.out.println("Digite número de jogadores:");
		return sc.nextInt();
	}
	
	public String pedirNomeJogador(int index) {
		separarMensagens();
		System.out.println("Digite o nome do " + (index + 1) + "º jogador:");
		return sc.next();
	}
	
	public int pedirVersãoJogo() {
		separarMensagens();
		System.out.println("Digite o número da versão do jogo");
		System.out.println("1 - Com embaixador");
		System.out.println("2 - Com inquisidor");
		return sc.nextInt();
	}
	
	public int perguntarAcao(Jogador jogador, List<Jogador> jogadoresLista) {
		separarMensagens();
		int saldoJogador = jogador.getSaldo();

		if (saldoJogador > 10) {
			informarGolpe(jogador, jogadoresLista);
		} else {
			System.out.println("1 - Imposto (Duque)");
			System.out.println("2 - Roubar (Capitão)");
			System.out.println("3 - Trocar (Embaixador)");
			System.out.println("3 - Ajuda externa");
			System.out.println("4 - Pedir renda");

			if (saldoJogador > 3) {
				System.out.println("5 - Assasinar (Assassino)");
			}
			if (saldoJogador > 7) {
				System.out.println("6 - Golpe de estado");
			}
		}

		System.out.println(jogador.getNome() + " digite o número da ação:");

		return sc.nextInt();
	}
	
	public int perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
		separarMensagens();
		int saldoJogador = jogadorAtual.getSaldo();
		
		if (saldoJogador > 10) {
			informarGolpe(jogadorAtual, jogadoresLista);
		} else {
			System.out.println("1 - Imposto (Duque)");
			System.out.println("2 - Roubar (Capitão)");
			System.out.println("3 - Trocar (Embaixador)");
			System.out.println("3 - Ajuda externa");
			System.out.println("4 - Pedir renda");
			
			if (saldoJogador > 3) {
				System.out.println("5 - Assasinar (Assassino)");
			}
			if (saldoJogador > 7) {
				System.out.println("6 - Golpe de estado");
			}
		}
		
		System.out.println(jogadorAtual.getNome() + " digite o número da ação:");
		
		return sc.nextInt();
	}
	
	public void informarGolpe(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
		separarMensagens();
		System.out.println(jogadorAtual.getNome() + "  é obrigado a fazer golpe de estado");
		perguntarAlvo(null, null);
		System.out.println(jogadorAtual.getNome() + " digite o número do jogador para sofrer o golpe:");
		
	}

	public void separarMensagens() {
		System.out.println("---------------------");
	}
}
