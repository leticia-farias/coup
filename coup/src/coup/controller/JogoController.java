package coup.controller;

import java.util.ArrayList;
import java.util.List;

import coup.acoes.FactoryVersaoOriginal;
import coup.estadoJogo.ContextoJogo;
import coup.factory.IJogoFactory;
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
import coup.view.IJogoView;

public class JogoController {

	private IJogoView view;
	private ContextoJogo contexto;
	
	private List<Jogador> jogadoresLista = new ArrayList<Jogador>();
	private List<Jogador> jogadoresAtivosLista = new ArrayList<Jogador>();
	private Baralho baralho = new Baralho();
	
	private IJogoFactory tipoJogo;
	
	private int indexJogadorAtual = 0;
	
	
	public JogoController(IJogoView view) {
		this.view = view;
	}	

	// logica de loop dos turnos
	// verificacao de saldo
	// processamento de acoes

	public void prepararJogo() {
		List<Personagem> personagensLista = new ArrayList<>();
		
		// verifica quant de jogadores
		int quantJogadores = view.pedirQuantidadeJogadores();

		// adiciona jogadores
		for (int i = 0; i < quantJogadores; i++) {
			String nome = view.pedirNomeJogador(i);
			jogadoresLista.add(new Jogador(i, nome));
		}

		// separa quant de cartas de cada personagem com base na quant de jogadores
		personagensLista.add(new Assassino());
		personagensLista.add(new Capitao());
		personagensLista.add(new Condessa());
		personagensLista.add(new Duque());

		// pergunta qual a versão do jogo
		if (view.pedirVersaoJogo() == 1) {
			personagensLista.add(new Embaixador());
			
			tipoJogo = new FactoryVersaoOriginal();
		} else {
			personagensLista.add(new Inquisidor());
		}

		List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, personagensLista);

		// embaralha cartas
		baralhoCartas = baralho.embaralharCartas(baralhoCartas);

		// distribui duas cartas para cada jogador
		baralho.distribuirCartas(jogadoresLista, baralhoCartas);
	}
	
	public void iniciarProximoTurno() {
		if (jogadoresAtivosLista.isEmpty()) {
			return;
		}
		int totalJogadores = jogadoresAtivosLista.size();
		int jogadoresVerificados = 0;
		indexJogadorAtual++;
		
		if (indexJogadorAtual == (jogadoresLista.size() + 1)) {
			indexJogadorAtual = 0;
		}
		
		Jogador proximoJogador = jogadoresLista.get(indexJogadorAtual);
		
		while (!proximoJogador.isStatusAtivo()) {
			indexJogadorAtual++;
			proximoJogador = jogadoresLista.get(indexJogadorAtual);
		}
	}
	
//	public void iniciarProximoTurno() {
//	    if (jogadoresLista == null || jogadoresLista.isEmpty()) {
//	        return; // Previne erros se a lista estiver vazia
//	    }
//
//	    int totalJogadores = jogadoresLista.size();
//	    int jogadoresVerificados = 0;
//
//	    do {
//	        // O operador '%' faz o índice voltar a 0 automaticamente ao atingir o limite
//	        indexJogadorAtual = (indexJogadorAtual + 1) % totalJogadores;
//	        jogadoresVerificados++;
//	        
//	    // Continua rodando enquanto o jogador estiver inativo
//	    // A segunda condição impede um loop infinito se todos estiverem inativos
//	    } while (!jogadoresLista.get(indexJogadorAtual).isStatusAtivo() 
//	             && jogadoresVerificados < totalJogadores);
//
//	    // Verificação extra (opcional): se rodou todos e ninguém está ativo
//	    if (!jogadoresLista.get(indexJogadorAtual).isStatusAtivo()) {
//	        System.out.println("Fim de jogo ou nenhum jogador ativo!");
//	        // Aqui você pode disparar a lógica de fim de partida
//	    }
//	}
}
