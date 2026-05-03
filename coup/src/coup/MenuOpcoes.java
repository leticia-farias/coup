package coup;

import java.util.List;
import java.util.Scanner;

import coup.acoes.AcoesPersonagens;
import coup.acoes.Bloqueios;
import coup.acoes.Contestacao;
import coup.acoes.Golpe;
import coup.acoes.OutrasAcoes;
import coup.domain.model.Jogador;
import coup.domain.model.Personagem;
import coup.personagens.Duque;

public class MenuOpcoes {
	int resposta;
	Personagem supostoPersonagem;
	
	AcoesPersonagens acoesPersonagens = new AcoesPersonagens();
	Bloqueios bloqueios= new Bloqueios();
	Contestacao contestacao = new Contestacao();
	Golpe golpe = new Golpe();
	OutrasAcoes outrasAcoes = new OutrasAcoes();

	public void mostrarMenuAcoes(Jogador jogador, List<Personagem> personagensLista, List<Jogador> jogadoresLista, Scanner sc) {
		int saldoJogador = jogador.getSaldo();

		System.out.println("---------------------");
		if (saldoJogador > 10) {
			System.out.println("Jogador é obrigado a fazer golpe de estado");
			System.out.println("Golpe de estado");
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
		resposta = sc.nextInt();

		switch (resposta) {
		case 1:
			System.out.println(jogador.getNome() + " afirma ter o Duque e recebe 3 moedas de imposto");
			supostoPersonagem = new Duque();
			mostrarMenuResposta(jogador, supostoPersonagem, jogadoresLista, sc);
			break;

		default:
			break;
		}
	}

	public void mostrarMenuResposta(Jogador jogador, Personagem supostoPersonagem, List<Jogador> jogadoresLista, Scanner sc) {
		System.out.println("Alguém deseja contestar?");
		System.out.println("1 - Contestar");
		System.out.println("2 - Aceitar");

		/*
		 * if (acaoPodeSerBloqueada) { System.out.println("Bloquear"); }
		 */

		resposta = sc.nextInt();

		switch (resposta) {
		case 1:
			selecionarContestador(jogadoresLista, jogador, supostoPersonagem, sc);
			// contestar(jogadorDuvidando, jogador, supostoPersonagem);
			break;
		case 2:
			System.out.println("Ninguém contestou, jogo segue");
			break;

		default:
			break;
		}
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

	public Jogador selecionarContestador(List<Jogador> jogadoresList, Jogador jogadorContestado, Personagem supostoPersonagem, Scanner sc) {
		
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
		
		System.out.println("Confirmar que " + contestador.getNome() + " está contestando " + jogadorContestado.getNome() + ":");
		System.out.println("1 - SIM");
		System.out.println("2 - NÃO");
		resposta = sc.nextInt();
		
		if (resposta == 1) {
			contestacao.contestar(contestador, jogadorContestado, supostoPersonagem);
		}
		
		return jogadorContestado;
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
}
