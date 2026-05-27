package coup.controller;

import java.util.ArrayList;
import java.util.List;

import coup.acoes.Acao;
import coup.acoes.FactoryVersaoInquisidor;
import coup.acoes.FactoryVersaoOriginal;
import coup.estadoJogo.AguardandoAcao;
import coup.estadoJogo.AguardandoDescarte;
import coup.estadoJogo.AguardandoRespostaAcao;
import coup.estadoJogo.ContextoJogo;
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
        
    }
	
	private int contarJogadoresVivos() {
		return (int) jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).count();
	}

	private void iniciarProximoTurno() {
		// Soma 1 para ir para o próximo, e o % garante que volta ao zero quando chega ao final da lista
		indexJogadorAtual = (indexJogadorAtual + 1) % jogadoresAtivosLista.size();
		Jogador jogadorAtual = jogadoresAtivosLista.get(indexJogadorAtual);
		
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

		int respostaAcao = view.perguntarAcao(jogadorAtual);
		Acao acao = versaoJogo.acoes(respostaAcao, baralho);
		
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
			
			for (Jogador outro : jogadoresAtivosLista) {
				if (outro.equals(jogadorAtual) || !outro.isStatusAtivo()) continue;
				
				int resposta = view.perguntarRespostaAcao(outro, null, jogadoresAtivosLista, acao.podeSerContestada(), acao.podeSerbloqueado());
				
				if (resposta == 1 || resposta == 3) {
					if (resposta == 1) view.mostrarLog(outro.getNome() + " CONTESTOU a ação!");
					if (resposta == 3) view.mostrarLog(outro.getNome() + " BLOQUEOU a ação!");
					contexto.getEstado().responderAcao(outro, resposta);
					break;
				} else {
					view.mostrarLog(outro.getNome() + " aceitou.");
					contexto.getEstado().responderAcao(outro, resposta);
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

		// 3. Resolve os desfechos pendentes na View (Ex: Descarte)
		if (contexto.getEstado() instanceof AguardandoDescarte) {
			AguardandoDescarte estadoDescarte = (AguardandoDescarte) contexto.getEstado();
			Jogador perdedor = estadoDescarte.getJogadorQueDescarta();
			
			if (perdedor != null && perdedor.isStatusAtivo()) {
				view.mostrarLog(perdedor.getNome() + " deve perder uma carta!");
				coup.model.Carta cartaMorta = view.pedirCartaParaDescarte(perdedor);
				
				estadoDescarte.descartarCarta(cartaMorta);
				view.mostrarLog(perdedor.getNome() + " perdeu a carta: " + cartaMorta.getPersonagem().getNome());
			}
		}
	}

	public void pedirAcao(Jogador jogador) {
		
		view.perguntarAcao(jogador);
	}
	
	
}
