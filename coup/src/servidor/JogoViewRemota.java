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
	public int perguntarRespostaAcao(Jogador jogadorAutor, Personagem supostoPersonagem, List<Jogador> jogadoresLista,
			boolean acaoPodeSerContestada, boolean acaoPodeSerBloqueada) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void pedirAcao() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mostrarLog() {
		// TODO Auto-generated method stub
		
	}
}