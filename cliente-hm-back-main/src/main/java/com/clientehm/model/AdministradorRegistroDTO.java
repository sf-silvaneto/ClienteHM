package com.clientehm.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdministradorRegistroDTO {

    @NotBlank(message = "Nome não pode ser vazio")
    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
    private String nome;

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Formato de email inválido")
    @Pattern(regexp = "^[\\w.-]+@hm\\.com$", message = "Email deve ser do domínio @hm.com")
    private String email;

    @NotBlank(message = "Senha não pode ser vazia")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    // A validação de força da senha (isSenhaForte) será mantida no serviço/controller
    // por ser mais complexa que uma simples anotação, mas @Size já ajuda.
    private String senha;

    @NotBlank(message = "Palavra-chave não pode ser vazia")
    @Size(min = 4, message = "Palavra-chave deve ter no mínimo 4 caracteres")
    private String palavraChave;

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getPalavraChave() { return palavraChave; }
    public void setPalavraChave(String palavraChave) { this.palavraChave = palavraChave; }
}