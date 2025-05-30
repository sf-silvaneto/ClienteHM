package com.clientehm.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdministradorLoginDTO {

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Senha não pode ser vazia")
    private String senha;

    // Getters e Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}