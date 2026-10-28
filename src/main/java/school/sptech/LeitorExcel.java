package school.sptech;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LeitorExcel {

    private final DataFormatter formatador = new DataFormatter();
    private final Map<String, Integer> colunas = new HashMap<>();

    public List<Voo> extrairVoos(String nomeArquivo) {

        List<Voo> voosExtraidos = new ArrayList<>();

        IOUtils.setByteArrayMaxOverride(200_000_000);

        try (
                InputStream arquivo = new FileInputStream(nomeArquivo);
                Workbook workbook = new XSSFWorkbook(arquivo)
        ) {
            System.out.printf("Iniciando leitura do arquivo %s%n", nomeArquivo);

            Sheet sheet = workbook.getSheetAt(0);
            Integer ignoradas = 0;

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    mapearCabecalho(row);
                    continue;
                }

                Voo voo = montarVoo(row);

                if (voo == null) {
                    ignoradas++;
                    continue;
                }

                voosExtraidos.add(voo);
            }

            System.out.println("-".repeat(50));
            System.out.println("Voos extraidos: " + voosExtraidos.size());
            System.out.println("Linhas ignoradas: " + ignoradas);
            System.out.println("-".repeat(50));

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de voos: " + e.getMessage());
        }

        return voosExtraidos;
    }

    // Guarda a posicao de cada coluna pelo nome do cabecalho, para a leitura
    // nao quebrar se a ordem das colunas da planilha mudar.
    private void mapearCabecalho(Row row) {

        colunas.clear();

        for (Cell cell : row) {
            String titulo = formatador.formatCellValue(cell).trim().toUpperCase();

            if (!titulo.isBlank()) {
                colunas.put(titulo, cell.getColumnIndex());
            }
        }

        System.out.println("Colunas encontradas: " + colunas.size());
    }

    private Voo montarVoo(Row row) {

        String origemSigla = texto(row, "ORIGEM_SIGLA");
        String destinoSigla = texto(row, "DESTINO_SIGLA");
        Integer ano = inteiro(row, "ANO");
        Integer mes = inteiro(row, "MES");
        String empresaNome = texto(row, "EMPRESA_NOME");

        boolean incompleto = origemSigla == null
                || destinoSigla == null
                || empresaNome == null
                || ano == null
                || mes == null;

        if (incompleto) {
            return null;
        }

        Voo voo = new Voo();

        voo.setEmpresaSigla(texto(row, "EMPRESA_SIGLA"));
        voo.setEmpresaNome(empresaNome);
        voo.setEmpresaNacionalidade(texto(row, "EMPRESA_NACIONALIDADE"));
        voo.setAno(ano);
        voo.setMes(mes);

        voo.setOrigemSigla(origemSigla.toUpperCase());
        voo.setOrigemNome(texto(row, "ORIGEM_NOME"));
        voo.setOrigemUf(texto(row, "ORIGEM_UF"));
        voo.setOrigemEstado(texto(row, "ORIGEM_ESTADO"));
        voo.setOrigemRegiao(texto(row, "ORIGEM_REGIAO"));
        voo.setOrigemPais(texto(row, "ORIGEM_PAIS"));
        voo.setOrigemContinente(texto(row, "ORIGEM_CONTINENTE"));

        voo.setDestinoSigla(destinoSigla.toUpperCase());
        voo.setDestinoNome(texto(row, "DESTINO_NOME"));
        voo.setDestinoUf(texto(row, "DESTINO_UF"));
        voo.setDestinoEstado(texto(row, "DESTINO_ESTADO"));
        voo.setDestinoRegiao(texto(row, "DESTINO_REGIAO"));
        voo.setDestinoPais(texto(row, "DESTINO_PAIS"));
        voo.setDestinoContinente(texto(row, "DESTINO_CONTINENTE"));

        voo.setNatureza(texto(row, "NATUREZA"));
        voo.setGrupoVoo(texto(row, "GRUPO_DE_VOO"));
        voo.setPassageirosPagos(inteiroOuZero(row, "PASSAGEIROS_PAGOS"));
        voo.setPassageirosGratis(inteiroOuZero(row, "PASSAGEIROS_GRATIS"));
        voo.setAssentosOfertados(inteiroOuZero(row, "ASSENTOS"));

        return voo;
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
            return (int) Double.parseDouble(valor.replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer inteiroOuZero(Row row, String coluna) {

        Integer valor = inteiro(row, coluna);

        return valor == null ? 0 : valor;
    }
}
