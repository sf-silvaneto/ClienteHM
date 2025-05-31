package com.clientehm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PacienteUpdateDTO {

    @Size(min = 3, message = "Nome do paciente deve ter no mínimo 3 caracteres")
    private String nome;

    @PastOrPresent(message = "Data de nascimento deve ser no passado ou presente")
    private LocalDate dataNascimento;

    // CPF geralmente não é alterável. Se for, precisa de validação cuidadosa.
    // private String cpf;

    @Pattern(regexp = "^[0-9]{5,15}$", message = "RG deve conter entre 5 e 15 dígitos (apenas números)")
    private String rg;

    private String genero; // MASCULINO, FEMININO, OUTRO, NAO_INFORMADO

    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter 10 ou 11 dígitos")
    private String telefone;

    @Email(message = "Formato de email inválido")
    private String email;

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
    private EnderecoUpdateDTO endereco;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
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
    public EnderecoUpdateDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoUpdateDTO endereco) { this.endereco = endereco; }
}