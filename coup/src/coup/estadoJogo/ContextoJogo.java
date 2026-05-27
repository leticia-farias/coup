package coup.estadoJogo;

import coup.acoes.Acao;
import coup.model.Baralho;
import coup.model.Jogador;

public class ContextoJogo {

    private IEstadoJogo estado;
    private Baralho baralho;
    private Jogador jogadorAutor;
    private Jogador jogadorAlvo;
    private Acao acaoPendente;
    private int aceites = 0;
    private int totalJogadoresAtivos = 0;
    
    // Construtor atualizado para receber o Baralho
    public ContextoJogo(IEstadoJogo estado, Jogador jogadorInicial, Baralho baralho) {
        this.estado = estado;
        this.jogadorAutor = jogadorInicial;
        this.baralho = baralho;
    }

    public void registrarAceites() {
        this.aceites++;
        
        if (this.aceites >= totalJogadoresAtivos - 1) {
            // Todos aceitaram, executa a ação apenas UMA vez
            acaoPendente.executar(jogadorAutor, jogadorAlvo);
            
            // Verifica se a ação resulta em perda de carta ou troca
            if (acaoPendente.getClass().getSimpleName().equals("Assassinar")) {
                setEstado(new AguardandoDescarte(this, jogadorAlvo, false));
            } else if (acaoPendente.getClass().getSimpleName().equals("Embaixadar")) {
                setEstado(new AguardandoTrocaEmbaixador(this, jogadorAutor, baralho));
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
}