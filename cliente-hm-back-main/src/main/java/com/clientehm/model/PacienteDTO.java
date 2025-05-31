package com.clientehm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PacienteDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento; // Alterado para LocalDate
    private String cpf;
    private String genero;
    private String telefone;
    private String email;
    private EnderecoDTO endereco; // EnderecoDTO permanece como antes
    private LocalDateTime createdAt; // Alterado para LocalDateTime
    private LocalDateTime updatedAt; // Alterado para LocalDateTime

    // Construtores
    public PacienteDTO() {
    }

    // Getters e Setters atualizados para os novos tipos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public EnderecoDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}