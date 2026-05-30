package cliente;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import coup.view.TelaJogo;

public class Cliente extends UnicastRemoteObject implements IClient, Serializable {
	private static final long serialVersionUID = 1L;

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
        // Mapeamento dos nomes na Lore TechSphere
        String[] opcoes = {
            "1 - Coletar Royalties (Java Duke)", 
            "2 - Extorquir (Elefante)", 
            "3 - Gerenciar Identidades (Droid/Octo)", 
            "4 - Ajuda externa", 
            "5 - Pedir renda", 
            "6 - Executar (Kali)", 
            "7 - Golpe de estado"
        };
        
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "Sua vez, " + nome + "! (Saldo: " + saldo + " moedas)\nEscolha sua ação:",
                "Ação de " + nome, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolhaStr == null) return 5; 
        // O parseInt ainda funciona porque mantivemos o número no início da string (ex: "1 - ...")
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
        String[] opcoes = {
            "1 - Stack Legada (com Droid)", 
            "2 - Stack Avançada (com Octo)"
        };
        
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                "Você é o Host! Qual arquitetura deseja carregar?",
                "Configuração da Partida - TechSphere", 
                JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);
                
        if (escolhaStr == null) return 1;
        return Integer.parseInt(escolhaStr.substring(0, 1));
    }

    private List<String> minhasCartasAtuais = new ArrayList<>();
    
    @Override
    public void mostrarSuasCartas(List<String> cartas) throws RemoteException {
        // Manda a tela desenhar suas cartas na parte inferior
        this.minhasCartasAtuais = cartas;
        tela.atualizarSuasCartas(cartas); 
    }

    @Override
    public void sincronizarEstado() throws RemoteException {
        // Isso força a tela a redesenhar as cartas que o cliente já possui em memória
        if (!minhasCartasAtuais.isEmpty()) {
            tela.atualizarSuasCartas(minhasCartasAtuais);
        }
    }
    
    @Override
    public int pedirEscolhaMenu(String titulo, List<String> opcoes) throws RemoteException {
        String escolhaStr = (String) JOptionPane.showInputDialog(tela,
                titulo,
                "Opções do Jogo", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                opcoes.toArray(), 
                opcoes.get(0));
                
        if (escolhaStr == null) return 0; // Retorna a primeira opção como fallback seguro
        return opcoes.indexOf(escolhaStr);
    }
}