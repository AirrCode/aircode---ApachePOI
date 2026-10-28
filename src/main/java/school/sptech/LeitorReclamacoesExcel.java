package school.sptech;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LeitorReclamacoesExcel {

    // A primeira linha da planilha do consumidor.gov traz "Atualizado em: ...",
    // entao o cabecalho real fica na linha de indice 1.
    private static final int LINHA_CABECALHO = 1;

    private final DataFormatter formatador = new DataFormatter();
    private final Map<String, Integer> colunas = new HashMap<>();

    public List<Reclamacao> extrairReclamacoes(String nomeArquivo) {

        List<Reclamacao> reclamacoesExtraidas = new ArrayList<>();

        IOUtils.setByteArrayMaxOverride(200_000_000);

        try (
                InputStream arquivo = new FileInputStream(nomeArquivo);
                Workbook workbook = new XSSFWorkbook(arquivo)
        ) {
            System.out.println("Iniciando leitura do arquivo " + nomeArquivo);

            Sheet sheet = workbook.getSheetAt(0);
            Integer ignoradas = 0;

            for (Row row : sheet) {

                if (row.getRowNum() < LINHA_CABECALHO) {
                    continue;
                }

                if (row.getRowNum() == LINHA_CABECALHO) {
                    mapearCabecalho(row);
                    continue;
                }

                Reclamacao reclamacao = montarReclamacao(row);

                if (reclamacao == null) {
                    ignoradas++;
                    continue;
                }

                reclamacoesExtraidas.add(reclamacao);
            }

            System.out.println("-".repeat(50));
            System.out.println("Reclamacoes extraidas: " + reclamacoesExtraidas.size());
            System.out.println("Linhas ignoradas: " + ignoradas);
            System.out.println("-".repeat(50));

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de reclamacoes: " + e.getMessage());
        }

        return reclamacoesExtraidas;
    }

    private void mapearCabecalho(Row row) {

        colunas.clear();

        for (Cell cell : row) {
            String titulo = chave(formatador.formatCellValue(cell));

            if (!titulo.isBlank()) {
                colunas.put(titulo, cell.getColumnIndex());
            }
        }

        System.out.println("Colunas encontradas: " + colunas.size());
    }

    // Remove acentos e padroniza o titulo da coluna para a busca nao depender
    // de como a planilha escreveu o cabecalho.
    private String chave(String texto) {

        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .trim();
    }

    private Reclamacao montarReclamacao(Row row) {

        String empresaNome = texto(row, "NOME FANTASIA");
        String dataAbertura = data(row, "DATA ABERTURA");

        if (empresaNome == null || dataAbertura == null) {
            return null;
        }

        Reclamacao reclamacao = new Reclamacao();

        reclamacao.setEmpresaNome(empresaNome);
        reclamacao.setUfConsumidor(uf(row));
        reclamacao.setCidadeConsumidor(texto(row, "CIDADE"));
        reclamacao.setAnoAbertura(inteiro(row, "ANO ABERTURA"));
        reclamacao.setMesAbertura(inteiro(row, "MES ABERTURA"));
        reclamacao.setDataAbertura(dataAbertura);
        reclamacao.setDataFinalizacao(data(row, "DATA FINALIZACAO"));
        reclamacao.setTempoRespostaDias(inteiro(row, "TEMPO RESPOSTA (EM DIAS)"));
        reclamacao.setGrupoProblema(texto(row, "GRUPO PROBLEMA"));
        reclamacao.setProblema(texto(row, "PROBLEMA"));
        reclamacao.setRespondida(texto(row, "RESPONDIDA"));
        reclamacao.setSituacao(texto(row, "SITUACAO"));
        reclamacao.setAvaliacaoReclamacao(texto(row, "AVALIACAO RECLAMACAO"));
        reclamacao.setNotaConsumidor(inteiro(row, "NOTA DO CONSUMIDOR"));

        return reclamacao;
    }

    private String uf(Row row) {

        String valor = texto(row, "UF");

        if (valor == null || valor.length() != 2) {
            return null;
        }

        return valor.toUpperCase(Locale.ROOT);
    }

    private String texto(Row row, String coluna) {

        Integer indice = colunas.get(coluna);

        if (indice == null) {
            return null;
        }

        Cell cell = row.getCell(indice);

        if (cell == null) {
            return null;
        }

        String valor = formatador.formatCellValue(cell).trim();

        if (valor.isBlank() || valor.equalsIgnoreCase("null")) {
            return null;
        }

        return valor;
    }

    private Integer inteiro(Row row, String coluna) {

        String valor = texto(row, coluna);

        if (valor == null) {
            return null;
        }

        try {
            return (int) Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String data(Row row, String coluna) {

        Integer indice = colunas.get(coluna);

        if (indice == null) {
            return null;
        }

        Cell cell = row.getCell(indice);

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate().toString();
        }

        return texto(row, coluna);
    }
}
