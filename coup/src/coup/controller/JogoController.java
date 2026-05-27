package coup.controller;

import java.util.ArrayList;
import java.util.List;

import coup.acoes.Acao;
import coup.acoes.FactoryVersaoInquisidor;
import coup.acoes.FactoryVersaoOriginal;
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
        
        view.mostrarLog(); // Exibir vencedor
    }
	
	private int contarJogadoresVivos() {
		return (int) jogadoresAtivosLista.stream().filter(Jogador::isStatusAtivo).count();
	}

	private void iniciarProximoTurno() {
		indexJogadorAtual = indexJogadorAtual % jogadoresAtivosLista.size();
		Jogador jogadorAtual = jogadoresAtivosLista.get(indexJogadorAtual);
		
		processarTurno(jogadorAtual);
		
	}
	
	private void processarTurno(Jogador jogadorAtual) {
		view.mostrarCartas();
		int respostaAcao = view.perguntarAcao(jogadorAtual);
		Acao acao = versaoJogo.acoes(respostaAcao);
		
		if (contexto.getEstado() instanceof AguardandoRespostaAcao) {
			int resposta = view.perguntarRespostaAcao(jogadorAtual, jogadoresAtivosLista, acao.podeSerContestada(), acao.podeSerbloqueado());
		    Jogador respondente = jogadoresAtivosLista.get(0); // Exemplo (depois varre a lista)
		    
		    if (resposta == 1) {
		        view.mostrarLog(respondente.getNome() + " CONTESTOU a ação!");
		    } else if (resposta == 3) {
		        view.mostrarLog(respondente.getNome() + " BLOQUEOU a ação!");
		    } else {
		        view.mostrarLog(respondente.getNome() + " aceitou.");
		    }
		    
		    contexto.getEstado().responderAcao(respondente, respostaAcao);
		}
		
		
		if (acao.requerAlvo()) {
			Jogador alvo = view.perguntarAlvo(jogadorAtual, jogadoresAtivosLista);
		}
		
		if (acao.podeSerbloqueado() || acao.podeSerContestada()) {
			
		}
	}

	public void pedirAcao(Jogador jogador) {
		
		view.perguntarAcao(jogador);
	}
	
	
}
