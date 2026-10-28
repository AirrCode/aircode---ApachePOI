package school.sptech;

public class Reclamacao {

    private String empresaNome;
    private String empresaChave;
    private String ufConsumidor;
    private String cidadeConsumidor;
    private Integer anoAbertura;
    private Integer mesAbertura;
    private String dataAbertura;
    private String dataFinalizacao;
    private Integer tempoRespostaDias;
    private String grupoProblema;
    private String problema;
    private String respondida;
    private String situacao;
    private String avaliacaoReclamacao;
    private Integer notaConsumidor;

    public Reclamacao() {
    }

    public Reclamacao(
            String empresaNome,
            Integer anoAbertura,
            Integer mesAbertura,
            String dataAbertura,
            String dataFinalizacao,
            Integer tempoRespostaDias,
            String grupoProblema,
            String problema,
            String respondida,
            String situacao,
            String avaliacaoReclamacao,
            Integer notaConsumidor
    ) {
        this.empresaNome = empresaNome;
        this.empresaChave =
                NormalizadorCompanhia.normalizar(empresaNome);
        this.anoAbertura = anoAbertura;
        this.mesAbertura = mesAbertura;
        this.dataAbertura = dataAbertura;
        this.dataFinalizacao = dataFinalizacao;
        this.tempoRespostaDias = tempoRespostaDias;
        this.grupoProblema = grupoProblema;
        this.problema = problema;
        this.respondida = respondida;
        this.situacao = situacao;
        this.avaliacaoReclamacao = avaliacaoReclamacao;
        this.notaConsumidor = notaConsumidor;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public String getEmpresaChave() {
        return empresaChave;
    }

    public String getUfConsumidor() {
        return ufConsumidor;
    }

    public void setUfConsumidor(String ufConsumidor) {
        this.ufConsumidor = ufConsumidor;
    }

    public String getCidadeConsumidor() {
        return cidadeConsumidor;
    }

    public void setCidadeConsumidor(String cidadeConsumidor) {
        this.cidadeConsumidor = cidadeConsumidor;
    }

    public Integer getAnoAbertura() {
        return anoAbertura;
    }

    public Integer getMesAbertura() {
        return mesAbertura;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public String getDataFinalizacao() {
        return dataFinalizacao;
    }

    public Integer getTempoRespostaDias() {
        return tempoRespostaDias;
    }

    public String getGrupoProblema() {
        return grupoProblema;
    }

    public String getProblema() {
        return problema;
    }

    public String getRespondida() {
        return respondida;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getAvaliacaoReclamacao() {
        return avaliacaoReclamacao;
    }

    public Integer getNotaConsumidor() {
        return notaConsumidor;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
        this.empresaChave =
                NormalizadorCompanhia.normalizar(empresaNome);
    }

    public void setEmpresaChave(String empresaChave) {
        this.empresaChave = empresaChave;
    }

    public void setAnoAbertura(Integer anoAbertura) {
        this.anoAbertura = anoAbertura;
    }

    public void setMesAbertura(Integer mesAbertura) {
        this.mesAbertura = mesAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public void setDataFinalizacao(String dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public void setTempoRespostaDias(Integer tempoRespostaDias) {
        this.tempoRespostaDias = tempoRespostaDias;
    }

    public void setGrupoProblema(String grupoProblema) {
        this.grupoProblema = grupoProblema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public void setRespondida(String respondida) {
        this.respondida = respondida;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public void setAvaliacaoReclamacao(
            String avaliacaoReclamacao
    ) {
        this.avaliacaoReclamacao = avaliacaoReclamacao;
    }

    public void setNotaConsumidor(Integer notaConsumidor) {
        this.notaConsumidor = notaConsumidor;
    }

    @Override
    public String toString() {
        return "Reclamacao{" +
                "empresaNome='" + empresaNome + '\'' +
                ", empresaChave='" + empresaChave + '\'' +
                ", anoAbertura=" + anoAbertura +
                ", mesAbertura=" + mesAbertura +
                ", dataAbertura='" + dataAbertura + '\'' +
                ", dataFinalizacao='" + dataFinalizacao + '\'' +
                ", tempoRespostaDias=" + tempoRespostaDias +
                ", grupoProblema='" + grupoProblema + '\'' +
                ", problema='" + problema + '\'' +
                ", respondida='" + respondida + '\'' +
                ", situacao='" + situacao + '\'' +
                ", avaliacaoReclamacao='" +
                avaliacaoReclamacao + '\'' +
                ", notaConsumidor=" + notaConsumidor +
                '}';
    }
}