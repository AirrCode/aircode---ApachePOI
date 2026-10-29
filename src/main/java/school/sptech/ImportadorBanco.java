package school.sptech;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportadorBanco {

    private static final int TAMANHO_LOTE = 500;

    private final Connection conexao;

    private final Map<String, Integer> companhias = new HashMap<>();
    private final Map<String, Integer> aeroportos = new HashMap<>();
    private final Map<String, Integer> rotas = new HashMap<>();

    public ImportadorBanco(Connection conexao) throws SQLException {
        this.conexao = conexao;
        carregarCompanhias();
        carregarAeroportos();
        carregarRotas();
    }

    private void carregarCompanhias() throws SQLException {

        String sql = """
                SELECT id_companhia, nome_empresa, nome_fantasia_consumidor
                FROM companhia
                """;

        try (
                Statement statement = conexao.createStatement();
                ResultSet resultado = statement.executeQuery(sql)
        ) {
            while (resultado.next()) {

                Integer id = resultado.getInt("id_companhia");

                for (String coluna : new String[]{"nome_empresa", "nome_fantasia_consumidor"}) {

                    String nome = resultado.getString(coluna);

                    if (nome != null) {
                        companhias.putIfAbsent(NormalizadorCompanhia.normalizar(nome), id);
                    }
                }
            }
        }
    }

    private void carregarAeroportos() throws SQLException {

        String sql = "SELECT id_aeroporto, sigla_icao_iata FROM aeroporto";

        try (
                Statement statement = conexao.createStatement();
                ResultSet resultado = statement.executeQuery(sql)
        ) {
            while (resultado.next()) {
                aeroportos.put(
                        resultado.getString("sigla_icao_iata"),
                        resultado.getInt("id_aeroporto")
                );
            }
        }
    }

    private void carregarRotas() throws SQLException {

        String sql = """
                SELECT id_rota, id_aeroporto_origem, id_aeroporto_destino, natureza
                FROM rota
                """;

        try (
                Statement statement = conexao.createStatement();
                ResultSet resultado = statement.executeQuery(sql)
        ) {
            while (resultado.next()) {

                String chave = resultado.getInt("id_aeroporto_origem")
                        + "-" + resultado.getInt("id_aeroporto_destino")
                        + "-" + resultado.getString("natureza");

                rotas.put(chave, resultado.getInt("id_rota"));
            }
        }
    }

    private Integer obterCompanhia(String nomeEmpresa, String sigla, String nacionalidade)
            throws SQLException {

        String chave = NormalizadorCompanhia.normalizar(nomeEmpresa);

        if (companhias.containsKey(chave)) {
            return companhias.get(chave);
        }

        String sql = """
                INSERT INTO companhia
                (sigla_icao, nome_empresa, nome_fantasia_consumidor, nacionalidade)
                VALUES (?, ?, ?, ?)
                """;

        try (
                PreparedStatement statement =
                        conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, sigla);
            statement.setString(2, nomeEmpresa);
            statement.setString(3, NormalizadorCompanhia.nomeComercial(nomeEmpresa));

            if (nacionalidade == null) {
                statement.setString(4, "BRASILEIRA");
            } else {
                statement.setString(4, nacionalidade);
            }

            statement.executeUpdate();

            try (ResultSet ids = statement.getGeneratedKeys()) {

                if (ids.next()) {
                    Integer id = ids.getInt(1);
                    companhias.put(chave, id);
                    return id;
                }
            }
        }

        throw new SQLException("Erro ao cadastrar companhia: " + nomeEmpresa);
    }

    private Integer obterAeroporto(
            String sigla,
            String nome,
            String uf,
            String estado,
            String regiao,
            String pais,
            String continente
    ) throws SQLException {

        if (sigla == null || sigla.isBlank()) {
            throw new SQLException("Aeroporto sem sigla.");
        }

        sigla = sigla.trim().toUpperCase();

        if (aeroportos.containsKey(sigla)) {
            return aeroportos.get(sigla);
        }

        String sql = """
                INSERT INTO aeroporto
                (sigla_icao_iata, nome_aeroporto, uf, nome_estado, regiao, pais, continente)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement statement =
                        conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, sigla);
            statement.setString(2, nome == null ? sigla : nome);
            statement.setString(3, uf);
            statement.setString(4, estado);
            statement.setString(5, regiao);
            statement.setString(6, pais == null ? "BRASIL" : pais);
            statement.setString(7, continente);

            statement.executeUpdate();

            try (ResultSet ids = statement.getGeneratedKeys()) {

                if (ids.next()) {
                    Integer id = ids.getInt(1);
                    aeroportos.put(sigla, id);
                    return id;
                }
            }
        }

        throw new SQLException("Erro ao cadastrar aeroporto: " + sigla);
    }

    private Integer obterRota(Voo voo) throws SQLException {

        Integer origem = obterAeroporto(
                voo.getOrigemSigla(),
                voo.getOrigemNome(),
                voo.getOrigemUf(),
                voo.getOrigemEstado(),
                voo.getOrigemRegiao(),
                voo.getOrigemPais(),
                voo.getOrigemContinente()
        );

        Integer destino = obterAeroporto(
                voo.getDestinoSigla(),
                voo.getDestinoNome(),
                voo.getDestinoUf(),
                voo.getDestinoEstado(),
                voo.getDestinoRegiao(),
                voo.getDestinoPais(),
                voo.getDestinoContinente()
        );

        String natureza = voo.getNatureza();
        String chave = origem + "-" + destino + "-" + natureza;

        if (rotas.containsKey(chave)) {
            return rotas.get(chave);
        }

        String sql = """
                INSERT INTO rota
                (id_aeroporto_origem, id_aeroporto_destino, natureza)
                VALUES (?, ?, ?)
                """;

        try (
                PreparedStatement statement =
                        conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, origem);
            statement.setInt(2, destino);
            statement.setString(3, natureza);

            statement.executeUpdate();

            try (ResultSet ids = statement.getGeneratedKeys()) {

                if (ids.next()) {
                    Integer id = ids.getInt(1);
                    rotas.put(chave, id);
                    return id;
                }
            }
        }

        throw new SQLException("Erro ao cadastrar rota: " + chave);
    }

    public void importarVoos(List<Voo> voos) throws SQLException {

        String sql = """
                INSERT INTO voo_mensal
                (id_companhia, id_rota, ano, mes, grupo_voo,
                 passageiros_pagos, passageiros_gratis, assentos_ofertados)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Integer enviados = 0;
        Integer ignorados = 0;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {

            for (Voo voo : voos) {

                if (voo.getNatureza() == null) {
                    ignorados++;
                    continue;
                }

                Integer idCompanhia = obterCompanhia(
                        voo.getEmpresaNome(),
                        voo.getEmpresaSigla(),
                        voo.getEmpresaNacionalidade()
                );

                Integer idRota = obterRota(voo);

                statement.setInt(1, idCompanhia);
                statement.setInt(2, idRota);
                statement.setInt(3, voo.getAno());
                statement.setInt(4, voo.getMes());
                statement.setString(5, voo.getGrupoVoo());
                statement.setInt(6, voo.getPassageirosPagos());
                statement.setInt(7, voo.getPassageirosGratis());
                statement.setInt(8, voo.getAssentosOfertados());

                statement.addBatch();
                enviados++;

                if (enviados % TAMANHO_LOTE == 0) {
                    statement.executeBatch();
                }
            }

            statement.executeBatch();
        }

        System.out.println("Voos enviados para o banco: " + enviados);
        System.out.println("Voos ignorados (sem natureza): " + ignorados);
    }

    private Date converterData(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        String[] formatos = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy HH:mm:ss"
        };

        for (String formato : formatos) {

            try {
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern(formato);

                if (formato.contains("HH:mm:ss")) {
                    return Date.valueOf(
                            java.time.LocalDateTime.parse(texto, formatador).toLocalDate()
                    );
                }

                return Date.valueOf(LocalDate.parse(texto, formatador));

            } catch (DateTimeParseException e) {
            }
        }

        return null;
    }

    public void importarReclamacoes(List<Reclamacao> reclamacoes) throws SQLException {

        String sql = """
                INSERT INTO reclamacao
                (id_companhia, uf_consumidor, cidade_consumidor,
                 data_abertura, data_finalizacao, tempo_resposta_dias,
                 grupo_problema, problema, avaliacao_reclamacao, nota_consumidor)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Integer enviadas = 0;
        Integer ignoradas = 0;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {

            for (Reclamacao reclamacao : reclamacoes) {

                Date abertura = converterData(reclamacao.getDataAbertura());

                if (abertura == null
                        || reclamacao.getGrupoProblema() == null
                        || reclamacao.getProblema() == null) {
                    ignoradas++;
                    continue;
                }

                Integer idCompanhia = obterCompanhia(
                        reclamacao.getEmpresaNome(), null, null
                );

                statement.setInt(1, idCompanhia);
                statement.setString(2, reclamacao.getUfConsumidor());
                statement.setString(3, reclamacao.getCidadeConsumidor());
                statement.setDate(4, abertura);

                Date finalizacao = converterData(reclamacao.getDataFinalizacao());

                if (finalizacao == null) {
                    statement.setNull(5, java.sql.Types.DATE);
                } else {
                    statement.setDate(5, finalizacao);
                }

                if (reclamacao.getTempoRespostaDias() == null) {
                    statement.setNull(6, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(6, reclamacao.getTempoRespostaDias());
                }

                statement.setString(7, reclamacao.getGrupoProblema());
                statement.setString(8, reclamacao.getProblema());
                statement.setString(9, reclamacao.getAvaliacaoReclamacao());

                if (reclamacao.getNotaConsumidor() == null) {
                    statement.setNull(10, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(10, reclamacao.getNotaConsumidor());
                }

                statement.addBatch();
                enviadas++;

                if (enviadas % TAMANHO_LOTE == 0) {
                    statement.executeBatch();
                }
            }

            statement.executeBatch();
        }

        System.out.println("Reclamacoes enviadas para o banco: " + enviadas);
        System.out.println("Reclamacoes ignoradas (dados obrigatorios ausentes): " + ignoradas);
    }

    public void salvarLog(String arqVoos, int qtdVoos, String arqRec, int qtdRec, LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        String sql = """
            INSERT INTO log_importacao 
            (arquivo_voos, linhas_voos, arquivo_reclamacoes, linhas_reclamacoes, data_hora_inicio, data_hora_fim) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, arqVoos);
            statement.setInt(2, qtdVoos);
            statement.setString(3, arqRec);
            statement.setInt(4, qtdRec);
            statement.setObject(5, inicio);
            statement.setObject(6, fim);

            statement.executeUpdate();
            System.out.println("Log de importação salvo no banco de dados com sucesso!");
        }
    }

}
