package cliente;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javax.swing.JOptionPane;
import coup.view.TelaJogo;

public class Cliente extends UnicastRemoteObject implements IClient {

    private transient TelaJogo tela;
    private String meuNome;

    public Cliente(TelaJogo tela, String meuNome) throws RemoteException {
        super();
        this.tela = tela;
        this.meuNome = meuNome;
    }

    @Override
    public void receberLog(String mensagem) throws RemoteException {
        tela.adicionarLog(mensagem); 
    }

    @Override
    public int pedirAcao(String nome, int saldo) throws RemoteException {
        String[] opcoes = {"1 - Imposto (Duque)", "2 - Roubar (Capitão)", "3 - Trocar (Emb/Inq)", "4 - Ajuda externa", "5 - Pedir renda", "6 - Assassinar", "7 - Golpe de estado"};
        
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "Sua vez, " + nome + "! (Saldo: " + saldo + " moedas)\nEscolha sua ação:",
                "Ação de " + nome, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolhaStr == null) return 5; 
        return Integer.parseInt(escolhaStr.substring(0, 1));
    }

    @Override
    public int pedirHabilidadeInquisidor() throws RemoteException {
        String[] opcoes = {"1 - Trocar 1 carta com o baralho", "2 - Espionar a carta de um jogador"};
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "[INQUISIDOR] Qual habilidade deseja usar?",
                "Habilidade do Inquisidor", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
                
        if (escolhaStr == null) return 1;
        return Integer.parseInt(escolhaStr.substring(0, 1));
    }

    @Override
    public String pedirAlvo(String nome, List<String> possiveisAlvos) throws RemoteException {
        String alvoStr = (String) JOptionPane.showInputDialog(tela,
                "Escolha um alvo para a sua ação:",
                "Seleção de Alvo", JOptionPane.QUESTION_MESSAGE, null, possiveisAlvos.toArray(), possiveisAlvos.get(0));
                
        return alvoStr != null ? alvoStr : possiveisAlvos.get(0);
    }

    @Override
    public int pedirRespostaReacao(String autor, boolean podeContestar, boolean podeBloquear) throws RemoteException {
        String[] opcoes;
        if (podeContestar && podeBloquear) opcoes = new String[]{"1 - Contestar", "2 - Aceitar", "3 - Bloquear"};
        else if (podeContestar) opcoes = new String[]{"1 - Contestar", "2 - Aceitar"};
        else if (podeBloquear) opcoes = new String[]{"2 - Aceitar", "3 - Bloquear"};
        else opcoes = new String[]{"2 - Aceitar"};

        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "[Ação de " + autor + "] Como deseja responder?",
                "Reação", JOptionPane.QUESTION_MESSAGE, null, opcoes, "2 - Aceitar");
                
        if (escolhaStr == null) return 2;
        return Integer.parseInt(escolhaStr.substring(0, 1));
    }

    @Override
    public void mostrarSaldos(List<String> saldos) throws RemoteException {
        StringBuilder sb = new StringBuilder("\n--- STATUS DA MESA ---\n");
        for (String s : saldos) {
            sb.append(s).append("\n");
        }
        tela.adicionarLog(sb.toString());
        
        // Manda os dados para a tela desenhar as cartas dos oponentes
        tela.atualizarMesa(saldos, meuNome); 
    }
    
    @Override
    public int pedirDescarte(String nome, List<String> cartasAtivas) throws RemoteException {
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "[DESCARTE] " + nome + ", escolha uma carta para descartar/devolver:",
                "Descarte de Carta", JOptionPane.WARNING_MESSAGE, null, cartasAtivas.toArray(), cartasAtivas.get(0));
                
        if (escolhaStr == null) return 0;
        return cartasAtivas.indexOf(escolhaStr);
    }

    @Override
    public int pedirCartaParaMostrar(String nome, List<String> cartasAtivas) throws RemoteException {
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "[INQUISIDOR] Você está sendo espionado! Escolha UMA carta para mostrar:",
                "Espionagem", JOptionPane.WARNING_MESSAGE, null, cartasAtivas.toArray(), cartasAtivas.get(0));
                
        if (escolhaStr == null) return 0;
        return cartasAtivas.indexOf(escolhaStr);
    }

    @Override
    public int decidirDestinoCartaEspionada(String nomeInquisidor, String cartaMostrada) throws RemoteException {
        String[] opcoes = {"1 - Forçar troca com o baralho", "2 - Deixar o alvo manter a carta"};
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "O alvo te mostrou a carta: " + cartaMostrada + "\nO que você deseja fazer?",
                "Decisão do Inquisidor", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
                
        if (escolhaStr == null) return 2;
        return Integer.parseInt(escolhaStr.substring(0, 1));
    }

    @Override
    public int pedirModoJogo() throws RemoteException {
        String[] opcoes = {"1 - Original (com Embaixador)", "2 - Expansão (com Inquisidor)"};
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "Você é o Host! Qual versão deseja jogar?",
                "Configuração da Partida", JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);
                
        if (escolhaStr == null) return 1;
        return Integer.parseInt(escolhaStr.substring(0, 1));
    }

    @Override
    public void mostrarSuasCartas(List<String> cartas) throws RemoteException {
        // Manda a tela desenhar suas cartas na parte inferior
        tela.atualizarSuasCartas(cartas); 
    }
}