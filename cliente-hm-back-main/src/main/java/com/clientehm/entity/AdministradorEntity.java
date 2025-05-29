package com.clientehm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "administradores")
public class AdministradorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha; // Será armazenada codificada
    private String palavraChave;

    public AdministradorEntity() {
    }

    public AdministradorEntity(String nome, String email, String senha, String palavraChave) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.palavraChave = palavraChave;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPalavraChave() {
        return palavraChave;
    }

    public void setPalavraChave(String palavraChave) {
        this.palavraChave = palavraChave;
    }
}