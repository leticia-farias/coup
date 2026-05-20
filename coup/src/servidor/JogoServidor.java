package servidor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class JogoServidor extends UnicastRemoteObject implements IJogoServidor {

	public JogoServidor() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {

	}
	
	@Override
	public void entrarJogo() throws RemoteException {
		System.out.println("sla");
	}

}
