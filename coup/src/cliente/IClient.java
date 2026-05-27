package cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IClient extends Remote {

	void receberLog(String mensagem) throws RemoteException;
    int pedirAcao(String nome, int saldo) throws RemoteException;
    String pedirAlvo(String nome, List<String> possiveisAlvos) throws RemoteException;
    int pedirRespostaReacao(String autor, boolean podeContestar, boolean podeBloquear) throws RemoteException;
    void mostrarSaldos(List<String> saldos) throws RemoteException;
}