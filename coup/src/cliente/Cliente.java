package cliente;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

import servidor.IJogoServidor;

public class Cliente extends UnicastRemoteObject implements IClient {

    private transient Scanner sc = new Scanner(System.in);

    public Cliente() throws RemoteException {
        super();
    }

    @Override
    public void receberLog(String mensagem) throws RemoteException {
        System.out.println("\n[SERVIDOR]: " + mensagem);
    }

    @Override
    public int pedirAcao(String nome, int saldo) throws RemoteException {
        System.out.println("\n----------------------------------");
        System.out.println("Sua vez, " + nome + "! (Saldo: " + saldo + " moedas)");
        System.out.println("1 - Imposto (Duque)");
        System.out.println("2 - Roubar (Capitão)");
        System.out.println("3 - Trocar (Embaixador/Inquisidor)");
        System.out.println("4 - Ajuda externa");
        System.out.println("5 - Pedir renda");
        if (saldo >= 3) System.out.println("6 - Assassinar (Assassino)");
        if (saldo >= 7) System.out.println("7 - Golpe de estado");
        
        System.out.print("Escolha sua ação: ");
        return sc.nextInt();
    }

    @Override
    public String pedirAlvo(String nome, List<String> possiveisAlvos) throws RemoteException {
        System.out.println("\nEscolha um alvo:");
        for (int i = 0; i < possiveisAlvos.size(); i++) {
            System.out.println((i + 1) + " - " + possiveisAlvos.get(i));
        }
        System.out.print("Digite o número do alvo: ");
        int escolha = sc.nextInt();
        // Retorna o nome da string correspondente (ajustando o índice -1)
        return possiveisAlvos.get(escolha - 1);
    }

    @Override
    public int pedirRespostaReacao(String autor, boolean podeContestar, boolean podeBloquear) throws RemoteException {
        System.out.println("\n[Ação de " + autor + "] Como deseja responder?");
        if (podeContestar) System.out.println("1 - Contestar");
        System.out.println("2 - Aceitar");
        if (podeBloquear) System.out.println("3 - Bloquear");
        
        System.out.print("Sua escolha: ");
        return sc.nextInt();
    }

    @Override
    public void mostrarSaldos(List<String> saldos) throws RemoteException {
        System.out.println("\n--- SALDO DOS JOGADORES ---");
        for (String s : saldos) {
            System.out.println(s);
        }
        System.out.println("---------------------------");
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite seu nome para entrar na partida: ");
            String nome = scanner.nextLine();

            // Conecta ao RMI Registry na porta padrão (1099) no localhost
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IJogoServidor servidor = (IJogoServidor) registry.lookup("CoupServidor");

            Cliente cliente = new Cliente();
            servidor.entrarJogo(cliente, nome);

            System.out.println("Conectado! Aguardando o lobby encher...");

        } catch (Exception e) {
            System.err.println("Erro ao conectar no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}