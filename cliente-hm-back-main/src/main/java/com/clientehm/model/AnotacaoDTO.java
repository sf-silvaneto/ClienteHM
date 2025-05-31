package com.clientehm.model;

public class AnotacaoDTO {
    private Long id;
    private String data; // Ex: "YYYY-MM-DDTHH:mm:ss" ou apenas data "YYYY-MM-DD"
    private String texto;
    private String responsavel;
    private String createdAt;
    private String updatedAt;

    // Construtores
    public AnotacaoDTO() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}