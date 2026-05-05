package coup;

import java.util.List;
import java.util.Scanner;

import coup.jogador.Jogador;
import coup.personagens.Personagem;

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

	public int perguntarRespostaAcao(Jogador jogadorAutor, Personagem supostoPersonagem, List<Jogador> jogadoresLista, boolean acaoPodeSerBloqueada) {
		System.out.println("Alguém deseja contestar?");
		System.out.println("1 - Contestar");
		System.out.println("2 - Aceitar");

		if (acaoPodeSerBloqueada) { 
			System.out.println("3 - Bloquear"); 
		}

		return sc.nextInt();
	}

	public void mostrarMenuJogadores(List<Jogador> jogadoresList, Jogador jogadorContestado,
			boolean menuContestatores) {
		if (menuContestatores) {
			for (Jogador contestador : jogadoresList) {
				if (!contestador.equals(jogadorContestado)) {
					System.out.println(contestador.getId() + " - " + contestador.getNome());
				}
			}
		} else {
			for (Jogador j : jogadoresList) {
				System.out.println(j.getId() + " - " + j.getNome());
			}
		}

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

		public Jogador selecionarContestador(List<Jogador> jogadoresList, Jogador jogadorContestado, Personagem supostoPersonagem) {
		
		System.out.println("Quem está constestando " + jogadorContestado.getNome() + "?");
		mostrarMenuJogadores(jogadoresList, jogadorContestado, true);
		
		System.out.println("Digite o número:");
		int idContestador = sc.nextInt(); 
		
		Jogador contestador = new Jogador();
		for (Jogador c : jogadoresList) {
			if (c.getId() == idContestador) {
				contestador = c;
			}
		}
		return jogadorContestado;
	}

	public void confirmarContestador() {
		System.out.println("Confirmar que " + contestador.getNome() + " está contestando " + jogadorContestado.getNome() + ":");
		System.out.println("1 - SIM");
		System.out.println("2 - NÃO");
		resposta = sc.nextInt();
		
		if (resposta == 1) {
			contestacao.contestar(contestador, jogadorContestado, supostoPersonagem);
		}
	}
	
	public void mostrarCartas(Jogador jogador) {
		System.out.println(jogador.getNome() + ", você tem as seguintes cartas: ");
		
		for (int i = 0; i < jogador.getJogadorCartas().getCartas().size(); i++) {
			System.out.println(i + " - " + jogador.getJogadorCartas().getCartas().get(i).personagem.getNome());			
		}
		
		/* com o ID da carta
		for (Carta c : cartasEntity.cartas) {
			System.out.println(c.getId() + " - " + c.getPersonagem().nome);			
		}
		*/
	}
	
	public void informarGolpe(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
		separarMensagens();
		System.out.println(jogadorAtual.getNome() + "  é obrigado a fazer golpe de estado");
		perguntarAlvo(null, null);
		System.out.println(jogadorAtual.getNome() + " digite o número do jogador para sofrer o golpe:");
		
	}

	public void informarDuque(Jogador jogador) {
		System.out.println(jogador.getNome() + " afirma ter o Duque e recebe 3 moedas de imposto");
	}
	
	public void informarCapitao(Jogador jogador) {
		System.out.println(jogador.getNome() + " afirma ter o capitão e recebe 3 moedas de imposto");
	}

	public void separarMensagens() {
		System.out.println("---------------------");
	}
}
