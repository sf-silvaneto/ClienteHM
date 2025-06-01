package com.clientehm.model;

import java.time.LocalDateTime;

public class AnexoDTO {
    private Long id;
    private String nomeOriginalArquivo;
    private String nomeArquivoArmazenado; // No frontend, isso pode se tornar uma URL de download/visualização
    private String tipoConteudo;
    private Long tamanhoBytes;
    private LocalDateTime dataUpload;

    // Construtores
    public AnexoDTO() {
    }

    public AnexoDTO(Long id, String nomeOriginalArquivo, String nomeArquivoArmazenado, String tipoConteudo, Long tamanhoBytes, LocalDateTime dataUpload) {
        this.id = id;
        this.nomeOriginalArquivo = nomeOriginalArquivo;
        this.nomeArquivoArmazenado = nomeArquivoArmazenado;
        this.tipoConteudo = tipoConteudo;
        this.tamanhoBytes = tamanhoBytes;
        this.dataUpload = dataUpload;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeOriginalArquivo() { return nomeOriginalArquivo; }
    public void setNomeOriginalArquivo(String nomeOriginalArquivo) { this.nomeOriginalArquivo = nomeOriginalArquivo; }
    public String getNomeArquivoArmazenado() { return nomeArquivoArmazenado; }
    public void setNomeArquivoArmazenado(String nomeArquivoArmazenado) { this.nomeArquivoArmazenado = nomeArquivoArmazenado; }
    public String getTipoConteudo() { return tipoConteudo; }
    public void setTipoConteudo(String tipoConteudo) { this.tipoConteudo = tipoConteudo; }
    public Long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(Long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }
}