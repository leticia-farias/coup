package coup.ui;

import java.util.List;

import javax.swing.JOptionPane;

import coup.jogador.Jogador;
import coup.personagens.Personagem;

public class MenuGrafico {

    public int pedirQuantidadeJogadores() {
        while (true) {
            String entrada = JOptionPane.showInputDialog(
                    null,
                    "Digite número de jogadores (2 a 6):",
                    "Quantidade de jogadores",
                    JOptionPane.QUESTION_MESSAGE
            );

            try {
                int quantidade = Integer.parseInt(entrada);

                if (quantidade >= 2 && quantidade <= 6) {
                    return quantidade;
                }

                JOptionPane.showMessageDialog(null, "Quantidade inválida. Digite de 2 a 6.");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Digite um número.");
            }
        }
    }

    public String pedirNomeJogador(int index) {
        while (true) {
            String nome = JOptionPane.showInputDialog(
                    null,
                    "Digite o nome do " + (index + 1) + "º jogador:",
                    "Nome do jogador",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (nome != null && !nome.trim().isEmpty()) {
                return nome.trim();
            }

            JOptionPane.showMessageDialog(null, "Nome inválido.");
        }
    }

    public int pedirVersaoJogo() {
        String[] opcoes = {
                "Com embaixador",
                "Com inquisidor"
        };

        int escolha = JOptionPane.showOptionDialog(
                null,
                "Escolha a versão do jogo:",
                "Versão do Coup",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        return escolha + 1;
    }

    
    public void mostrarCartasJogadores(List<Jogador> jogadoresList) {
        StringBuilder texto = new StringBuilder();

        texto.append("CARTAS DOS JOGADORES:\n\n");

        for (Jogador jogador : jogadoresList) {
            texto.append("Jogador: ").append(jogador.getNome()).append("\n");
            texto.append("Cartas:\n");

            for (coup.Carta carta : jogador.getJogadorCartas().getCartas()) {
                texto.append("- ")
                     .append(carta.getPersonagem().getNome())
                     .append("\n");
            }

            texto.append("\n");
        }

        JOptionPane.showMessageDialog(
                null,
                texto.toString(),
                "Cartas dos jogadores",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    
    public void mostrarCartasDoJogador(Jogador jogador) {

        StringBuilder texto = new StringBuilder();

        texto.append(jogador.getNome())
             .append(", suas cartas são:\n\n");

        for (coup.Carta carta : jogador.getJogadorCartas().getCartas()) {

            texto.append("- ")
                 .append(carta.getPersonagem().getNome())
                 .append("\n");
        }

        JOptionPane.showMessageDialog(
                null,
                texto.toString(),
                "Cartas de " + jogador.getNome(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    
    
    public int perguntarAcao(Jogador jogador) {
        String[] opcoes = {
                "Imposto (Duque)",
                "Roubar (Capitão)",
                "Trocar (Embaixador)",
                "Ajuda externa",
                "Pedir renda"
        };

        int escolha = JOptionPane.showOptionDialog(
                null,
                jogador.getNome() + ", escolha sua ação:",
                "Ação",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        return escolha + 1;
    }

    public Jogador perguntarAlvo(Jogador jogadorAtual, List<Jogador> jogadoresLista) {
        String[] nomes = jogadoresLista.stream()
                .filter(j -> !j.equals(jogadorAtual))
                .filter(Jogador::isStatusAtivo)
                .map(Jogador::getNome)
                .toArray(String[]::new);

        String nomeEscolhido = (String) JOptionPane.showInputDialog(
                null,
                jogadorAtual.getNome() + ", escolha o alvo:",
                "Escolher alvo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nomes,
                nomes[0]
        );

        for (Jogador jogador : jogadoresLista) {
            if (jogador.getNome().equalsIgnoreCase(nomeEscolhido)) {
                return jogador;
            }
        }

        return null;
    }

    public int perguntarRespostaAcao(
            Jogador jogadorAutor,
            Personagem supostoPersonagem,
            List<Jogador> jogadoresLista,
            boolean acaoPodeSerContestada,
            boolean acaoPodeSerBloqueada
    ) {
        if (acaoPodeSerContestada && acaoPodeSerBloqueada) {
            String[] opcoes = {"Contestar", "Aceitar", "Bloquear"};

            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Como deseja responder?",
                    "Resposta da ação",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            return escolha + 1;
        }

        if (acaoPodeSerContestada) {
            String[] opcoes = {"Contestar", "Aceitar"};

            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Como deseja responder?",
                    "Resposta da ação",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            return escolha + 1;
        }

        if (acaoPodeSerBloqueada) {
            String[] opcoes = {"Aceitar", "Bloquear"};

            int escolha = JOptionPane.showOptionDialog(
                    null,
                    "Como deseja responder?",
                    "Resposta da ação",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (escolha == 0) return 2;
            if (escolha == 1) return 3;
        }

        return 2;
    }

    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem);
    }
}