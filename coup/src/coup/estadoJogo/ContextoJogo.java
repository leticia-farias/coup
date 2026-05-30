package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Baralho;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class ContextoJogo {

    private IEstadoJogo estado;
    private Baralho baralho;
    private Jogador jogadorAutor;
    private Jogador jogadorAlvo;
    private Acao acaoPendente;
    private int aceites = 0;
    private int totalJogadoresAtivos = 0;
    private PersonagensNomes personagemBloqueio; 
    
    // Construtor atualizado para receber o Baralho
    public ContextoJogo(IEstadoJogo estado, Jogador jogadorInicial, Baralho baralho) {
        this.estado = estado;
        this.jogadorAutor = jogadorInicial;
        this.baralho = baralho;
    }

    public void registrarAceites() {
        this.aceites++;

        if (this.aceites >= totalJogadoresAtivos - 1) {
            acaoPendente.executar(jogadorAutor, jogadorAlvo);

            if (acaoPendente instanceof coup.acoes.Assassinar) {
                setEstado(new AguardandoDescarte(this, jogadorAlvo, false));

            } else if (acaoPendente instanceof coup.acoes.Golpear) {
                setEstado(new AguardandoDescarte(this, jogadorAlvo, false));

            } else if (acaoPendente instanceof coup.acoes.Embaixadar) {
                setEstado(new AguardandoTrocaEmbaixador(this, jogadorAutor, baralho));

            } else if (acaoPendente instanceof coup.acoes.InquisidorTrocar) {
                // Compra 1 carta antes de entrar no estado de troca
                jogadorAutor.getJogadorCartas().getCartas().add(baralho.comprarCarta());
                setEstado(new AguardandoTrocaInquisidor(this, jogadorAutor, baralho));

            } else if (acaoPendente instanceof coup.acoes.InquisidorExaminar) {
                setEstado(new AguardandoDecisaoInquisidor(this, jogadorAutor, jogadorAlvo));

            } else {
                setEstado(new AguardandoAcao(this));
            }
        }
    }
    
    // Getters e Setters
    public IEstadoJogo getEstado() { return estado; }
    public void setEstado(IEstadoJogo estado) { this.estado = estado; }
    public Jogador getJogadorAutor() { return jogadorAutor; }
    public void setJogadorAutor(Jogador jogadorAtual) { this.jogadorAutor = jogadorAtual; }
    public Jogador getJogadorAlvo() { return jogadorAlvo; }
    public void setJogadorAlvo(Jogador alvo) { this.jogadorAlvo = alvo; }
    public Acao getAcaoPendente() { return acaoPendente; }
    public void setAcaoPendente(Acao acaoPendente) { this.acaoPendente = acaoPendente; }
    public int getAceites() { return aceites; }
    public void setAceites(int aceites) { this.aceites = aceites; }
    public int getTotalJogadoresAtivos() { return totalJogadoresAtivos; }
    public void setTotalJogadoresAtivos(int totalJogadoresAtivos) { this.totalJogadoresAtivos = totalJogadoresAtivos; }
    public Baralho getBaralho() { return baralho; }
    public void setBaralho(Baralho baralho) { this.baralho = baralho; }
    public PersonagensNomes getPersonagemBloqueio() { return personagemBloqueio; }
    public void setPersonagemBloqueio(PersonagensNomes personagemBloqueio) { this.personagemBloqueio = personagemBloqueio; }
}