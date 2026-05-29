package coup.view;

import java.util.List;
import java.util.Scanner;

import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Personagem;

public class Console implements IJogoView {

	Scanner sc = new Scanner(System.in);

	private int lerInteiro() {
		while (true) {
			try {
				return sc.nextInt();
			} catch (Exception e) {
				System.out.println("Entrada inválida. Digite um número:");
				sc.next();
			}
		}
	}

	private String lerTexto() {
		return sc.next();
	}

	@Override
	public int pedirQuantidadeJogadores() {
		int quantidade;

		do {
			separarMensagens();
			System.out.println("Digite número de jogadores (2 a 6):");
			quantidade = lerInteiro();

			if (quantidade < 2 || quantidade > 6) {
				System.out.println("Quantidade inválida. O jogo deve ter de 2 a 6 jogadores.");
			}

		} while (quantidade < 2 || quantidade > 6);

		return quantidade;
	}

	@Override
	public String pedirNomeJogador(int index) {
		separarMensagens();
		System.out.println("Digite o nome do " + (index + 1) + "º jogador:");
		return lerTexto();
	}

	@Override
	public int pedirVersaoJogo() {
		int versao;

		do {
			separarMensagens();
			System.out.println("Digite o número da versão do jogo");
			System.out.println("1 - Com embaixador");
			System.out.println("2 - Com inquisidor");

			versao = lerInteiro();

			if (versao != 1 && versao != 2) {
				System.out.println("Versão inválida. Escolha 1 ou 2.");
			}

		} while (versao != 1 && versao != 2);

		return versao;
	}

	@Override
	public int perguntarAcao(Jogador jogador) {
		separarMensagens();

		int saldoJogador = jogador.getSaldo();

		if (saldoJogador >= 10) {
			informarGolpe(jogador);
			return 7;
		}

		System.out.println("1 - Imposto (Duque)");
		System.out.println("2 - Roubar (Capitão)");
		System.out.println("3 - Trocar (Embaixador)");
		System.out.println("4 - Ajuda externa");
		System.out.println("5 - Pedir renda");

		if (saldoJogador >= 3) {
			System.out.println("6 - Assassinar (Assassino)");
		}

		if (saldoJogador >= 7) {
			System.out.println("7 - Golpe de estado");
		}

		System.out.println(jogador.getNome() + ", digite o número da ação:");
		return lerInteiro();
	}

	@Override
	public int perguntarRespostaAcao(Jogador jogadorAutor, Personagem supostoPersonagem, List<Jogador> jogadoresLista,
			boolean acaoPodeSerContestada, boolean acaoPodeSerBloqueada) {

		separarMensagens();

		System.out.println("Como deseja responder?");

		if (acaoPodeSerContestada) {
			System.out.println("1 - Contestar");
		}

		System.out.println("2 - Aceitar");

		if (acaoPodeSerBloqueada) {
			System.out.println("3 - Bloquear");
		}

		return lerInteiro();
	}

	public void mostrarMenuJogadores(List<Jogador> jogadoresList, Jogador jogadorIgnorado, boolean ignorarJogador) {
		for (Jogador jogador : jogadoresList) {
			if (!ignorarJogador || !jogador.equals(jogadorIgnorado)) {
				System.out.println("- " + jogador.getNome());
			}
		}
	}

	@Override
	public Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
		separarMensagens();
		System.out.println(jogadorAtual.getNome() + ", escolha quem será o alvo:");

		mostrarMenuJogadores(jogadoresLista, jogadorAtual, true);

		System.out.println("Digite o nome:");
		String nomeAlvo = lerTexto();

		for (Jogador jogador : jogadoresLista) {
			if (jogador.getNome().equalsIgnoreCase(nomeAlvo) && !jogador.equals(jogadorAtual)
					&& jogador.isStatusAtivo()) {
				return jogador;
			}
		}

		System.out.println("Alvo inválido.");
		return null;
	}

	public Jogador selecionarContestador(List<Jogador> jogadoresList, Jogador jogadorContestado,
			Personagem supostoPersonagem) {
		separarMensagens();
		System.out.println("Quem está contestando " + jogadorContestado.getNome() + "?");

		mostrarMenuJogadores(jogadoresList, jogadorContestado, true);

		System.out.println("Digite o nome:");
		String nomeContestador = lerTexto();

		for (Jogador jogador : jogadoresList) {
			if (jogador.getNome().equalsIgnoreCase(nomeContestador) && !jogador.equals(jogadorContestado)
					&& jogador.isStatusAtivo()) {
				return jogador;
			}
		}

		System.out.println("Jogador inválido.");
		return null;
	}

	public void confirmarContestador(Jogador contestador, Jogador jogadorContestado, Personagem supostoPersonagem) {
		if (contestador == null) {
			System.out.println("Nenhum contestador válido.");
			return;
		}

		System.out.println(
				"Confirmar que " + contestador.getNome() + " está contestando " + jogadorContestado.getNome() + "?");
		System.out.println("1 - SIM");
		System.out.println("2 - NÃO");

		int resposta = lerInteiro();

		if (resposta == 1) {
			// Contestacao contestacao = new Contestacao();
			// contestacao.contestar(contestador, jogadorContestado, supostoPersonagem);
		}
	}

	public void mostrarCartas(Jogador jogador) {
		System.out.println(jogador.getNome() + ", você tem as seguintes cartas:");

		for (int i = 0; i < jogador.getJogadorCartas().getCartas().size(); i++) {
			Carta carta = jogador.getJogadorCartas().getCartas().get(i);

			String status = carta.isStatusAtiva() ? "ativa" : "morta";

			System.out.println(i + " - " + carta.getPersonagem().getNome() + " (" + status + ")");
		}
	}

	public void mostrarSaldos(List<Jogador> jogadoresList) {
		separarMensagens();
		System.out.println("SALDO DOS JOGADORES:");

		for (Jogador jogador : jogadoresList) {
			System.out.println(jogador.getNome() + ": " + jogador.getSaldo() + " moedas");
		}
	}

	public void informarGolpe(Jogador jogadorAtual) {
		separarMensagens();
		System.out.println(jogadorAtual.getNome() + " é obrigado a fazer golpe de estado.");
	}

	public void informarDuque(Jogador jogador) {
		System.out.println(jogador.getNome() + " afirma ter o Duque e recebe 3 moedas de imposto.");
	}

	public void informarCapitao(Jogador jogador) {
		System.out.println(jogador.getNome() + " afirma ter o Capitão e tenta roubar 2 moedas.");
	}

	public void separarMensagens() {
		System.out.println("---------------------");
	}

	public Jogador selecionarBloqueador(List<Jogador> jogadoresList, Jogador jogadorAutor) {

		separarMensagens();
		System.out.println("Quem está bloqueando " + jogadorAutor.getNome() + "?");

		mostrarMenuJogadores(jogadoresList, jogadorAutor, true);

		System.out.println("Digite o nome:");
		String nomeBloqueador = lerTexto();

		for (Jogador jogador : jogadoresList) {

			if (jogador.getNome().equalsIgnoreCase(nomeBloqueador) && !jogador.equals(jogadorAutor)
					&& jogador.isStatusAtivo()) {

				return jogador;
			}
		}

		System.out.println("Bloqueador inválido.");
		return null;
	}

	public int perguntarContestacaoBloqueio(Jogador bloqueador) {

		separarMensagens();

		System.out.println(bloqueador.getNome() + " está bloqueando.");
		System.out.println("Alguém deseja contestar o bloqueio?");

		System.out.println("1 - Contestar bloqueio");
		System.out.println("2 - Aceitar bloqueio");

		return lerInteiro();
	}

	@Override
	public void mostrarCartas() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pedirAcao() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mostrarLog(String mensagem) {
		separarMensagens();
		System.out.println(mensagem);
	}

	@Override
	public int perguntarModo() {
		System.out.println("Qual será o modo de jogo?");
		System.out.println("1 - Original (com embaixador)");
		System.out.println("2 - Com inquisidor");

		return lerInteiro();
	}

	@Override
	public int perguntarOpcaoHerença() {
		System.out.println("Habilitar herança?");
		System.out.println("1 - SIM");
		System.out.println("2 - NAO");

		return lerInteiro();
	} // trocar para boolean

	@Override
	public Carta pedirCartaParaDescarte(Jogador jogador) {
		separarMensagens();
		System.out.println(jogador.getNome() + ", escolha uma carta para perder:");
		mostrarCartas(jogador);

		int index = lerInteiro();
		// Verifica se a carta é válida e retorna a carta.
		return jogador.getJogadorCartas().getCartas().get(index);
	}

	@Override
	public Carta pedirDescarteEmbaixador(Jogador jogador) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int pedirHabilidadeInquisidor(Jogador jogador) { 
		return 1; 
	}

	@Override
	public Carta pedirCartaParaMostrar(Jogador jogador) { 
		return null; 
	}

	@Override
	public boolean decidirTrocaInquisidor(Jogador inquisidor, Carta cartaMostrada) { 
		return false; 
	}
}