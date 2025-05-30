package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateKeywordRequestDTO {

    @NotBlank(message = "Palavra-chave atual não pode ser vazia.")
    private String palavraChaveAtual;

    @NotBlank(message = "Nova palavra-chave não pode ser vazia.")
    @Size(min = 4, message = "Nova palavra-chave deve ter no mínimo 4 caracteres.")
    private String novaPalavraChave;

    // Construtor padrão (opcional, mas boa prática)
    public UpdateKeywordRequestDTO() {
    }

    // Construtor com todos os campos (opcional)
    public UpdateKeywordRequestDTO(String palavraChaveAtual, String novaPalavraChave) {
        this.palavraChaveAtual = palavraChaveAtual;
        this.novaPalavraChave = novaPalavraChave;
    }

    // Getters e Setters
    public String getPalavraChaveAtual() {
        return palavraChaveAtual;
    }

    public void setPalavraChaveAtual(String palavraChaveAtual) {
        this.palavraChaveAtual = palavraChaveAtual;
    }

    public String getNovaPalavraChave() {
        return novaPalavraChave;
    }

    public void setNovaPalavraChave(String novaPalavraChave) {
        this.novaPalavraChave = novaPalavraChave;
    }
}