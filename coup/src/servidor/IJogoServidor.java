package servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;

import coup.acoes.Acao;
import coup.model.Jogador;

public interface IJogoServidor extends Remote {

	public void entrarJogo() throws RemoteException;

}
