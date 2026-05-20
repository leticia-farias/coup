

import java.util.ArrayList;
import java.util.List;

import coup.model.Assassino;
import coup.model.Baralho;
import coup.model.Capitao;
import coup.model.Carta;
import coup.model.Condessa;
import coup.model.Duque;
import coup.model.Embaixador;
import coup.model.Inquisidor;
import coup.model.Jogador;
import coup.model.Personagem;
import coup.ui.MenuGrafico;
import coup.ui.TelaJogo;
import coup.view.Console;

public class App {

	public static void main(String[] args) {

		Console console = new Console();
        
		MenuGrafico menuGrafico = new MenuGrafico();

		TelaJogo tela = new TelaJogo();
        tela.adicionarLog("Jogo iniciado!");

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
		if (console.pedirVersaoJogo() == 1) {
			personagensLista.add(new Embaixador());
		} else {
			personagensLista.add(new Inquisidor());
		}

		List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, personagensLista);

		// embaralha cartas
		baralhoCartas = baralho.embaralharCartas(baralhoCartas);

		// distribui duas cartas para cada jogador
		baralho.distribuirCartas(jogadoresList, baralhoCartas);

		//não mostrar as cartas para todos jogadores
       // menuGrafico.mostrarCartasJogadores(jogadoresList);
	  tela.adicionarLog("Cartas distribuídas.");

		/* verificao */
       while (jogadoresList.size() > 1) {

            for (Jogador jogador : jogadoresList) {

                if (!jogador.isStatusAtivo()) {
                    continue;
                }
                menuGrafico.mostrarCartasDoJogador(jogador);
                
                Personagem supostoPersonagem = null;
                boolean acaoValida = true;
                boolean acaoBloqueada = false;

                Acao acao = null;
                Jogador alvo = null;

                int opcao = menuGrafico.perguntarAcao(jogador);

                switch (opcao) {

                    case 1:
                        acao = new Ducar();
                        console.informarDuque(jogador);
                        tela.adicionarLog(jogador.getNome() + " afirma ter Duque.");
                        supostoPersonagem = new Duque();
                        break;

                    case 2:
                        acao = new Capitar();
                        alvo = menuGrafico.perguntarAlvo(jogador, jogadoresList);

                        if (alvo != null) {
                            console.informarCapitao(jogador);
                            tela.adicionarLog(jogador.getNome() + " tenta roubar " + alvo.getNome() + ".");
                            supostoPersonagem = new Capitao();
                        } else {
                            acaoValida = false;
                        }
                        break;

                    case 3:
                        menuGrafico.mostrarMensagem("Trocar (Embaixador) ainda não implementado.");
                        acaoValida = false;
                        break;

                    case 4:
                        acao = new Ajudar();
                        tela.adicionarLog(jogador.getNome() + " pediu ajuda externa.");
                        break;

                    case 5:
                        acao = new Receitar();
                        tela.adicionarLog(jogador.getNome() + " pediu renda.");
                        break;

                    case 7:
                        acao = new Golpear();
                        alvo = menuGrafico.perguntarAlvo(jogador, jogadoresList);

                        if (alvo == null) {
                            acaoValida = false;
                        }
                        break;

                    default:
                        menuGrafico.mostrarMensagem("Ação ainda não implementada.");
                        acaoValida = false;
                        break;
                }

                if (acaoValida && acao != null && (acao.podeSerContestada() || acao.podeSerbloqueado())) {

                    switch (menuGrafico.perguntarRespostaAcao(
                            jogador,
                            supostoPersonagem,
                            jogadoresList,
                            acao.podeSerContestada(),
                            acao.podeSerbloqueado()
                    )) {

                        case 1:
                            Jogador contestador = console.selecionarContestador(
                                    jogadoresList,
                                    jogador,
                                    supostoPersonagem
                            );

                            console.confirmarContestador(
                                    contestador,
                                    jogador,
                                    supostoPersonagem
                            );
                            break;

                        case 2:
                            tela.adicionarLog("Ninguém contestou/bloqueou.");
                            break;

                        case 3:
                            Jogador bloqueador = console.selecionarBloqueador(jogadoresList, jogador);

                            if (bloqueador != null) {
                                int respostaBloqueio = console.perguntarContestacaoBloqueio(bloqueador);

                                if (respostaBloqueio == 1) {
                                    tela.adicionarLog("Contestação de bloqueio ainda será implementada.");
                                    acaoBloqueada = true;
                                } else {
                                    tela.adicionarLog("Bloqueio aceito. Ação bloqueada.");
                                    acaoBloqueada = true;
                                }
                            }
                            break;

                        default:
                            menuGrafico.mostrarMensagem("Resposta inválida.");
                            break;
                    }
                }

                if (acaoValida && acao != null && !acaoBloqueada) {
                    acao.executar(jogador, alvo);
                }

                console.mostrarSaldos(jogadoresList);
           }
        }
    }
}