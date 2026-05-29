package servidor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import coup.model.Jogador;
import coup.model.Personagem;
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
            } catch (RemoteException ignored) {}
        }
    }
    
    // Métodos de configuração inicial podem ser fixados ou tratados no setup do Servidor
    @Override public int pedirQuantidadeJogadores() { return clientes.size(); }
    @Override public String pedirNomeJogador(int index) { return new ArrayList<>(clientes.keySet()).get(index); }
    //@Override public int perguntarModo() { return 1; }
    @Override public int perguntarOpcaoHerença() { return 2; }
    @Override public void mostrarCartas() {}

    @Override 
    public int perguntarModo() { 
        try {
            // Pega o primeiro jogador da lista (O Host que criou a sala)
            String nomeHost = new ArrayList<>(clientes.keySet()).get(0);
            IClient host = clientes.get(nomeHost);
            return host.pedirModoJogo();
        } catch (Exception e) {
            System.err.println("Erro de rede ao perguntar o modo de jogo ao host. Usando modo padrão (1).");
            return 1; // Se der erro de rede, assume a versão original para não quebrar
        }
    }
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
    public int pedirHabilidadeInquisidor(Jogador jogador) {
        try {
            cliente.IClient client = clientes.get(jogador.getNome());
            return client.pedirHabilidadeInquisidor();
        } catch (RemoteException e) {
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
            
        } catch (RemoteException e) {
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
            
        } catch (RemoteException e) {
            System.err.println("Erro de rede na decisão do Inquisidor " + inquisidor.getNome());
            return false; // Se a conexão falhar, o alvo mantém a carta por segurança
        }
    }

    @Override
    public void pedirAcao() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public coup.model.Carta pedirDescarteEmbaixador(Jogador jogador) {
        // TODO: Implementar a chamada RMI para o Embaixador futuramente
        return null;
    }

    // Metodo que permite ao servidor varrer todos os jogadores e forçar o envio das cartas para cada um deles
    public void sincronizarCartasIniciais(List<Jogador> todosJogadores) {
        for (Jogador j : todosJogadores) {
            enviarCartasParaJogador(j);
        }
    }
}