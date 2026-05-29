package servidor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Personagem;
import coup.model.PersonagensNomes;
import coup.view.IJogoView;
import cliente.IClient;

public class JogoViewRemota implements IJogoView {

    private final Map<String, IClient> clientes = new HashMap<>();

    public void adicionarCliente(String nome, IClient cliente) {
        clientes.put(nome, cliente);
    }

    @Override
    public int perguntarAcao(Jogador jogador) {
        try {
            IClient client = clientes.get(jogador.getNome());
            return client.pedirAcao(jogador.getNome(), jogador.getSaldo());
        } catch (RemoteException e) {
            e.printStackTrace();
            return 5; // Retorna Renda como fallback em caso de erro de rede
        }
    }

    @Override
    public Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
        try {
            IClient client = clientes.get(jogadorAtual.getNome());
            List<String> nomes = new ArrayList<>();
            for (Jogador j : jogadoresLista) {
                if (!j.equals(jogadorAtual) && j.isStatusAtivo()) {
                    nomes.add(j.getNome());
                }
            }
            String nomeEscolhido = client.pedirAlvo(jogadorAtual.getNome(), nomes);
            for (Jogador j : jogadoresLista) {
                if (j.getNome().equalsIgnoreCase(nomeEscolhido)) return j;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void mostrarLog(String mensagem) {
        // Envia a mensagem de log para todos os clientes conectados
        for (IClient client : clientes.values()) {
            try {
                client.receberLog(mensagem);
            } catch (RemoteException ignored) {}
        }
    }

    @Override
    public void mostrarSaldos(List<Jogador> jogadoresList) {
        List<String> saldos = new ArrayList<>();
        for (Jogador j : jogadoresList) {
            saldos.add(j.getNome() + ": " + j.getSaldo() + " moedas (" + (j.isStatusAtivo() ? "Ativo" : "Eliminado") + ")");
        }
        for (IClient client : clientes.values()) {
            try {
                client.mostrarSaldos(saldos);
            } catch (RemoteException ignored) {}
        }
    }

    // Métodos de configuração inicial podem ser fixados ou tratados no setup do Servidor
    @Override public int pedirQuantidadeJogadores() { return clientes.size(); }
    @Override public String pedirNomeJogador(int index) { return new ArrayList<>(clientes.keySet()).get(index); }
    @Override public int perguntarModo() { return 1; }
    @Override public int perguntarOpcaoHerença() { return 2; }
    @Override public void mostrarCartas() {}

	@Override
	public int pedirVersaoJogo() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int perguntarRespostaAcao(Jogador respondente, Personagem supostoPersonagem, List<Jogador> jogadoresLista, boolean acaoPodeSerContestada, boolean acaoPodeSerBloqueada) {
		try {
			// Busca a conexão de rede do jogador que precisa responder
			IClient client = clientes.get(respondente.getNome());
			
			// Chama a tela do cliente perguntando o que ele quer fazer
			return client.pedirRespostaReacao("um jogador", acaoPodeSerContestada, acaoPodeSerBloqueada);
		} catch (RemoteException e) {
			System.err.println("Erro ao contactar o cliente " + respondente.getNome());
			e.printStackTrace();
			return 2; // Em caso de erro de rede, assume "2" (Aceitar) para não travar o jogo
		}
	}
	
	@Override
	public coup.model.Carta pedirCartaParaDescarte(Jogador jogador) {
		try {
			IClient client = clientes.get(jogador.getNome());
			List<String> nomesCartas = new ArrayList<>();
			List<coup.model.Carta> cartasAtivas = new ArrayList<>();

			// Recolhe apenas as cartas que ainda estão vivas
			for (coup.model.Carta c : jogador.getJogadorCartas().getCartas()) {
				if (c.isStatusAtiva()) {
					nomesCartas.add(c.getPersonagem().getNome().toString());
					cartasAtivas.add(c);
				}
			}

			// Se só tiver uma carta, o cliente também pode apenas ver e ser forçado a escolhê-la
			int indexEscolhido = client.pedirDescarte(jogador.getNome(), nomesCartas);
			
			// Prevenção de erros caso o jogador digite um número fora do limite
			if (indexEscolhido < 0 || indexEscolhido >= cartasAtivas.size()) {
				indexEscolhido = 0; 
			}
			
			return cartasAtivas.get(indexEscolhido);

		} catch (RemoteException e) {
			System.err.println("Erro ao contactar o cliente " + jogador.getNome() + " para descarte.");
			// Em caso de falha de rede, mata a primeira carta ativa que encontrar para o jogo não travar
			for (coup.model.Carta c : jogador.getJogadorCartas().getCartas()) {
				if (c.isStatusAtiva()) return c;
			}
		}
		return null;
	}
	
	public void enviarCartasParaJogador(Jogador jogador) {
	    try {
	        IClient client = clientes.get(jogador.getNome());
	        List<String> nomesCartas = new ArrayList<>();
	        for (coup.model.Carta c : jogador.getJogadorCartas().getCartas()) {
	            nomesCartas.add(c.getPersonagem().getNome().toString() + (c.isStatusAtiva() ? " (Ativa)" : " (Morta)"));
	        }
	        client.mostrarSuasCartas(nomesCartas);
	    } catch (RemoteException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void pedirAcao() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Carta pedirDescarteEmbaixador(Jogador jogador) {
	    try {
	        IClient client = clientes.get(jogador.getNome());

	        List<String> nomesCartas = new ArrayList<>();
	        List<Carta> cartasAtivas = new ArrayList<>();

	        for (Carta c : jogador.getJogadorCartas().getCartas()) {
	            if (c.isStatusAtiva()) {
	                nomesCartas.add(c.getPersonagem().getNome().toString());
	                cartasAtivas.add(c);
	            }
	        }

	        // Reutilizamos pedirDescarte — a diferença semântica fica na mensagem enviada
	        // Para distinguir no cliente, você pode criar um método pedirDescarteEmbaixador
	        // na interface IClient futuramente. Por ora, reutilizar é seguro.
	        int indexEscolhido = client.pedirDescarte(
	            jogador.getNome() + " (Embaixador - devolva ao baralho)", nomesCartas
	        );

	        if (indexEscolhido < 0 || indexEscolhido >= cartasAtivas.size()) {
	            indexEscolhido = 0;
	        }

	        return cartasAtivas.get(indexEscolhido);

	    } catch (RemoteException e) {
	        System.err.println("Erro ao contactar " + jogador.getNome() + " para troca do Embaixador.");
	        // Fallback: devolve a última carta ativa
	        for (int i = jogador.getJogadorCartas().getCartas().size() - 1; i >= 0; i--) {
	            if (jogador.getJogadorCartas().getCartas().get(i).isStatusAtiva()) {
	                return jogador.getJogadorCartas().getCartas().get(i);
	            }
	        }
	    }
	    return null;
	}
	
	@Override
	public PersonagensNomes perguntarPersonagemBloqueio(Jogador bloqueador, List<PersonagensNomes> personagensValidos) {
	    try {
	        IClient client = clientes.get(bloqueador.getNome());

	        // Converte enum para string para enviar ao cliente
	        List<String> nomes = personagensValidos.stream()
	                .map(Enum::toString)
	                .collect(java.util.stream.Collectors.toList());

	        // Reutiliza pedirAlvo — semanticamente diferente, mas evita mudar IClient agora
	        String escolha = client.pedirAlvo(
	            bloqueador.getNome() + " - escolha o personagem bloqueador", nomes
	        );

	        return PersonagensNomes.valueOf(escolha);

	    } catch (RemoteException e) {
	        System.err.println("Erro ao contactar " + bloqueador.getNome() + " para bloqueio.");
	        return personagensValidos.get(0); // fallback: primeiro personagem válido
	    }
	}
}