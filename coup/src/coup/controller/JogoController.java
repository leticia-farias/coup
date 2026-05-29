package coup.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import coup.acoes.Acao;
import coup.estadoJogo.AguardandoAcao;
import coup.estadoJogo.AguardandoDescarte;
import coup.estadoJogo.AguardandoRespostaAcao;
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
		
		//iniciarProximoTurno();
	}



	public void iniciarPartida() {
        while (contarJogadoresVivos() > 1) {
            iniciarProximoTurno();
        }
        // Quando sai do loop, significa que sobrou apenas 1 jogador vivo
        Jogador vencedor = jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).findFirst().get();
        view.mostrarLog("\n FIM DE JOGO! O grande vencedor é: " + vencedor.getNome().toUpperCase());
    }
	
	private int contarJogadoresVivos() {
		return (int) jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).count();
	}

	//loop que ignora os jogadores mortos 
	private void iniciarProximoTurno() {
		Jogador jogadorAtual;
		do {
		// Soma 1 para ir para o próximo, e o % garante que volta ao zero quando chega ao final da lista
			indexJogadorAtual = (indexJogadorAtual + 1) % jogadoresAtivosLista.size();
			jogadorAtual = jogadoresAtivosLista.get(indexJogadorAtual);
		} while (!jogadorAtual.isStatusAtivo()); // Pula os eliminados

		processarTurno(jogadorAtual);
	}
	
private void processarTurno(Jogador jogadorAtual) {
        // 1. Inicializa o contexto (agora com o baralho)
        if (this.contexto == null) {
            this.contexto = new ContextoJogo(null, jogadorAtual, baralho);
            this.contexto.setTotalJogadoresAtivos(contarJogadoresVivos());
        } else {
            this.contexto.setJogadorAutor(jogadorAtual);
        }
        
        // 2. Atualiza as cartas na tela antes de pedir a jogada
        if (view instanceof servidor.JogoViewRemota) {
            ((servidor.JogoViewRemota) view).enviarCartasParaJogador(jogadorAtual);
        }
        
        // 3. Determina a ação respeitando a regra de 10 moedas obrigatórias
        int respostaAcao;
        if (jogadorAtual.getSaldo() >= 10) {
            view.mostrarLog("\n🚨 OBRIGATÓRIO! " + jogadorAtual.getNome() + " acumulou " + jogadorAtual.getSaldo() + " moedas e DEVE realizar um Golpe de Estado!");
            respostaAcao = 7; 
        } else {
            respostaAcao = view.perguntarAcao(jogadorAtual);
        }
        
        Acao acao = versaoJogo.acoes(respostaAcao, baralho);
        
        // 4. Cobrança imediata
        if (acao.getClass().getSimpleName().equals("Assassinar")) {
            jogadorAtual.setSaldo(jogadorAtual.getSaldo() - 3);
        } else if (acao.getClass().getSimpleName().equals("Golpear")) {
            jogadorAtual.setSaldo(jogadorAtual.getSaldo() - 7);
        }

        // 5. Avisa os clientes e define o alvo
        view.mostrarLog("\n>>> O jogador " + jogadorAtual.getNome() + " iniciou a ação: " + acao.getClass().getSimpleName().toUpperCase());
        
        contexto.setAcaoPendente(acao);

        if (acao.requerAlvo()) {
            Jogador alvo = view.perguntarAlvo(jogadorAtual, jogadoresAtivosLista);
            contexto.setJogadorAlvo(alvo);
        }
        
        // 6. Processa respostas (Bloqueios/Contestações)
        if (acao.podeSerContestada() || acao.podeSerbloqueado()) {
            contexto.setEstado(new AguardandoRespostaAcao(contexto));
            
            List<CompletableFuture<Void>> futuros = new ArrayList<>();

            for (Jogador outro : jogadoresAtivosLista) {
                if (outro.equals(jogadorAtual) || !outro.isStatusAtivo()) continue;

                CompletableFuture<Void> futuro = CompletableFuture.runAsync(() -> {
                    int resposta = view.perguntarRespostaAcao(outro, null, jogadoresAtivosLista, acao.podeSerContestada(), acao.podeSerbloqueado());
                    
                    synchronized (contexto) { 
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

            CompletableFuture.allOf(futuros.toArray(new CompletableFuture[0])).join();
            // Resolve se alguém BLOQUEOU a ação original
            if (contexto.getEstado() instanceof coup.estadoJogo.AguardandoRespostaBloqueio) {
                coup.estadoJogo.AguardandoRespostaBloqueio estadoBloqueio = (coup.estadoJogo.AguardandoRespostaBloqueio) contexto.getEstado();
                
                view.mostrarLog("A ação foi BLOQUEADA!");
                
                // Pergunta ao autor da ação se ele quer contestar o bloqueio (ex: duvidar da Condessa do alvo)
                // Usamos o jogadorAutor do contexto
                int respostaAoBloqueio = view.perguntarRespostaAcao(contexto.getJogadorAutor(), null, jogadoresAtivosLista, true, false);
                
                if (respostaAoBloqueio == 1) { // 1 = Contestar o bloqueio
                    view.mostrarLog(contexto.getJogadorAutor().getNome() + " DUVIDOU do bloqueio!");
                    estadoBloqueio.responderAcao(contexto.getJogadorAutor(), 1);
                    
                    // Como virou uma contestação, precisamos resolver para ver quem perde a carta
                    if (contexto.getEstado() instanceof coup.estadoJogo.ResolvendoContestacao) {
                        ((coup.estadoJogo.ResolvendoContestacao) contexto.getEstado()).resolverContestacao();
                    }
                } else {
                    view.mostrarLog(contexto.getJogadorAutor().getNome() + " aceitou o bloqueio. A ação falhou.");
                    estadoBloqueio.responderAcao(contexto.getJogadorAutor(), 2); // 2 = Aceitar
                }
            }

        } else {
            // Ações inquestionáveis (Renda, Golpe de Estado)
            acao.executar(jogadorAtual, contexto.getJogadorAlvo());
            
            if (acao.getClass().getSimpleName().equals("Golpear")) {
                contexto.setEstado(new AguardandoDescarte(contexto, contexto.getJogadorAlvo(), false));
            } else {
                contexto.setEstado(new AguardandoAcao(contexto));
            }
        }

        // 7. Resolve os desfechos pendentes na View (Ex: Descarte ou Troca do Embaixador)
        if (acao.getClass().getSimpleName().equals("Embaixadar") && contexto.getEstado() instanceof coup.estadoJogo.AguardandoTrocaEmbaixador) {
            coup.estadoJogo.AguardandoTrocaEmbaixador estadoTroca = (coup.estadoJogo.AguardandoTrocaEmbaixador) contexto.getEstado();
            
            for (int i = 0; i < 2; i++) {
                view.mostrarLog("\n[EMBAIXADOR] " + jogadorAtual.getNome() + ", escolha a " + (i + 1) + "ª carta para DEVOLVER ao baralho:");
                coup.model.Carta cartaDevolvida = view.pedirCartaParaDescarte(jogadorAtual);
                estadoTroca.descartarCarta(cartaDevolvida); 
            }
            view.mostrarLog(jogadorAtual.getNome() + " devolveu as cartas excedentes ao baralho.");
        }
        
        if (contexto.getEstado() instanceof AguardandoDescarte) {
            AguardandoDescarte estadoDescarte = (AguardandoDescarte) contexto.getEstado();
            Jogador perdedor = estadoDescarte.getJogadorQueDescarta();
            
            if (perdedor != null && perdedor.isStatusAtivo()) {
                view.mostrarLog(perdedor.getNome() + " deve descartar uma carta para a mesa!");
                coup.model.Carta cartaMorta = view.pedirCartaParaDescarte(perdedor);
                
                estadoDescarte.descartarCarta(cartaMorta);
                view.mostrarLog( perdedor.getNome() + " revelou e PERDEU a influência de: " + cartaMorta.getPersonagem().getNome());
            }
        }
        
        view.mostrarLog("\n=============================================");
        view.mostrarLog("Ação Finalizada com sucesso!");
        view.mostrarSaldos(jogadoresAtivosLista);
        view.mostrarLog("=============================================\n");
    }
	
}
