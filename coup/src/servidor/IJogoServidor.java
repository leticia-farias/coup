package servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;

import cliente.IClient;

public interface IJogoServidor extends Remote {

	 void entrarJogo(IClient cliente, String nomeJogador) throws RemoteException;
}