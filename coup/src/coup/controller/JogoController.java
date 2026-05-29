package coup.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import coup.acoes.Acao;
import coup.acoes.Golpear;
import coup.estadoJogo.*;
import coup.factory.FactoryVersaoInquisidor;
import coup.factory.FactoryVersaoOriginal;
import coup.factory.IJogoFactory;
import coup.model.Baralho;
import coup.model.Carta;
import coup.model.Jogador;
import coup.model.Partida;
import coup.model.PersonagensNomes;
import coup.view.IJogoView;

public class JogoController {

    private IJogoView view;
    private ContextoJogo contexto;
    private Partida partida; // responsabilidade dos dados

    public JogoController(IJogoView view) {
        this.view = view;
    }

    public void prepararJogo() {
        int quantJogadores = view.pedirQuantidadeJogadores();

        List<Jogador> jogadoresLista = new ArrayList<>();
        for (int i = 0; i < quantJogadores; i++) {
            jogadoresLista.add(new Jogador(i, view.pedirNomeJogador(i)));
        }

        IJogoFactory versaoJogo = (view.perguntarModo() == 1) 
                ? new FactoryVersaoOriginal() 
                : new FactoryVersaoInquisidor();

        Baralho baralho = new Baralho();
        List<Carta> baralhoCartas = baralho.gerarBaralho(quantJogadores, versaoJogo);
        baralho.distribuirCartas(jogadoresLista, baralhoCartas);

        // inicializa a partida
        this.partida = new Partida(jogadoresLista, baralho, versaoJogo);
    }

    public void iniciarPartida() {
        while (!partida.isFimDeJogo()) {
            Jogador jogadorAtual = partida.avancarTurno();
            processarTurno(jogadorAtual);
        }

        view.mostrarLog("FIM DE JOGO! Vencedor: " + partida.getVencedor().getNome());
    }

    private void processarTurno(Jogador jogadorAtual) {
        inicializarContexto(jogadorAtual);
        exibirEstadoJogo();

        Acao acao = escolherAcao(jogadorAtual);
        contexto.setAcaoPendente(acao);
        view.mostrarLog(">>> " + jogadorAtual.getNome() + " escolheu: " + acao.getClass().getSimpleName());

        if (acao.requerAlvo()) {
            contexto.setJogadorAlvo(view.perguntarAlvo(jogadorAtual, partida.getJogadores()));
        }

        if (acao.podeSerContestada() || acao.podeSerbloqueado()) {
            coletarRespostas(jogadorAtual, acao);
        } else {
            executarAcaoInquestionavel(acao);
        }

        resolverEstadoPendente(jogadorAtual);

        view.mostrarLog("<<< Turno de " + jogadorAtual.getNome() + " encerrado. Saldo: " + jogadorAtual.getSaldo() + " moedas.");
    }

    private void inicializarContexto(Jogador jogadorAtual) {
        if (contexto == null) {
            contexto = new ContextoJogo(null, jogadorAtual, partida.getBaralho());
        } else {
            contexto.setJogadorAutor(jogadorAtual);
            contexto.setJogadorAlvo(null);
            contexto.setAceites(0);
        }
        contexto.setTotalJogadoresAtivos(partida.contarJogadoresVivos());

        if (view instanceof servidor.JogoViewRemota) {
            ((servidor.JogoViewRemota) view).enviarCartasParaJogador(jogadorAtual);
        }
    }

    private void exibirEstadoJogo() {
        view.mostrarSaldos(partida.getJogadores());
        
        StringBuilder sb = new StringBuilder("CARTAS EM JOGO:\n");
        for (Jogador j : partida.getJogadores()) {
            sb.append(j.getNome()).append(": ");
            long vivas  = j.getJogadorCartas().getCartas().stream().filter(Carta::isStatusAtiva).count();
            sb.append(vivas).append(" carta(s) viva(s)");
            if (!j.isStatusAtivo()) sb.append(" [ELIMINADO]");
            sb.append("\n");
        }
        view.mostrarLog(sb.toString());
    }
    
    private Acao escolherAcao(Jogador jogadorAtual) {
        if (jogadorAtual.getSaldo() >= 10) {
            view.mostrarLog(">>> " + jogadorAtual.getNome() + " tem 10+ moedas — golpe obrigatório!");
            return partida.getVersaoJogo().acoes(7, partida.getBaralho());
        }
        int opcao = (partida.getVersaoJogo() instanceof FactoryVersaoInquisidor)
                ? view.perguntarAcaoComInquisidor(jogadorAtual)
                : view.perguntarAcao(jogadorAtual);
        return partida.getVersaoJogo().acoes(opcao, partida.getBaralho());
    }
    
    private void coletarRespostas(Jogador jogadorAtual, Acao acao) {
        contexto.setEstado(new AguardandoRespostaAcao(contexto));

        List<CompletableFuture<Void>> futuros = partida.getJogadores().stream()
                .filter(j -> !j.equals(jogadorAtual) && j.isStatusAtivo())
                .map(outro -> CompletableFuture.runAsync(() -> processarRespostaJogador(outro, acao)))
                .collect(java.util.stream.Collectors.toList());

        CompletableFuture.allOf(futuros.toArray(new CompletableFuture[0])).join();
    }

    private void processarRespostaJogador(Jogador outro, Acao acao) {
        int resposta = view.perguntarRespostaAcao(
                outro, null, partida.getJogadores(),
                acao.podeSerContestada(), acao.podeSerbloqueado());

        synchronized (contexto) {
            if (!(contexto.getEstado() instanceof AguardandoRespostaAcao)) return;

            if (resposta == 1) {
                view.mostrarLog(outro.getNome() + " CONTESTOU!");
                contexto.getEstado().responderAcao(outro, resposta);
            } else if (resposta == 3) {
                PersonagensNomes personagem = view.perguntarPersonagemBloqueio(outro, acao.getPersonagensBloquadores());
                contexto.setPersonagemBloqueio(personagem);
                view.mostrarLog(outro.getNome() + " BLOQUEOU com " + personagem + "!");
                contexto.getEstado().responderAcao(outro, resposta);
            } else {
                view.mostrarLog(outro.getNome() + " aceitou.");
                contexto.getEstado().responderAcao(outro, resposta);
            }
        }
    }

    private void executarAcaoInquestionavel(Acao acao) {
        acao.executar(contexto.getJogadorAutor(), contexto.getJogadorAlvo());
        IEstadoJogo proximo = (acao instanceof Golpear)
                ? new AguardandoDescarte(contexto, contexto.getJogadorAlvo(), false)
                : new AguardandoAcao(contexto);
        contexto.setEstado(proximo);
    }
    
    private void resolverEstadoPendente(Jogador jogadorAtual) {
        if (contexto.getEstado() instanceof ResolvendoContestacao) {
            ((ResolvendoContestacao) contexto.getEstado()).resolverContestacao();
        }

        if (contexto.getEstado() instanceof AguardandoRespostaBloqueio) {
            AguardandoRespostaBloqueio estado = (AguardandoRespostaBloqueio) contexto.getEstado();
            view.mostrarLog("Ação bloqueada. " + jogadorAtual.getNome() + ", deseja contestar?");
            int resposta = view.perguntarRespostaAcao(jogadorAtual, null, partida.getJogadores(), true, false);
            synchronized (contexto) { estado.responderAcao(jogadorAtual, resposta); }
            if (contexto.getEstado() instanceof ResolvendoContestacao) {
                ((ResolvendoContestacao) contexto.getEstado()).resolverContestacao();
            }
        }

        if (contexto.getEstado() instanceof AguardandoDescarte) {
            Jogador perdedor = ((AguardandoDescarte) contexto.getEstado()).getJogadorQueDescarta();
            if (perdedor != null && perdedor.isStatusAtivo()) {
                view.mostrarLog(perdedor.getNome() + " deve perder uma carta!");
                Carta cartaMorta = view.pedirCartaParaDescarte(perdedor);
                contexto.getEstado().descartarCarta(cartaMorta);
                view.mostrarLog(perdedor.getNome() + " perdeu: " + cartaMorta.getPersonagem().getNome());
                if (!perdedor.isStatusAtivo()) view.mostrarLog("!!! " + perdedor.getNome() + " foi ELIMINADO!");
            }
        }

        if (contexto.getEstado() instanceof AguardandoTrocaEmbaixador) {
            for (int i = 0; i < 2; i++) contexto.getEstado().descartarCarta(view.pedirDescarteEmbaixador(jogadorAtual));
        }

        if (contexto.getEstado() instanceof AguardandoTrocaInquisidor) {
            contexto.getEstado().descartarCarta(view.pedirDescarteEmbaixador(jogadorAtual));
        }

        if (contexto.getEstado() instanceof AguardandoDecisaoInquisidor) {
            AguardandoDecisaoInquisidor estado = (AguardandoDecisaoInquisidor) contexto.getEstado();
            Carta cartaRevelada = view.pedirCartaParaRevelar(estado.getAlvo());
            view.mostrarCartaPrivada(estado.getInquisidor(), cartaRevelada);
            boolean forca = view.perguntarForcaExame(estado.getInquisidor());
            view.mostrarLog(forca ? estado.getInquisidor().getNome() + " forçou a troca." : estado.getInquisidor().getNome() + " não forçou a troca.");
            estado.resolverExame(cartaRevelada, forca, partida.getBaralho());
        }
    }
}