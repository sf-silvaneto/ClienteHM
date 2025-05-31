package com.clientehm.model;

public class ExameDTO {
    private Long id;
    private String nome;
    private String data; // Ex: "YYYY-MM-DD"
    private String resultado;
    private String arquivoUrl; // URL para o anexo, se houver. No frontend é 'arquivo?: string'
    private String observacoes; // Opcional
    private String createdAt;
    private String updatedAt;

    // Construtores
    public ExameDTO() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getArquivoUrl() { return arquivoUrl; }
    public void setArquivoUrl(String arquivoUrl) { this.arquivoUrl = arquivoUrl; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}