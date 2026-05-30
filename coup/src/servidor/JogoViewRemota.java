package servidor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Personagem;
import coup.model.PersonagensNomes;
import coup.view.IJogoView;
import cliente.IClient;

public class JogoViewRemota implements IJogoView {

    private final Map<String, IClient> clientes = new ConcurrentHashMap<>();
    private IClient host = null; 
    
    public JogoViewRemota() {
        Thread heartbeat = new Thread(() -> {
            while (true) {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                
                for (Map.Entry<String, IClient> entry : clientes.entrySet()) {
                    try {
                        entry.getValue().ping();
                    } catch (RemoteException e) {
                        String nome = entry.getKey();
                        // Remove o cliente e avisa todos os outros se ele caiu
                        if (clientes.remove(nome) != null) {
                            mostrarLog("⚠️ ALERTA: O jogador " + nome + " perdeu a ligação com o servidor!");
                        }
                    }
                }
            }
        });
        heartbeat.setDaemon(true);
        heartbeat.start();
    }
    
    public void setHost(IClient host) {
    	this.host = host;
    }

    public void adicionarCliente(String nome, IClient cliente) {
        clientes.put(nome, cliente);
    }

    @Override
    public int perguntarAcao(Jogador jogador) {
        try {
            IClient client = clientes.get(jogador.getNome());
            return client.pedirAcao(jogador.getNome(), jogador.getSaldo());
        } catch (Exception e) {
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
        } catch (Exception e) {
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
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void mostrarSaldos(List<Jogador> jogadoresList) {
        List<String> saldos = new ArrayList<>();
        
        for (Jogador j : jogadoresList) {
            StringBuilder info = new StringBuilder();
            info.append(j.getNome()).append(": ").append(j.getSaldo()).append(" moedas");
            
            if (!j.isStatusAtivo()) {
                info.append(" (ELIMINADO)");
            }
            
            // Procura se o jogador tem cartas que já perderam a vida
            List<String> cartasMortas = new ArrayList<>();
            for (coup.model.Carta c : j.getJogadorCartas().getCartas()) {
                if (!c.isStatusAtiva()) {
                    cartasMortas.add(c.getPersonagem().getNome().toString());
                }
            }
            
            // Se tiver carta morta, anexa na string
            if (!cartasMortas.isEmpty()) {
                info.append(" | Cartas na mesa: ").append(String.join(", ", cartasMortas));
            }
            
            saldos.add(info.toString());
        }
        
        // Envia a lista formatada para todos os clientes
        for (IClient client : clientes.values()) {
            try {
                client.mostrarSaldos(saldos);
            } catch (Exception ignored) {}
        }
    }
    
    // Métodos de configuração inicial podem ser fixados ou tratados no setup do Servidor
    @Override public int pedirQuantidadeJogadores() { return clientes.size(); }
    @Override public String pedirNomeJogador(int index) { return new ArrayList<>(clientes.keySet()).get(index); }
    @Override
    public int perguntarModo() {
        if (host == null) {
            return 1; // Fallback seguro caso o host não esteja definido
        }

        try {
            int versao = host.pedirModoJogo();
            mostrarLog("Versão escolhida pelo host: " + (versao == 1 ? "Embaixador" : "Inquisidor"));
            return versao;
        } catch (Exception e) {
            mostrarLog("Host desconectado ou erro de rede ao escolher versão. Usando versão padrão (Embaixador).");
            return 1; 
        }
    }
    @Override public int perguntarOpcaoHerença() { return 2; }
    @Override public void mostrarCartas() {}

//    @Override 
//    public int perguntarModo() { 
//        try {
//            // Pega o primeiro jogador da lista (O Host que criou a sala)
//            String nomeHost = new ArrayList<>(clientes.keySet()).get(0);
//            IClient host = clientes.get(nomeHost);
//            return host.pedirModoJogo();
//        } catch (Exception e) {
//            System.err.println("Erro de rede ao perguntar o modo de jogo ao host. Usando modo padrão (1).");
//            return 1; // Se der erro de rede, assume a versão original para não quebrar
//        }
//    }
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
        } catch (Exception e) {
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

        } catch (Exception e) {
            System.err.println("Erro ao contactar o cliente " + jogador.getNome() + " para descarte.");
            // Em caso de falha de rede, mata a primeira carta ativa que encontrar para o jogo não travar
            for (coup.model.Carta c : jogador.getJogadorCartas().getCartas()) {
                if (c.isStatusAtiva()) return c;
            }
        }
        return null;
    }
    
    @Override
    public void atualizarCartas(Jogador jogador) {
        try {
            IClient client = clientes.get(jogador.getNome());
            List<String> nomesCartas = new ArrayList<>();
            for (coup.model.Carta c : jogador.getJogadorCartas().getCartas()) {
                nomesCartas.add(c.getPersonagem().getNome().toString() + (c.isStatusAtiva() ? " (Ativa)" : " (Morta)"));
            }
            client.mostrarSuasCartas(nomesCartas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void mostrarLogPrivado(Jogador jogador, String mensagem) {
        try {
            IClient client = clientes.get(jogador.getNome());
            if (client != null) {
                client.receberLog("[SECRETO] " + mensagem);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public int pedirHabilidadeInquisidor(Jogador jogador) {
        try {
            cliente.IClient client = clientes.get(jogador.getNome());
            return client.pedirHabilidadeInquisidor();
        } catch (Exception e) {
            System.err.println("Erro de rede ao pedir habilidade do Inquisidor para " + jogador.getNome());
            return 1; // Fallback seguro: se a rede falhar, assume a opção 1 (Trocar) para o jogo não travar
        }
    } 

   @Override
    public coup.model.Carta pedirCartaParaMostrar(Jogador jogador) {
        try {
            cliente.IClient client = clientes.get(jogador.getNome());
            List<String> nomesCartas = new ArrayList<>();
            List<coup.model.Carta> cartasAtivas = new ArrayList<>();
            
            // Filtra apenas as cartas que o jogador ainda tem vivas para ele escolher uma
            for (coup.model.Carta c : jogador.getJogadorCartas().getCartas()) {
                if (c.isStatusAtiva()) {
                    nomesCartas.add(c.getPersonagem().getNome().toString());
                    cartasAtivas.add(c);
                }
            }
            
            int indexEscolhido = client.pedirCartaParaMostrar(jogador.getNome(), nomesCartas);
            
            if (indexEscolhido < 0 || indexEscolhido >= cartasAtivas.size()) {
                indexEscolhido = 0; 
            }
            return cartasAtivas.get(indexEscolhido);
            
        } catch (Exception e) {
            System.err.println("Erro de rede ao pedir carta para mostrar de " + jogador.getNome());
            // Se o cliente cair no meio do processo, retorna a primeira carta viva encontrada
            return jogador.getJogadorCartas().getCartas().stream().filter(c -> c.isStatusAtiva()).findFirst().orElse(null);
        }
    }

    @Override
    public boolean decidirTrocaInquisidor(Jogador inquisidor, coup.model.Carta cartaMostrada) {
        try {
            cliente.IClient client = clientes.get(inquisidor.getNome());
            String nomeCarta = cartaMostrada.getPersonagem().getNome().toString();
            
            // Envia a resposta do cliente: true se escolheu forçar a troca (opção 1), false caso contrário
            int escolha = client.decidirDestinoCartaEspionada(inquisidor.getNome(), nomeCarta);
            return escolha == 1;
            
        } catch (Exception e) {
            System.err.println("Erro de rede na decisão do Inquisidor " + inquisidor.getNome());
            return false; // Se a conexão falhar, o alvo mantém a carta por segurança
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

	    } catch (Exception e) {
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

	    } catch (Exception e) {
	        System.err.println("Erro ao contactar " + bloqueador.getNome() + " para bloqueio.");
	        return personagensValidos.get(0); // fallback: primeiro personagem válido
	    }
	}
	
	@Override
	public int perguntarAcaoComInquisidor(Jogador jogador) {
	    // Reutiliza pedirAcao — o cliente já mostrará opção 8 se recebermos um flag
	    // Por ora o fallback é o mesmo que perguntarAcao
	    return perguntarAcao(jogador);
	}

	@Override
	public Carta pedirCartaParaRevelar(Jogador alvo) {
	    try {
	        IClient client = clientes.get(alvo.getNome());
	        List<String> nomes = new ArrayList<>();
	        List<Carta> cartasAtivas = new ArrayList<>();
	        for (Carta c : alvo.getJogadorCartas().getCartas()) {
	            if (c.isStatusAtiva()) { nomes.add(c.getPersonagem().getNome().toString()); cartasAtivas.add(c); }
	        }
	        int idx = client.pedirDescarte(alvo.getNome() + " (Inquisidor - escolha qual carta revelar)", nomes);
	        if (idx < 0 || idx >= cartasAtivas.size()) idx = 0;
	        return cartasAtivas.get(idx);
	    } catch (Exception e) {
	        for (Carta c : alvo.getJogadorCartas().getCartas()) if (c.isStatusAtiva()) return c;
	    }
	    return null;
	}

	@Override
	public void mostrarCartaPrivada(Jogador destinatario, Carta carta) {
	    try {
	        // Mensagem privada: envia SOMENTE para o cliente do inquisidor
	        IClient client = clientes.get(destinatario.getNome());
	        client.receberLog("[PRIVADO] Carta examinada: " + carta.getPersonagem().getNome());
	    } catch (Exception e) {
	        System.err.println("Erro ao enviar carta privada para " + destinatario.getNome());
	    }
	}

	@Override
	public boolean perguntarForcaExame(Jogador inquisidor) {
	    try {
	        IClient client = clientes.get(inquisidor.getNome());
	        // 1 = contestar (reaproveitamos semântica: 1 = sim, 2 = não)
	        int resp = client.pedirRespostaReacao("Inquisidor", true, false);
	        return resp == 1;
	    } catch (Exception e) {
	        return false; // fallback: não força
	    }
	}

    // Metodo que permite ao servidor varrer todos os jogadores e forçar o envio das cartas para cada um deles
    public void sincronizarCartasIniciais(List<Jogador> todosJogadores) {
        for (Jogador j : todosJogadores) {
            atualizarCartas(j);
        }
    }
}