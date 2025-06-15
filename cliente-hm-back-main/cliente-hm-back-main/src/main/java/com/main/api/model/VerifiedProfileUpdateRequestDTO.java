package com.main.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class VerifiedProfileUpdateRequestDTO {

    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres, se fornecido.")
    private String nome;

    @Email(message = "Formato de email inválido, se fornecido.")
    private String email;

    @Size(min = 4, message = "Nova palavra-chave deve ter no mínimo 4 caracteres, se fornecida.")
    private String novaPalavraChave;

    public VerifiedProfileUpdateRequestDTO() {}

    public VerifiedProfileUpdateRequestDTO(String nome, String email, String novaPalavraChave) {
        this.nome = nome;
        this.email = email;
        this.novaPalavraChave = novaPalavraChave;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNovaPalavraChave() { return novaPalavraChave; }
    public void setNovaPalavraChave(String novaPalavraChave) { this.novaPalavraChave = novaPalavraChave; }
}