package servidor;

import cliente.IClient;
import coup.model.Carta;
import coup.model.Jogador;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BroadcastService {
    private final Map<String, ClienteProxy> clientes;

    public BroadcastService(Map<String, ClienteProxy> clientes) {
        this.clientes = clientes;
    }

    public void mostrarLog(String mensagem) {
        for (ClienteProxy proxy : clientes.values()) {
            try { proxy.getClient().receberLog(mensagem); } catch (Exception ignored) {}
        }
    }

    public void mostrarSaldos(List<Jogador> jogadoresList) {
        List<String> saldos = new ArrayList<>();
        for (Jogador j : jogadoresList) {
            StringBuilder info = new StringBuilder();
            info.append(j.getNome()).append(": ").append(j.getSaldo()).append(" moedas");
            if (!j.isStatusAtivo()) info.append(" (ELIMINADO)");
            
            List<String> cartasMortas = new ArrayList<>();
            if (j.getJogadorCartas() != null) {
                for (Carta c : j.getJogadorCartas().getCartas()) {
                    if (!c.isStatusAtiva()) cartasMortas.add(c.getPersonagem().getNome().toString());
                }
            }
            if (!cartasMortas.isEmpty()) info.append(" | Cartas na mesa: ").append(String.join(", ", cartasMortas));
            
            saldos.add(info.toString());
        }
        
        for (ClienteProxy proxy : clientes.values()) {
            try { proxy.getClient().mostrarSaldos(saldos); } catch (Exception ignored) {}
        }
    }
}