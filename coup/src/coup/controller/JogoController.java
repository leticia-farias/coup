package coup.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import coup.acoes.Acao;
import coup.acoes.Golpear;
import coup.estadoJogo.AguardandoAcao;
import coup.estadoJogo.AguardandoDecisaoInquisidor;
import coup.estadoJogo.AguardandoDescarte;
import coup.estadoJogo.AguardandoRespostaAcao;
import coup.estadoJogo.AguardandoRespostaBloqueio;
import coup.estadoJogo.AguardandoTrocaEmbaixador;
import coup.estadoJogo.AguardandoTrocaInquisidor;
import coup.estadoJogo.ContextoJogo;
import coup.estadoJogo.IEstadoJogo;
import coup.estadoJogo.ResolvendoContestacao;
import coup.factory.FactoryVersaoInquisidor;
import coup.factory.FactoryVersaoOriginal;
import coup.factory.IJogoFactory;
import coup.model.Baralho;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.PersonagensNomes;
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
	    inicializarContexto(jogadorAtual);
	    exibirEstadoJogo();

	    Acao acao = escolherAcao(jogadorAtual);
	    contexto.setAcaoPendente(acao);
	    view.mostrarLog(">>> " + jogadorAtual.getNome() + " escolheu: " + acao.getClass().getSimpleName());

	    if (acao.requerAlvo()) {
	        contexto.setJogadorAlvo(view.perguntarAlvo(jogadorAtual, jogadoresAtivosLista));
	    }

	    if (acao.podeSerContestada() || acao.podeSerbloqueado()) {
	        coletarRespostas(jogadorAtual, acao);
	    } else {
	        executarAcaoInquestionavel(acao);
	    }

	    resolverEstadoPendente(jogadorAtual);

	    view.mostrarLog("<<< Turno de " + jogadorAtual.getNome()
	            + " encerrado. Saldo: " + jogadorAtual.getSaldo() + " moedas.");
	}
	
	private void inicializarContexto(Jogador jogadorAtual) {
	    if (contexto == null) {
	        contexto = new ContextoJogo(null, jogadorAtual, baralho);
	    } else {
	        contexto.setJogadorAutor(jogadorAtual);
	        contexto.setJogadorAlvo(null);
	        contexto.setAceites(0);
	    }
	    contexto.setTotalJogadoresAtivos(contarJogadoresVivos());

	    // TODO (Bloco 1): mover enviarCartasParaJogador para IJogoView
	    // para eliminar o acoplamento entre controller e servidor.*
	    if (view instanceof servidor.JogoViewRemota) {
	        ((servidor.JogoViewRemota) view).enviarCartasParaJogador(jogadorAtual);
	    }
	}

	private void exibirEstadoJogo() {
	    view.mostrarSaldos(jogadoresAtivosLista);
	    exibirCartasPublicas();
	}

	private void exibirCartasPublicas() {
	    StringBuilder sb = new StringBuilder("CARTAS EM JOGO:\n");
	    for (Jogador j : jogadoresAtivosLista) {
	        sb.append(j.getNome()).append(": ");
	        long vivas  = j.getJogadorCartas().getCartas().stream().filter(Carta::isStatusAtiva).count();
	        long mortas = j.getJogadorCartas().getCartas().stream().filter(c -> !c.isStatusAtiva()).count();
	        sb.append(vivas).append(" carta(s) viva(s)");
	        if (mortas > 0) {
	            sb.append(", mortas: ");
	            j.getJogadorCartas().getCartas().stream()
	                    .filter(c -> !c.isStatusAtiva())
	                    .forEach(c -> sb.append(c.getPersonagem().getNome()).append(" "));
	        }
	        if (!j.isStatusAtivo()) sb.append("[ELIMINADO]");
	        sb.append("\n");
	    }
	    view.mostrarLog(sb.toString());
	}
	
	private Acao escolherAcao(Jogador jogadorAtual) {
	    if (jogadorAtual.getSaldo() >= 10) {
	        view.mostrarLog(">>> " + jogadorAtual.getNome() + " tem 10+ moedas — golpe obrigatório!");
	        return versaoJogo.acoes(7, baralho);
	    }
	    int opcao = (versaoJogo instanceof FactoryVersaoInquisidor)
	            ? view.perguntarAcaoComInquisidor(jogadorAtual)
	            : view.perguntarAcao(jogadorAtual);
	    return versaoJogo.acoes(opcao, baralho);
	}
	
	private void coletarRespostas(Jogador jogadorAtual, Acao acao) {
	    contexto.setEstado(new AguardandoRespostaAcao(contexto));

	    List<CompletableFuture<Void>> futuros = jogadoresAtivosLista.stream()
	            .filter(j -> !j.equals(jogadorAtual) && j.isStatusAtivo())
	            .map(outro -> CompletableFuture.runAsync(
	                    () -> processarRespostaJogador(outro, acao)))
	            .collect(java.util.stream.Collectors.toList());

	    CompletableFuture.allOf(futuros.toArray(new CompletableFuture[0])).join();
	}

	private void processarRespostaJogador(Jogador outro, Acao acao) {
	    int resposta = view.perguntarRespostaAcao(
	            outro, null, jogadoresAtivosLista,
	            acao.podeSerContestada(), acao.podeSerbloqueado());

	    synchronized (contexto) {
	        if (!(contexto.getEstado() instanceof AguardandoRespostaAcao)) return;

	        if (resposta == 1) {
	            view.mostrarLog(outro.getNome() + " CONTESTOU!");
	            contexto.getEstado().responderAcao(outro, resposta);

	        } else if (resposta == 3) {
	            List<PersonagensNomes> validos = acao.getPersonagensBloquadores();
	            PersonagensNomes personagem = view.perguntarPersonagemBloqueio(outro, validos);
	            contexto.setPersonagemBloqueio(personagem);
	            view.mostrarLog(outro.getNome() + " BLOQUEOU com " + personagem + "!");
	            contexto.getEstado().responderAcao(outro, resposta);

	        } else {
	            view.mostrarLog(outro.getNome() + " aceitou.");
	            contexto.getEstado().responderAcao(outro, resposta);
	        }
	    }
	}

	private void executarAcaoInquestionavel(Acao acao) {
	    acao.executar(contexto.getJogadorAutor(), contexto.getJogadorAlvo());
	    IEstadoJogo proximo = (acao instanceof Golpear)
	            ? new AguardandoDescarte(contexto, contexto.getJogadorAlvo(), false)
	            : new AguardandoAcao(contexto);
	    contexto.setEstado(proximo);
	}
	
	private void resolverEstadoPendente(Jogador jogadorAtual) {
	    resolverContestacaoDireta();
	    resolverBloqueio(jogadorAtual);
	    resolverDescarte();
	    resolverTrocaEmbaixador(jogadorAtual);
	    resolverTrocaInquisidor(jogadorAtual);
	    resolverExameInquisidor(jogadorAtual);
	}

	private void resolverContestacaoDireta() {
	    if (contexto.getEstado() instanceof ResolvendoContestacao) {
	        ((ResolvendoContestacao) contexto.getEstado()).resolverContestacao();
	    }
	}

	private void resolverBloqueio(Jogador jogadorAtual) {
	    if (!(contexto.getEstado() instanceof AguardandoRespostaBloqueio)) return;

	    AguardandoRespostaBloqueio estado = (AguardandoRespostaBloqueio) contexto.getEstado();
	    view.mostrarLog("Ação bloqueada. " + jogadorAtual.getNome() + ", deseja contestar?");

	    int resposta = view.perguntarRespostaAcao(
	            jogadorAtual, null, jogadoresAtivosLista, true, false);

	    synchronized (contexto) {
	        estado.responderAcao(jogadorAtual, resposta);
	    }

	    // O bloqueio contestado pode ter gerado uma contestação — resolve em cascata
	    resolverContestacaoDireta();
	}

	private void resolverDescarte() {
	    if (!(contexto.getEstado() instanceof AguardandoDescarte)) return;

	    AguardandoDescarte estado = (AguardandoDescarte) contexto.getEstado();
	    Jogador perdedor = estado.getJogadorQueDescarta();
	    if (perdedor == null || !perdedor.isStatusAtivo()) return;

	    view.mostrarLog(perdedor.getNome() + " deve perder uma carta!");
	    Carta cartaMorta = view.pedirCartaParaDescarte(perdedor);
	    estado.descartarCarta(cartaMorta);
	    view.mostrarLog(perdedor.getNome() + " perdeu: " + cartaMorta.getPersonagem().getNome());

	    if (!perdedor.isStatusAtivo()) {
	        view.mostrarLog("!!! " + perdedor.getNome() + " foi ELIMINADO!");
	    }
	}

	private void resolverTrocaEmbaixador(Jogador jogadorAtual) {
	    if (!(contexto.getEstado() instanceof AguardandoTrocaEmbaixador)) return;

	    AguardandoTrocaEmbaixador estado = (AguardandoTrocaEmbaixador) contexto.getEstado();
	    view.mostrarLog(jogadorAtual.getNome() + " deve devolver 2 cartas ao baralho.");

	    for (int i = 0; i < 2; i++) {
	        Carta carta = view.pedirDescarteEmbaixador(jogadorAtual);
	        if (carta != null) {
	            estado.descartarCarta(carta);
	            view.mostrarLog(jogadorAtual.getNome() + " devolveu uma carta.");
	        }
	    }
	}

	private void resolverTrocaInquisidor(Jogador jogadorAtual) {
	    if (!(contexto.getEstado() instanceof AguardandoTrocaInquisidor)) return;

	    AguardandoTrocaInquisidor estado = (AguardandoTrocaInquisidor) contexto.getEstado();
	    view.mostrarLog(jogadorAtual.getNome() + " deve devolver 1 carta ao baralho.");

	    Carta carta = view.pedirDescarteEmbaixador(jogadorAtual); // mesma semântica
	    if (carta != null) {
	        estado.descartarCarta(carta);
	        view.mostrarLog(jogadorAtual.getNome() + " devolveu uma carta.");
	    }
	}

	private void resolverExameInquisidor(Jogador jogadorAtual) {
	    if (!(contexto.getEstado() instanceof AguardandoDecisaoInquisidor)) return;

	    AguardandoDecisaoInquisidor estado = (AguardandoDecisaoInquisidor) contexto.getEstado();
	    Jogador alvo       = estado.getAlvo();
	    Jogador inquisidor = estado.getInquisidor();

	    Carta cartaRevelada = view.pedirCartaParaRevelar(alvo);
	    view.mostrarCartaPrivada(inquisidor, cartaRevelada);
	    boolean forca = view.perguntarForcaExame(inquisidor);

	    view.mostrarLog(forca
	            ? inquisidor.getNome() + " forçou a troca da carta de " + alvo.getNome() + "."
	            : inquisidor.getNome() + " decidiu não forçar a troca.");

	    estado.resolverExame(cartaRevelada, forca, baralho);
	}

	private int contarJogadoresVivos() {
		return (int) jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).count();
	}
}
