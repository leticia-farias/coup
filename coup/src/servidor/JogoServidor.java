package servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import cliente.IClient;
import coup.controller.JogoController;

public class JogoServidor extends UnicastRemoteObject implements IJogoServidor {

    private final JogoViewRemota viewRemota;
    private final int numJogadoresAlvo = 3; // O jogo inicia automaticamente com 3 jogadores
    private int jogadoresConectados = 0;

    public JogoServidor() throws RemoteException {
        super();
        this.viewRemota = new JogoViewRemota();
    }

    @Override
    public synchronized void entrarJogo(IClient cliente, String nomeJogador) throws RemoteException {
        if (jogadoresConectados >= numJogadoresAlvo) {
            cliente.receberLog("A partida já está cheia.");
            return;
        }

        viewRemota.adicionarCliente(nomeJogador, cliente);
        jogadoresConectados++;
        viewRemota.mostrarLog(nomeJogador + " entrou no lobby. (" + jogadoresConectados + "/" + numJogadoresAlvo + ")");

        if (jogadoresConectados == numJogadoresAlvo) {
            // Inicia a thread do motor do jogo quando o lobby estiver cheio
            new Thread(() -> {
                viewRemota.mostrarLog("Lobby cheio! Iniciando a partida...");
                JogoController controller = new JogoController(viewRemota);
                controller.prepararJogo();
                
                // Força a sincronização inicial de cartas para todos os clientes
                // Agora delegamos essa responsabilidade para a viewRemota
                viewRemota.sincronizarCartasIniciais(controller.getListaJogadores());
                
                controller.iniciarPartida();
            }).start();
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            JogoServidor servidor = new JogoServidor();
            registry.rebind("CoupServidor", servidor);
            System.out.println("Servidor do Coup iniciado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}