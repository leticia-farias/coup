package cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IClient extends Remote {

	void mostrarSuasCartas(List<String> cartas) throws RemoteException;

	int pedirAcao(String nome, int saldo) throws RemoteException;

	int pedirRespostaReacao(String autor, boolean podeContestar, boolean podeBloquear) throws RemoteException;

	String pedirAlvo(String nome, List<String> possiveisAlvos) throws RemoteException;

	int pedirDescarte(String nome, List<String> cartasAtivas) throws RemoteException;

	void mostrarSaldos(List<String> saldos) throws RemoteException;

	void receberLog(String mensagem) throws RemoteException;

	int pedirEscolhaMenu(String titulo, List<String> opcoes) throws RemoteException;

    int pedirCartaParaMostrar(String nome, List<String> cartasAtivas) throws RemoteException;
    int decidirDestinoCartaEspionada(String nomeInquisidor, String cartaMostrada) throws RemoteException;
    int pedirHabilidadeInquisidor() throws RemoteException;
    int pedirModoJogo() throws RemoteException;
    void sincronizarEstado() throws RemoteException;
    
    void ping() throws RemoteException;
}