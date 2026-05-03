package coup.domain.model;

import java.util.Scanner;

import coup.MenuOpcoes;
import coup.jogador.JogadorCartas;

public class Jogador {

	int id;
	String nome;
	int saldo = 2;
	JogadorCartas jogadorCartas = new JogadorCartas();
	boolean statusAtivo = true;
	
	Personagem p = null;
	MenuOpcoes menu = new MenuOpcoes();
	
	// metodos: adicionarMoedas, removerMoedas, getCartasAtivas, isEleminado
	
	public Personagem escolherCartaParaMostrar() {

		// SE TIVER AS DUAS CARTAS AINDA
		if (jogadorCartas.isStatusCompleto()) {

			menu.mostrarCartas(this);
			System.out.println("DIGITE O NÚMERO DE QUAL CARTA DESEJA MOSTRAR: ");
			
			resposta = sc.nextInt();

			if (resposta == 0) {
				p = jogadorCartas.getCartas().get(0).getPersonagem();
			} else {
				p = jogadorCartas.getCartas().get(1).getPersonagem();
			}

		} else {
			for (Carta carta : jogadorCartas.getCartas()) {
				if (carta.isStatusAtiva()) {
					p = carta.getPersonagem();
				}
			}
		}

		return p;
	}

	public void escolherCartaParaMorrer() {
		int r = -1;
		
		// SE TIVER AS DUAS CARTAS AINDA
		if (jogadorCartas.statusCompleto) {

			menu.mostrarCartas(this);
			System.out.println("DIGITE O NÚMERO DE QUAL CARTA VOCÊ DESEJA MATAR");
			//p = escolherCarta();
			r = sc.nextInt();

		} else {
			for (Carta carta : jogadorCartas.cartas) {
				if (carta.isStatusAtiva()) {
					//p = carta.personagem;
					r = sc.nextInt();
				}
			}
		}
		
		//cartasEntity.cartas.remove(p);
		jogadorCartas.cartas.remove(r);
	}
	
	public Jogador(int id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public Jogador() {
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getSaldo() {
		return saldo;
	}

	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}

	public JogadorCartas getJogadorCartas() {
		return jogadorCartas;

	public void setJogadorCartas(JogadorCartas jogadorCartas) {
		this.jogadorCartas = jogadorCartas;
	}

	public boolean isStatusAtivo() {
		return statusAtivo;
	}

	public void setStatusAtivo(boolean statusAtivo) {
		this.statusAtivo = statusAtivo;
	}

	public void adicionarMoedas(int i) {
		// TODO Auto-generated method stub
		
	}

	public void adicionarCarta(Object comprarTopo) {
		// TODO Auto-generated method stub
		
	}

}
