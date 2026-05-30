package servidor;

import cliente.IClient;
import java.util.List;

public class ClienteProxy {
    private final String nome;
    private final IClient client;

    public ClienteProxy(String nome, IClient client) {
        this.nome = nome;
        this.client = client;
    }
    
    public String getNome() { return nome; }

    public IClient getClient() { return client; }

    public int pedirAcao(int saldo) {
        try { return client.pedirAcao(nome, saldo); } catch (Exception e) { return 5; /* Fallback: Renda */ }
    }

    public String pedirAlvo(List<String> nomes) {
        try { return client.pedirAlvo(nome, nomes); } catch (Exception e) { return nomes.isEmpty() ? "" : nomes.get(0); }
    }

    public int pedirRespostaReacao(boolean contestavel, boolean bloqueavel) {
        try { return client.pedirRespostaReacao("Um jogador", contestavel, bloqueavel); } catch (Exception e) { return 2; /* Fallback: Aceitar */ }
    }

    public int pedirDescarte(String titulo, List<String> nomesCartas) {
        try { return client.pedirDescarte(titulo, nomesCartas); } catch (Exception e) { return 0; }
    }

    public void mostrarSuasCartas(List<String> nomesCartas) {
        try { client.mostrarSuasCartas(nomesCartas); } catch (Exception ignored) {}
    }

    public int pedirHabilidadeInquisidor() {
        try { return client.pedirHabilidadeInquisidor(); } catch (Exception e) { return 1; /* Fallback: Trocar */ }
    }

    public int pedirCartaParaMostrar(List<String> nomesCartas) {
        try { return client.pedirCartaParaMostrar(nome, nomesCartas); } catch (Exception e) { return 0; }
    }

    public int decidirDestinoCartaEspionada(String nomeCarta) {
        try { return client.decidirDestinoCartaEspionada(nome, nomeCarta); } catch (Exception e) { return 2; }
    }

    public void receberLogPrivado(String mensagem) {
        try { client.receberLog("[SECRETO] " + mensagem); } catch (Exception ignored) {}
    }

    public void ping() throws Exception {
        client.ping();
    }
}