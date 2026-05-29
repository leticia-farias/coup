package coup.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import coup.acoes.Acao;
import coup.estadoJogo.AguardandoAcao;
import coup.estadoJogo.AguardandoDescarte;
import coup.estadoJogo.AguardandoRespostaAcao;
import coup.estadoJogo.AguardandoTrocaEmbaixador;
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
		//this.partida = new Partida.PartidaBuilder(modoJogo);

		List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, versaoJogo);

		// distribui duas cartas para cada jogador
		baralho.distribuirCartas(jogadoresLista, baralhoCartas);
		
		iniciarProximoTurno();
	}
	
	public void iniciarPartida() {
        while (contarJogadoresVivos() > 1) {
            iniciarProximoTurno();
        }
        
        Jogador vencedor = jogadoresAtivosLista.stream()
        	    .filter(Jogador::isStatusAtivo).findFirst().orElse(null);
        	view.mostrarLog("FIM DE JOGO! Vencedor: " + vencedor.getNome());
        	// TODO perguntar se quer jogar novamente
        
    }
	
	private int contarJogadoresVivos() {
		return (int) jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).count();
	}

	private void iniciarProximoTurno() {
		// Soma 1 para ir para o próximo, e o % garante que volta ao zero quando chega ao final da lista
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
	    Acao acao;
	    if (jogadorAtual.getSaldo() >= 10) {
	        view.mostrarLog(">>> " + jogadorAtual.getNome() + " tem 10+ moedas e é obrigado a dar golpe de estado!");
	        acao = versaoJogo.acoes(7, baralho);
	    } else {
	        int respostaAcao = view.perguntarAcao(jogadorAtual);
	        acao = versaoJogo.acoes(respostaAcao, baralho);
	    }

		// AVISA TODOS OS CLIENTES SOBRE A AÇÃO ESCOLHIDA
		view.mostrarLog("\n>>> O jogador " + jogadorAtual.getNome() + " iniciou a ação: " + acao.getClass().getSimpleName().toUpperCase());
		
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
			    if (outro.equals(jogadorAtual) || !outro.isStatusAtivo()) continue;

			    CompletableFuture<Void> futuro = CompletableFuture.runAsync(() -> {
			        int resposta = view.perguntarRespostaAcao(outro, null, jogadoresAtivosLista, acao.podeSerContestada(), acao.podeSerbloqueado());
			        
			        synchronized (contexto) { // Sincroniza para evitar condições de corrida no estado
			            // Só computa se o estado ainda for AguardandoRespostaAcao
			            // Se alguém já contestou, o estado muda e os outros aceites são ignorados
			            if (contexto.getEstado() instanceof AguardandoRespostaAcao) {
			                if (resposta == 1 || resposta == 3) {
			                    if (resposta == 1) view.mostrarLog(outro.getNome() + " CONTESTOU a ação!");
			                    if (resposta == 3) view.mostrarLog(outro.getNome() + " BLOQUEOU a ação!");
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
		
		if (contexto.getEstado() instanceof AguardandoTrocaEmbaixador) {
			view.mostrarLog(jogadorAtual.getNome() + " deve perder uma carta!");
		    // pedir 2 descartes ao jogador via view
		}
		
		// Avisa todos do resultado final do turno
		view.mostrarLog("<<< Turno de " + jogadorAtual.getNome() + " encerrado. Saldo atual: " + jogadorAtual.getSaldo() + " moedas.");
		
		view.mostrarLog("\n>>> O jogador " + jogadorAtual.getNome() + " realizou a ação: " + acao.getClass().getSimpleName().toUpperCase());
	}

	public void pedirAcao(Jogador jogador) {
		
		view.perguntarAcao(jogador);
	}
	
	private void exibirCartasPublicas() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("CARTAS EM JOGO:\n");
	    for (Jogador j : jogadoresAtivosLista) {
	        sb.append(j.getNome()).append(": ");
	        long mortas = j.getJogadorCartas().getCartas().stream()
	                .filter(c -> !c.isStatusAtiva()).count();
	        long vivas = j.getJogadorCartas().getCartas().stream()
	                .filter(coup.model.Carta::isStatusAtiva).count();
	        sb.append(vivas).append(" carta(s) viva(s)");
	        if (mortas > 0) {
	            sb.append(", mortas: ");
	            j.getJogadorCartas().getCartas().stream()
	                    .filter(c -> !c.isStatusAtiva())
	                    .forEach(c -> sb.append(c.getPersonagem().getNome()).append(" "));
	        }
	        if (!j.isStatusAtivo()) sb.append(" [ELIMINADO]");
	        sb.append("\n");
	    }
	    view.mostrarLog(sb.toString());
	}
}
