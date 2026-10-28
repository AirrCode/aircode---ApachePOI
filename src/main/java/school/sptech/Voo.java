package school.sptech;

public class Voo {

    private String empresaSigla;
    private String empresaNome;
    private String empresaChave;
    private String empresaNacionalidade;
    private Integer ano;
    private Integer mes;

    private String origemSigla;
    private String origemNome;
    private String origemUf;
    private String origemEstado;
    private String origemRegiao;
    private String origemPais;
    private String origemContinente;

    private String destinoSigla;
    private String destinoNome;
    private String destinoUf;
    private String destinoEstado;
    private String destinoRegiao;
    private String destinoPais;
    private String destinoContinente;

    private String natureza;
    private String grupoVoo;
    private Integer passageirosPagos;
    private Integer passageirosGratis;
    private Integer assentosOfertados;

    public Voo() {
    }

    public String getEmpresaSigla() {
        return empresaSigla;
    }

    public void setEmpresaSigla(String empresaSigla) {
        this.empresaSigla = empresaSigla;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
        this.empresaChave = NormalizadorCompanhia.normalizar(empresaNome);
    }

    public String getEmpresaChave() {
        return empresaChave;
    }

    public String getEmpresaNacionalidade() {
        return empresaNacionalidade;
    }

    public void setEmpresaNacionalidade(String empresaNacionalidade) {
        this.empresaNacionalidade = empresaNacionalidade;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getOrigemSigla() {
        return origemSigla;
    }

    public void setOrigemSigla(String origemSigla) {
        this.origemSigla = origemSigla;
    }

    public String getOrigemNome() {
        return origemNome;
    }

    public void setOrigemNome(String origemNome) {
        this.origemNome = origemNome;
    }

    public String getOrigemUf() {
        return origemUf;
    }

    public void setOrigemUf(String origemUf) {
        this.origemUf = origemUf;
    }

    public String getOrigemEstado() {
        return origemEstado;
    }

    public void setOrigemEstado(String origemEstado) {
        this.origemEstado = origemEstado;
    }

    public String getOrigemRegiao() {
        return origemRegiao;
    }

    public void setOrigemRegiao(String origemRegiao) {
        this.origemRegiao = origemRegiao;
    }

    public String getOrigemPais() {
        return origemPais;
    }

    public void setOrigemPais(String origemPais) {
        this.origemPais = origemPais;
    }

    public String getOrigemContinente() {
        return origemContinente;
    }

    public void setOrigemContinente(String origemContinente) {
        this.origemContinente = origemContinente;
    }

    public String getDestinoSigla() {
        return destinoSigla;
    }

    public void setDestinoSigla(String destinoSigla) {
        this.destinoSigla = destinoSigla;
    }

    public String getDestinoNome() {
        return destinoNome;
    }

    public void setDestinoNome(String destinoNome) {
        this.destinoNome = destinoNome;
    }

    public String getDestinoUf() {
        return destinoUf;
    }

    public void setDestinoUf(String destinoUf) {
        this.destinoUf = destinoUf;
    }

    public String getDestinoEstado() {
        return destinoEstado;
    }

    public void setDestinoEstado(String destinoEstado) {
        this.destinoEstado = destinoEstado;
    }

    public String getDestinoRegiao() {
        return destinoRegiao;
    }

    public void setDestinoRegiao(String destinoRegiao) {
        this.destinoRegiao = destinoRegiao;
    }

    public String getDestinoPais() {
        return destinoPais;
    }

    public void setDestinoPais(String destinoPais) {
        this.destinoPais = destinoPais;
    }

    public String getDestinoContinente() {
        return destinoContinente;
    }

    public void setDestinoContinente(String destinoContinente) {
        this.destinoContinente = destinoContinente;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getGrupoVoo() {
        return grupoVoo;
    }

    public void setGrupoVoo(String grupoVoo) {
        this.grupoVoo = grupoVoo;
    }

    public Integer getPassageirosPagos() {
        return passageirosPagos;
    }

    public void setPassageirosPagos(Integer passageirosPagos) {
        this.passageirosPagos = passageirosPagos;
    }

    public Integer getPassageirosGratis() {
        return passageirosGratis;
    }

    public void setPassageirosGratis(Integer passageirosGratis) {
        this.passageirosGratis = passageirosGratis;
    }

    public Integer getAssentosOfertados() {
        return assentosOfertados;
    }

    public void setAssentosOfertados(Integer assentosOfertados) {
        this.assentosOfertados = assentosOfertados;
    }

    @Override
    public String toString() {
        return "Voo{" +
                "empresa='" + empresaNome + '\'' +
                ", chave='" + empresaChave + '\'' +
                ", nacionalidade='" + empresaNacionalidade + '\'' +
                ", ano=" + ano +
                ", mes=" + mes +
                ", origem='" + origemSigla + " - " + origemNome + "/" + origemUf + '\'' +
                ", destino='" + destinoSigla + " - " + destinoNome + "/" + destinoUf + '\'' +
                ", natureza='" + natureza + '\'' +
                ", passageirosPagos=" + passageirosPagos +
                ", assentos=" + assentosOfertados +
                '}';
    }
}
