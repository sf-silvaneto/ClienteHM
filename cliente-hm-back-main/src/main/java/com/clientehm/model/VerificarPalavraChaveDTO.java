package com.clientehm.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class VerificarPalavraChaveDTO {

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Palavra-chave não pode ser vazia")
    private String palavraChave;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPalavraChave() { return palavraChave; }
    public void setPalavraChave(String palavraChave) { this.palavraChave = palavraChave; }
}