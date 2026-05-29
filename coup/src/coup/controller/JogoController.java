package coup.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import coup.acoes.Acao;
import coup.estadoJogo.AguardandoAcao;
import coup.estadoJogo.AguardandoDecisaoInquisidor;
import coup.estadoJogo.AguardandoDescarte;
import coup.estadoJogo.AguardandoRespostaAcao;
import coup.estadoJogo.AguardandoTrocaEmbaixador;
import coup.estadoJogo.AguardandoTrocaInquisidor;
import coup.estadoJogo.ContextoJogo;
import coup.factory.FactoryVersaoInquisidor;
import coup.factory.FactoryVersaoOriginal;
import coup.factory.IJogoFactory;
import coup.model.Baralho;
import coup.model.Carta;
import coup.model.Jogador;
import coup.view.IJogoView;

public class JogoController {

	private IJogoView view;
	private ContextoJogo contexto;
	private IJogoFactory versaoJogo;

	// private Partida partidaAtual;

	private List<Jogador> jogadoresAtivosLista = new ArrayList<Jogador>();
	private Baralho baralho = new Baralho();

	private int indexJogadorAtual = -1;

	public JogoController(IJogoView view) {
		this.view = view;
	}

	// logica de loop dos turnos
	// verificacao de saldo
	// processamento de acoes

	public void prepararJogo() {
		// verifica quant de jogadores
		int quantJogadores = view.pedirQuantidadeJogadores();

//		if (quantJogadores < 2 || quantJogadores > 6) {
//            throw new IllegalArgumentException("Quantidade de jogadores inválida.");
//        }

		// adiciona jogadores
		List<Jogador> jogadoresLista = new ArrayList<Jogador>();
		for (int i = 0; i < quantJogadores; i++) {
			String nome = view.pedirNomeJogador(i);
			jogadoresLista.add(new Jogador(i, nome));
		}
		jogadoresAtivosLista = jogadoresLista;

		// pergunta modo de jogo
		if (view.perguntarModo() == 1) {
			versaoJogo = new FactoryVersaoOriginal();
		} else {
			versaoJogo = new FactoryVersaoInquisidor();
		}

		// pergunta opção herança (precisa trocar p/ boolean
		// int ativarHeranca = view.perguntarOpcaoHerença();
		// this.partida = new Partida.PartidaBuilder(modoJogo);

		List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, versaoJogo);

		// distribui duas cartas para cada jogador
		baralho.distribuirCartas(jogadoresLista, baralhoCartas);

		iniciarProximoTurno();
	}

	public void iniciarPartida() {
		while (contarJogadoresVivos() > 1) {
			iniciarProximoTurno();
		}

		Jogador vencedor = jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).findFirst().orElse(null);
		view.mostrarLog("FIM DE JOGO! Vencedor: " + vencedor.getNome());
		// TODO perguntar se quer jogar novamente

	}

	private int contarJogadoresVivos() {
		return (int) jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).count();
	}

	private void iniciarProximoTurno() {
		// Soma 1 para ir para o próximo, e o % garante que volta ao zero quando chega
		// ao final da lista
		do {
			indexJogadorAtual = (indexJogadorAtual + 1) % jogadoresAtivosLista.size();
		} while (!jogadoresAtivosLista.get(indexJogadorAtual).isStatusAtivo());

		Jogador jogadorAtual = jogadoresAtivosLista.get(indexJogadorAtual);

		processarTurno(jogadorAtual);
	}

	private void processarTurno(Jogador jogadorAtual) {
		// Inicializa o contexto
		if (this.contexto == null) {
			this.contexto = new ContextoJogo(null, jogadorAtual, baralho);
			this.contexto.setTotalJogadoresAtivos(contarJogadoresVivos());
		} else {
			this.contexto.setJogadorAutor(jogadorAtual);
			this.contexto.setTotalJogadoresAtivos(contarJogadoresVivos());
			this.contexto.setAceites(0); // ← resetar aceites a cada turno!
		}

		view.mostrarSaldos(jogadoresAtivosLista);
		exibirCartasPublicas();

		if (view instanceof servidor.JogoViewRemota) {
			((servidor.JogoViewRemota) view).enviarCartasParaJogador(jogadorAtual);
		}

		// Regra: 10+ moedas obriga golpe de estado, sem perguntar
		// Usa menu com opção 8 se for versão inquisidor
		Acao acao;
		if (jogadorAtual.getSaldo() >= 10) {
			view.mostrarLog(">>> " + jogadorAtual.getNome() + " tem 10+ moedas e é obrigado a dar golpe!");
			acao = versaoJogo.acoes(7, baralho);
		} else {
			int respostaAcao = (versaoJogo instanceof FactoryVersaoInquisidor)
					? view.perguntarAcaoComInquisidor(jogadorAtual)
					: view.perguntarAcao(jogadorAtual);
			acao = versaoJogo.acoes(respostaAcao, baralho);
		}

		// AVISA TODOS OS CLIENTES SOBRE A AÇÃO ESCOLHIDA
		view.mostrarLog("\n>>> O jogador " + jogadorAtual.getNome() + " iniciou a ação: "
				+ acao.getClass().getSimpleName().toUpperCase());

		contexto.setAcaoPendente(acao);

		if (acao.requerAlvo()) {
			Jogador alvo = view.perguntarAlvo(jogadorAtual, jogadoresAtivosLista);
			contexto.setJogadorAlvo(alvo);
		}

		// 2. Processa respostas (Bloqueios/Contestações)
		if (acao.podeSerContestada() || acao.podeSerbloqueado()) {
			contexto.setEstado(new AguardandoRespostaAcao(contexto));

			List<CompletableFuture<Void>> futuros = new ArrayList<>();

			for (Jogador outro : jogadoresAtivosLista) {
				if (outro.equals(jogadorAtual) || !outro.isStatusAtivo())
					continue;

				CompletableFuture<Void> futuro = CompletableFuture.runAsync(() -> {
					int resposta = view.perguntarRespostaAcao(outro, null, jogadoresAtivosLista,
							acao.podeSerContestada(), acao.podeSerbloqueado());

					synchronized (contexto) {
						if (contexto.getEstado() instanceof AguardandoRespostaAcao) {
							if (resposta == 1) {
								view.mostrarLog(outro.getNome() + " CONTESTOU a ação!");
								contexto.getEstado().responderAcao(outro, resposta);

							} else if (resposta == 3) {
								// NOVO: perguntar com qual personagem está bloqueando
								List<coup.model.PersonagensNomes> personagensValidos = acao.getPersonagensBloquadores();
								coup.model.PersonagensNomes personagemEscolhido = view
										.perguntarPersonagemBloqueio(outro, personagensValidos);

								contexto.setPersonagemBloqueio(personagemEscolhido);
								view.mostrarLog(outro.getNome() + " BLOQUEOU com " + personagemEscolhido + "!");
								contexto.getEstado().responderAcao(outro, resposta);

							} else {
								view.mostrarLog(outro.getNome() + " aceitou.");
								contexto.getEstado().responderAcao(outro, resposta);
							}
						}
					}
				});
				futuros.add(futuro);
			}

			// Bloqueia o loop principal até que todos tenham respondido
			CompletableFuture.allOf(futuros.toArray(new CompletableFuture[0])).join();
			if (contexto.getEstado() instanceof coup.estadoJogo.ResolvendoContestacao) {
				((coup.estadoJogo.ResolvendoContestacao) contexto.getEstado()).resolverContestacao();
			}

			// NOVO: Resolve bloqueio pendente — pergunta ao autor da ação se quer contestar
			if (contexto.getEstado() instanceof coup.estadoJogo.AguardandoRespostaBloqueio) {
				coup.estadoJogo.AguardandoRespostaBloqueio estadoBloqueio = (coup.estadoJogo.AguardandoRespostaBloqueio) contexto
						.getEstado();

				view.mostrarLog("Ação de " + jogadorAtual.getNome() + " foi bloqueada. Deseja contestar o bloqueio?");

				// Apenas o autor da ação pode contestar o bloqueio (regra oficial do Coup)
				int respostaAoBloqueio = view.perguntarRespostaAcao(jogadorAtual, null, jogadoresAtivosLista, true, // pode
																													// contestar
						false // não pode bloquear um bloqueio
				);

				synchronized (contexto) {
					estadoBloqueio.responderAcao(jogadorAtual, respostaAoBloqueio);
				}

				// Se contestou o bloqueio, resolve agora
				if (contexto.getEstado() instanceof coup.estadoJogo.ResolvendoContestacao) {
					((coup.estadoJogo.ResolvendoContestacao) contexto.getEstado()).resolverContestacao();
				}
			}

			// 3. Resolve os desfechos pendentes na View...
		} else {
			// Ações inquestionáveis (Renda, Golpe de Estado)
			acao.executar(jogadorAtual, contexto.getJogadorAlvo());

			if (acao.getClass().getSimpleName().equals("Golpear")) {
				contexto.setEstado(new AguardandoDescarte(contexto, contexto.getJogadorAlvo(), false));
			} else {
				contexto.setEstado(new AguardandoAcao(contexto));
			}
		}

		// 3. Resolve os desfechos pendentes na View (Ex: Descarte)
		if (contexto.getEstado() instanceof AguardandoDescarte) {
			AguardandoDescarte estadoDescarte = (AguardandoDescarte) contexto.getEstado();
			Jogador perdedor = estadoDescarte.getJogadorQueDescarta();

			if (perdedor != null && perdedor.isStatusAtivo()) {
				view.mostrarLog(perdedor.getNome() + " deve perder uma carta!");
				coup.model.Carta cartaMorta = view.pedirCartaParaDescarte(perdedor);

				estadoDescarte.descartarCarta(cartaMorta);
				view.mostrarLog(perdedor.getNome() + " perdeu a carta: " + cartaMorta.getPersonagem().getNome());

				if (!perdedor.isStatusAtivo()) {
					view.mostrarLog("!!! " + perdedor.getNome() + " foi ELIMINADO!");
				}
			}
		}

		// Troca do Embaixador: jogador deve devolver 2 cartas ao baralho
		if (contexto.getEstado() instanceof coup.estadoJogo.AguardandoTrocaEmbaixador) {
			coup.estadoJogo.AguardandoTrocaEmbaixador estadoTroca = (coup.estadoJogo.AguardandoTrocaEmbaixador) contexto
					.getEstado();

			view.mostrarLog(jogadorAtual.getNome() + " deve devolver 2 cartas ao baralho.");

			// Pede a devolução 2 vezes — cada chamada remove uma carta da mão
			for (int i = 0; i < 2; i++) {
				Carta cartaDevolvida = view.pedirDescarteEmbaixador(jogadorAtual);

				if (cartaDevolvida != null) {
					estadoTroca.descartarCarta(cartaDevolvida);
					view.mostrarLog(jogadorAtual.getNome() + " devolveu uma carta ao baralho.");
				}
			}
		}
		
		// Troca do Inquisidor: devolve 1 carta
		if (contexto.getEstado() instanceof AguardandoTrocaInquisidor) {
		    AguardandoTrocaInquisidor estadoTroca =
		            (AguardandoTrocaInquisidor) contexto.getEstado();

		    view.mostrarLog(jogadorAtual.getNome() + " deve devolver 1 carta ao baralho.");
		    Carta cartaDevolvida = view.pedirDescarteEmbaixador(jogadorAtual); // mesma semântica
		    if (cartaDevolvida != null) {
		        estadoTroca.descartarCarta(cartaDevolvida);
		        view.mostrarLog(jogadorAtual.getNome() + " devolveu uma carta ao baralho.");
		    }
		}

		// Exame do Inquisidor: alvo mostra carta → inquisidor decide
		if (contexto.getEstado() instanceof AguardandoDecisaoInquisidor) {
		    AguardandoDecisaoInquisidor estadoExame =
		            (AguardandoDecisaoInquisidor) contexto.getEstado();

		    Jogador alvo       = estadoExame.getAlvo();
		    Jogador inquisidor = estadoExame.getInquisidor();

		    // 1. Alvo escolhe qual carta revelar
		    Carta cartaRevelada = view.pedirCartaParaRevelar(alvo);

		    // 2. Inquisidor vê a carta (mensagem privada)
		    view.mostrarCartaPrivada(inquisidor, cartaRevelada);

		    // 3. Inquisidor decide se força a troca
		    boolean forca = view.perguntarForcaExame(inquisidor);

		    if (forca) {
		        view.mostrarLog(inquisidor.getNome() + " forçou a troca da carta de " + alvo.getNome() + ".");
		    } else {
		        view.mostrarLog(inquisidor.getNome() + " decidiu não forçar a troca.");
		    }

		    estadoExame.resolverExame(cartaRevelada, forca, baralho);
		}

		// Avisa todos do resultado final do turno
		view.mostrarLog("<<< Turno de " + jogadorAtual.getNome() + " encerrado. Saldo atual: " + jogadorAtual.getSaldo()
				+ " moedas.");

		view.mostrarLog("\n>>> O jogador " + jogadorAtual.getNome() + " realizou a ação: "
				+ acao.getClass().getSimpleName().toUpperCase());
	}

	public void pedirAcao(Jogador jogador) {

		view.perguntarAcao(jogador);
	}

	private void exibirCartasPublicas() {
		StringBuilder sb = new StringBuilder();
		sb.append("CARTAS EM JOGO:\n");
		for (Jogador j : jogadoresAtivosLista) {
			sb.append(j.getNome()).append(": ");
			long mortas = j.getJogadorCartas().getCartas().stream().filter(c -> !c.isStatusAtiva()).count();
			long vivas = j.getJogadorCartas().getCartas().stream().filter(coup.model.Carta::isStatusAtiva).count();
			sb.append(vivas).append(" carta(s) viva(s)");
			if (mortas > 0) {
				sb.append(", mortas: ");
				j.getJogadorCartas().getCartas().stream().filter(c -> !c.isStatusAtiva())
						.forEach(c -> sb.append(c.getPersonagem().getNome()).append(" "));
			}
			if (!j.isStatusAtivo())
				sb.append(" [ELIMINADO]");
			sb.append("\n");
		}
		view.mostrarLog(sb.toString());
	}
}
