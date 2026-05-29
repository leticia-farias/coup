package coup.model;

public class MascoteTradutor {

    public static String getNomeMascote(PersonagensNomes personagem) {
        switch (personagem) {
            case DUQUE:      return "Duke (Java)";
            case ASSASSINO:  return "Kali (Distro Linux)";
            case CAPITAO:    return "Elefante (PostgreSQL)";
            case EMBAIXADOR: return "Droid (Android)";
            case CONDESSA:   return "Ferris (Rust)";
            case INQUISIDOR: return "Octo (Git/Github)";
            default:         return personagem.name();
        }
    }

    public static String getDescricao(PersonagensNomes personagem) {
        switch (personagem) {
            case DUQUE:      return "Taxar / Bloqueia Ajuda Externa";
            case ASSASSINO:  return "Assassinar (-3 moedas)";
            case CAPITAO:    return "Extorquir / Bloqueia Extorsão";
            case EMBAIXADOR: return "Trocar Influências / Bloqueia Extorsão";
            case CONDESSA:   return "Bloqueia Assassinato";
            case INQUISIDOR: return "Inquisição / Revela ou troca carta";
            default:         return "";
        }
    }
}