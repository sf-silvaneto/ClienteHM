package com.clientehm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PacienteDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String rg;
    private String genero;
    private String telefone;
    private String email;
    private String nomeMae;
    private String nomePai;
    private LocalDate dataEntrada;
    private String cartaoSus;
    private String racaCor;
    private String tipoSanguineo;
    private String nacionalidade;
    private String ocupacao;

    private List<AlergiaDTO> alergias;
    private List<ComorbidadeDTO> comorbidades;
    private List<MedicamentoContinuoDTO> medicamentosContinuos;

    private EnderecoDTO endereco;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }
    public String getNomePai() { return nomePai; }
    public void setNomePai(String nomePai) { this.nomePai = nomePai; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
    public String getCartaoSus() { return cartaoSus; }
    public void setCartaoSus(String cartaoSus) { this.cartaoSus = cartaoSus; }
    public String getRacaCor() { return racaCor; }
    public void setRacaCor(String racaCor) { this.racaCor = racaCor; }
    public String getTipoSanguineo() { return tipoSanguineo; }
    public void setTipoSanguineo(String tipoSanguineo) { this.tipoSanguineo = tipoSanguineo; }
    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }
    public String getOcupacao() { return ocupacao; }
    public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }

    public List<AlergiaDTO> getAlergias() {
        return alergias;
    }

    public void setAlergias(List<AlergiaDTO> alergias) {
        this.alergias = alergias;
    }

    public List<ComorbidadeDTO> getComorbidades() {
        return comorbidades;
    }

    public void setComorbidades(List<ComorbidadeDTO> comorbidades) {
        this.comorbidades = comorbidades;
    }

    public List<MedicamentoContinuoDTO> getMedicamentosContinuos() {
        return medicamentosContinuos;
    }

    public void setMedicamentosContinuos(List<MedicamentoContinuoDTO> medicamentosContinuos) {
        this.medicamentosContinuos = medicamentosContinuos;
    }

    public EnderecoDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}