package school.sptech;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Une o mesmo grupo aereo sob uma unica chave, porque a planilha de voos usa a
 * razao social da ANAC ("GOL LINHAS AEREAS S.A. (EX- VRG ...)") e a planilha de
 * reclamacoes usa o nome comercial ("Gol Linhas Aereas").
 */
public class NormalizadorCompanhia {

    private static final Map<String, String> NOMES_COMERCIAIS = new LinkedHashMap<>();

    private static final Set<String> MINUSCULAS =
            Set.of("de", "da", "do", "das", "dos", "e", "of", "the");

    static {
        NOMES_COMERCIAIS.put("LATAM", "LATAM");
        NOMES_COMERCIAIS.put("GOL", "GOL");
        NOMES_COMERCIAIS.put("AZUL", "Azul");
        NOMES_COMERCIAIS.put("AMERICAN AIRLINES", "American Airlines");
        NOMES_COMERCIAIS.put("COPA", "Copa Airlines");
        NOMES_COMERCIAIS.put("DELTA", "Delta Air Lines");
        NOMES_COMERCIAIS.put("TAP", "TAP Air Portugal");
        NOMES_COMERCIAIS.put("IBERIA", "Iberia");
        NOMES_COMERCIAIS.put("AVIANCA", "Avianca");
        NOMES_COMERCIAIS.put("AEROLINEAS ARGENTINAS", "Aerolíneas Argentinas");
        NOMES_COMERCIAIS.put("AIR FRANCE", "Air France");
        NOMES_COMERCIAIS.put("BRITISH AIRWAYS", "British Airways");
        NOMES_COMERCIAIS.put("UNITED AIRLINES", "United Airlines");
        NOMES_COMERCIAIS.put("QATAR AIRWAYS", "Qatar Airways");
        NOMES_COMERCIAIS.put("AIR CANADA", "Air Canada");
        NOMES_COMERCIAIS.put("LUFTHANSA", "Lufthansa");
        NOMES_COMERCIAIS.put("TURKISH AIRLINES", "Turkish Airlines");
    }

    private NormalizadorCompanhia() {
    }

    public static String normalizar(String nome) {

        if (nome == null || nome.isBlank()) {
            return "";
        }

        String texto = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .trim();

        if (texto.contains("LATAM") || texto.contains("TAM LINHAS AEREAS")) {
            return "LATAM";
        }
        if (texto.contains("GOL LINHAS AEREAS")) {
            return "GOL";
        }
        if (texto.contains("AZUL LINHAS AEREAS") || texto.contains("AZUL CONECTA")) {
            return "AZUL";
        }
        if (texto.contains("AMERICAN AIRLINES")) {
            return "AMERICAN AIRLINES";
        }
        if (texto.contains("COPA AIRLINES")
                || texto.contains("COMPANIA PANAMENA DE AVIACION")) {
            return "COPA";
        }
        if (texto.contains("DELTA AIR LINES")) {
            return "DELTA";
        }
        if (texto.contains("TAP AIR PORTUGAL")
                || texto.contains("TRANSPORTES AEREOS PORTUGUESES")) {
            return "TAP";
        }
        if (texto.contains("IBERIA") || texto.contains("IBERIA LINEAS")) {
            return "IBERIA";
        }
        if (texto.contains("AVIANCA")) {
            return "AVIANCA";
        }
        if (texto.contains("AEROLINEAS ARGENTINAS")) {
            return "AEROLINEAS ARGENTINAS";
        }
        if (texto.contains("AIR FRANCE")) {
            return "AIR FRANCE";
        }
        if (texto.contains("BRITISH AIRWAYS")) {
            return "BRITISH AIRWAYS";
        }
        if (texto.contains("UNITED AIRLINES")) {
            return "UNITED AIRLINES";
        }
        if (texto.contains("QATAR AIRWAYS")) {
            return "QATAR AIRWAYS";
        }
        if (texto.contains("AIR CANADA")) {
            return "AIR CANADA";
        }
        if (texto.contains("LUFTHANSA")) {
            return "LUFTHANSA";
        }
        if (texto.contains("TURKISH AIRLINES")) {
            return "TURKISH AIRLINES";
        }

        return texto;
    }

    /**
     * Nome curto usado nos graficos. Evita que a dashboard mostre a razao social
     * crua da ANAC, como "AZUL CONECTA LTDA. (EX TWO TAXI AEREO LTDA)".
     */
    public static String nomeComercial(String nome) {

        String chave = normalizar(nome);

        if (chave.isBlank()) {
            return null;
        }

        String comercial = NOMES_COMERCIAIS.get(chave);

        if (comercial != null) {
            return comercial;
        }

        return formatarNome(chave);
    }

    private static String formatarNome(String texto) {

        String limpo = texto.replaceAll("\\s*\\(EX[^)]*\\)", "")
                .replaceAll("\\s+", " ")
                .trim();

        if (limpo.isBlank()) {
            limpo = texto.trim();
        }

        String[] palavras = limpo.toLowerCase(Locale.ROOT).split(" ");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palavras.length; i++) {

            String palavra = palavras[i];

            if (palavra.isBlank()) {
                continue;
            }

            if (!resultado.isEmpty()) {
                resultado.append(" ");
            }

            if (i > 0 && MINUSCULAS.contains(palavra)) {
                resultado.append(palavra);
            } else {
                resultado.append(Character.toUpperCase(palavra.charAt(0)))
                        .append(palavra.substring(1));
            }
        }

        return resultado.toString();
    }
}
