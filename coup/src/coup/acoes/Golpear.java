package coup.acoes;
import coup.model.Jogador;
import coup.model.PersonagensNomes;

public class Golpear implements Acao {
    @Override public boolean requerAlvo() { return true; }
    @Override public boolean podeSerbloqueado() { return false; }
    @Override public boolean podeSerContestada() { return false; }
    @Override public boolean precisaSaldoMinino() { return true; }
    @Override public PersonagensNomes getPersonagemNecessario() { return null; }

    @Override
    public void executar(Jogador autor, Jogador alvo) {
        if (autor.getSaldo() >= 7 && alvo != null) {
            autor.setSaldo(autor.getSaldo() - 7);
            // Aqui o golpe foi validado. O descarte em si ocorrerá na máquina de estados 
            // (que vai pedir a carta para o alvo usando a View).
        }
    }
}