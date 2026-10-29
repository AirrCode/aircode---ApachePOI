package school.sptech;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    private static final String ARQUIVO_VOOS = "voos-tratados.xlsx";
    private static final String ARQUIVO_RECLAMACOES = "dadosconsumidor2025.xlsx";

    public static void main(String[] args) {
        LocalDateTime dataHoraInicio = LocalDateTime.now();

        LeitorExcel leitorExcel = new LeitorExcel();
        List<Voo> voos = leitorExcel.extrairVoos(ARQUIVO_VOOS);

        LeitorReclamacoesExcel leitorReclamacoes = new LeitorReclamacoesExcel();
        List<Reclamacao> reclamacoes =
                leitorReclamacoes.extrairReclamacoes(ARQUIVO_RECLAMACOES);

        if (voos.isEmpty() || reclamacoes.isEmpty()) {
            System.out.println("Importacao cancelada: verifique as planilhas.");
            return;
        }

        System.out.println("Amostra dos voos lidos:");
        voos.stream().limit(3).forEach(System.out::println);

        try (Connection conexao = ConexaoBanco.conectar()) {

            conexao.setAutoCommit(false);

            try {
                System.out.println("Conexao com MySQL realizada!");

                ImportadorBanco importador = new ImportadorBanco(conexao);

                System.out.println("Importando voos...");
                importador.importarVoos(voos);

                System.out.println("Importando reclamacoes...");
                importador.importarReclamacoes(reclamacoes);
                LocalDateTime dataHoraFim = LocalDateTime.now();

                System.out.println("Salvando log de importação...");
                importador.salvarLog(
                        ARQUIVO_VOOS,
                        voos.size(),
                        ARQUIVO_RECLAMACOES,
                        reclamacoes.size(),
                        dataHoraInicio,
                        dataHoraFim
                );

                conexao.commit();

                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                System.out.println("----------------------------");
                System.out.println("IMPORTACAO FINALIZADA!");
                System.out.println("Aplicação iniciada em: " + dataHoraInicio.format(formatador));
                System.out.println("Aplicação finalizada em: " + dataHoraFim.format(formatador));
                System.out.println("----------------------------");

            } catch (Exception e) {
                conexao.rollback();
                System.out.println("Erro na importacao: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
