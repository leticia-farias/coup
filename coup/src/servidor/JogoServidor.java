package servidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import cliente.IClient;
import coup.controller.JogoController;

public class JogoServidor extends UnicastRemoteObject implements IJogoServidor {

    private final JogoViewRemota viewRemota;
    private final int numJogadoresAlvo = 3;
    private int jogadoresConectados = 0;
    private IClient clienteHost = null; // NOVO: primeiro jogador a conectar

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

        // Primeiro jogador a conectar é o host
        if (jogadoresConectados == 0) {
            clienteHost = cliente;
            viewRemota.setHost(cliente); // NOVO
        }

        viewRemota.adicionarCliente(jogadoresConectados, nomeJogador, cliente);
        jogadoresConectados++;
        viewRemota.mostrarLog(nomeJogador + " entrou no lobby. (" + jogadoresConectados + "/" + numJogadoresAlvo + ")");

        if (jogadoresConectados == numJogadoresAlvo) {
            new Thread(() -> {
                try {
                    viewRemota.mostrarLog("Lobby cheio! O host escolherá a versão do jogo...");
                    JogoController controller = new JogoController(viewRemota);
                    controller.prepararJogo();
                    
                // Força a sincronização inicial de cartas para todos os clientes
                // Agora delegamos essa responsabilidade para a viewRemota
                viewRemota.sincronizarCartasIniciais(controller.getListaJogadores());
                
                controller.iniciarPartida();
                } catch (Exception e) {
                    viewRemota.mostrarLog("Erro fatal na partida: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            JogoServidor servidor = new JogoServidor();
            registry.rebind("CoupServidor", servidor);
            System.out.println("Servidor do Coup iniciado.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}