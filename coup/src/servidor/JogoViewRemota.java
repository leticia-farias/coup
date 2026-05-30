package servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cliente.IClient;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Personagem;
import coup.model.PersonagensNomes;
import coup.view.IJogoView;

public class JogoViewRemota implements IJogoView {

    private final Map<Integer, ClienteProxy> clientes = new ConcurrentHashMap<>();
    private final BroadcastService broadcast = new BroadcastService(clientes);
    private IClient host = null; 

    public JogoViewRemota() {
        Thread heartbeat = new Thread(() -> {
            while (true) {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                for (Map.Entry<Integer, ClienteProxy> entry : clientes.entrySet()) {
                    try {
                        entry.getValue().ping();
                    } catch (Exception e) {
                        int id = entry.getKey();
                        ClienteProxy removido = clientes.remove(id);
                        if (removido != null) {
                            broadcast.mostrarLog("⚠️ ALERTA: O jogador " + removido.getNome() + " perdeu a ligação com o servidor!");
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

    public void adicionarCliente(int id, String nome, IClient cliente) {
        clientes.put(id, new ClienteProxy(nome, cliente));
    }

    @Override
    public int perguntarAcao(Jogador jogador) {
        ClienteProxy proxy = clientes.get(jogador.getId());
        return proxy != null ? proxy.pedirAcao(jogador.getSaldo()) : 5;
    }

    @Override
    public Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
        ClienteProxy proxy = clientes.get(jogadorAtual.getId());
        if (proxy == null) return null;

        List<String> nomes = new ArrayList<>();
        for (Jogador j : jogadoresLista) {
            if (!j.equals(jogadorAtual) && j.isStatusAtivo()) nomes.add(j.getNome());
        }
        
        String nomeEscolhido = proxy.pedirAlvo(nomes);
        for (Jogador j : jogadoresLista) {
            if (j.getNome().equalsIgnoreCase(nomeEscolhido)) return j;
        }
        return null;
    }

    @Override
    public void mostrarLog(String mensagem) {
        broadcast.mostrarLog(mensagem);
    }

    @Override
    public void mostrarSaldos(List<Jogador> jogadoresList) {
        broadcast.mostrarSaldos(jogadoresList);
    }
    
    @Override public int pedirQuantidadeJogadores() { return clientes.size(); }
    
    @Override 
    public String pedirNomeJogador(int index) { 
        ClienteProxy proxy = clientes.get(index);
        return proxy != null ? proxy.getNome() : "Jogador " + index;
    }
    
    @Override public int perguntarOpcaoHerença() { return 2; }
    @Override public void mostrarCartas() {}
    @Override public int pedirVersaoJogo() { return 0; }
    @Override public void pedirAcao() {}

    @Override
    public int perguntarModo() {
        if (host == null) return 1;
        try {
            int versao = host.pedirModoJogo();
            mostrarLog("Versão escolhida pelo host: " + (versao == 1 ? "Embaixador" : "Inquisidor"));
            return versao;
        } catch (Exception e) {
            mostrarLog("Host desconectado. Usando versão padrão (Embaixador).");
            return 1;
        }
    }
    
    @Override
    public int perguntarRespostaAcao(Jogador respondente, Personagem supostoPersonagem, List<Jogador> jogadoresLista, boolean acaoPodeSerContestada, boolean acaoPodeSerBloqueada) {
        ClienteProxy proxy = clientes.get(respondente.getId());
        return proxy != null ? proxy.pedirRespostaReacao(acaoPodeSerContestada, acaoPodeSerBloqueada) : 2;
    }
    
    @Override
    public Carta pedirCartaParaDescarte(Jogador jogador) {
        ClienteProxy proxy = clientes.get(jogador.getId());
        List<String> nomesCartas = new ArrayList<>();
        List<Carta> cartasAtivas = new ArrayList<>();

        for (Carta c : jogador.getJogadorCartas().getCartas()) {
            if (c.isStatusAtiva()) {
                nomesCartas.add(c.getPersonagem().getNome().toString());
                cartasAtivas.add(c);
            }
        }

        int idx = (proxy != null) ? proxy.pedirDescarte(jogador.getNome(), nomesCartas) : 0;
        if (idx < 0 || idx >= cartasAtivas.size()) idx = 0;
        
        return !cartasAtivas.isEmpty() ? cartasAtivas.get(idx) : null;
    }
    
    @Override
    public void atualizarCartas(Jogador jogador) {
        ClienteProxy proxy = clientes.get(jogador.getId());
        if (proxy != null) {
            List<String> nomesCartas = new ArrayList<>();
            for (Carta c : jogador.getJogadorCartas().getCartas()) {
                nomesCartas.add(c.getPersonagem().getNome().toString() + (c.isStatusAtiva() ? " (Ativa)" : " (Morta)"));
            }
            proxy.mostrarSuasCartas(nomesCartas);
        }
    } 

    @Override
    public int pedirHabilidadeInquisidor(Jogador jogador) {
        ClienteProxy proxy = clientes.get(jogador.getId());
        return proxy != null ? proxy.pedirHabilidadeInquisidor() : 1;
    } 

    @Override
    public Carta pedirCartaParaMostrar(Jogador jogador) {
        ClienteProxy proxy = clientes.get(jogador.getId());
        List<String> nomesCartas = new ArrayList<>();
        List<Carta> cartasAtivas = new ArrayList<>();
        
        for (Carta c : jogador.getJogadorCartas().getCartas()) {
            if (c.isStatusAtiva()) {
                nomesCartas.add(c.getPersonagem().getNome().toString());
                cartasAtivas.add(c);
            }
        }
        
        int idx = (proxy != null) ? proxy.pedirCartaParaMostrar(nomesCartas) : 0;
        if (idx < 0 || idx >= cartasAtivas.size()) idx = 0;
        
        return !cartasAtivas.isEmpty() ? cartasAtivas.get(idx) : null;
    }

    @Override
    public boolean decidirTrocaInquisidor(Jogador inquisidor, Carta cartaMostrada) {
        ClienteProxy proxy = clientes.get(inquisidor.getId());
        if (proxy != null) {
            int escolha = proxy.decidirDestinoCartaEspionada(cartaMostrada.getPersonagem().getNome().toString());
            return escolha == 1;
        }
        return false;
    }

    @Override
    public Carta pedirDescarteEmbaixador(Jogador jogador) {
        ClienteProxy proxy = clientes.get(jogador.getId());
        List<String> nomesCartas = new ArrayList<>();
        List<Carta> cartasAtivas = new ArrayList<>();

        for (Carta c : jogador.getJogadorCartas().getCartas()) {
            if (c.isStatusAtiva()) {
                nomesCartas.add(c.getPersonagem().getNome().toString());
                cartasAtivas.add(c);
            }
        }

        int idx = (proxy != null) ? proxy.pedirDescarte(jogador.getNome() + " (Embaixador)", nomesCartas) : 0;
        if (idx < 0 || idx >= cartasAtivas.size()) idx = 0;
        
        return !cartasAtivas.isEmpty() ? cartasAtivas.get(idx) : null;
    }
    
    @Override
    public PersonagensNomes perguntarPersonagemBloqueio(Jogador bloqueador, List<PersonagensNomes> personagensValidos) {
        ClienteProxy proxy = clientes.get(bloqueador.getId());
        List<String> nomes = personagensValidos.stream().map(Enum::toString).collect(Collectors.toList());
        
        if (proxy != null) {
            String escolha = proxy.pedirAlvo(nomes);
            try { return PersonagensNomes.valueOf(escolha); } catch (IllegalArgumentException e) {}
        }
        return personagensValidos.get(0);
    }
    
    @Override
    public int perguntarAcaoComInquisidor(Jogador jogador) {
        return perguntarAcao(jogador);
    }

    @Override
    public Carta pedirCartaParaRevelar(Jogador alvo) {
        return pedirCartaParaDescarte(alvo); 
    }

    @Override
    public void mostrarLogPrivado(Jogador destinatario, String mensagem) {
        ClienteProxy proxy = clientes.get(destinatario.getId());
        if (proxy != null) proxy.receberLogPrivado(mensagem);
    }

    @Override
    public boolean perguntarForcaExame(Jogador inquisidor) {
        ClienteProxy proxy = clientes.get(inquisidor.getId());
        return proxy != null && proxy.pedirRespostaReacao(true, false) == 1;
    }

    public void sincronizarCartasIniciais(List<Jogador> todosJogadores) {
        for (Jogador j : todosJogadores) atualizarCartas(j);
    }
    
    @Override
    public void mostrarCartaPrivada(Jogador destinatario, Carta carta) {
        mostrarLogPrivado(destinatario, "Carta examinada: " + carta.getPersonagem().getNome());
    }
}