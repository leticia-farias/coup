package coup.model;

public class MascoteTradutor {

    public static String getNomeMascote(PersonagensNomes personagem) {
        switch (personagem) {
            case DUQUE:      return "Java Duke";
            case ASSASSINO:  return "Kali";
            case CAPITAO:    return "Elefante";
            case EMBAIXADOR: return "Droid";
            case CONDESSA:   return "Ferris";
            case INQUISIDOR: return "Octo";
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