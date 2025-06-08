package com.clientehm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class PacienteCreateDTO {

    @NotBlank(message = "Nome do paciente é obrigatório")
    @Size(min = 3, message = "Nome do paciente deve ter no mínimo 3 caracteres")
    private String nome;

    @NotNull(message = "Data de nascimento é obrigatória")
    @PastOrPresent(message = "Data de nascimento deve ser no passado ou presente")
    private LocalDate dataNascimento;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @Pattern(regexp = "^[0-9]{1,9}$", message = "RG deve conter no máximo 9 dígitos (apenas números)")
    private String rg;

    @NotBlank(message = "Gênero é obrigatório")
    private String genero;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Nome da mãe é obrigatório")
    @Size(min = 3, message = "Nome da mãe deve ter no mínimo 3 caracteres")
    private String nomeMae;

    @Size(min = 3, message = "Nome do pai deve ter no mínimo 3 caracteres (se informado)")
    private String nomePai;

    private LocalDate dataEntrada;

    @Pattern(regexp = "^\\d{15}$", message = "Cartão SUS deve conter 15 dígitos (se informado)")
    private String cartaoSus;

    private String racaCor;
    private String tipoSanguineo;
    private String nacionalidade;
    private String ocupacao;

    @Valid
    private List<AlergiaDTO> alergias;

    @Valid
    private List<ComorbidadeDTO> comorbidades;

    @Valid
    private List<MedicamentoContinuoDTO> medicamentosContinuos;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoCreateDTO endereco;

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
    public EnderecoCreateDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoCreateDTO endereco) { this.endereco = endereco; }

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
}