package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Carta;
import coup.model.Jogador;

public class AguardandoAcao implements IEstadoJogo {

    /*private ContextoJogo contexto;
    private Acao acaoPendente;
    private int jogadoresConcordantes = 0;
    private int totalVotos;
    private Jogador jogadorAtual; */


    /*public AguardandoAcao(ContextoJogo contexto, Acao acaoPendente, int totalAtivos) {
        this.contexto = contexto;
        this.acaoPendente = acaoPendente;
        this.totalVotos = totalAtivos - 1
    } */
   
    private ContextoJogo contexto;

    // Construtor limpo e correto usado pelo JogoController
    public AguardandoAcao(ContextoJogo contexto) {
        this.contexto = contexto;
    }

    @Override
    public void escolherAcao(Acao acao) {
        // Define a ação escolhida na memória do turno corrente
        contexto.setAcaoPendente(acao);
    }

    @Override
    public void responderAcao(Jogador respondente, int resposta) {
        // Bloqueio de segurança: Ninguém pode reagir antes da ação ser definida
        throw new IllegalStateException("Não é possível responder a uma ação, pois ela ainda está a ser escolhida.");
    }

    @Override
    public void descartarCarta(Carta carta) {
        // Bloqueio de segurança: Ninguém perde cartas no início do turno
        throw new IllegalStateException("Não é o momento de descartar cartas.");
    }
}