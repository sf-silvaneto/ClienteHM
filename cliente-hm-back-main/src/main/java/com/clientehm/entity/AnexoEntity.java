package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anexos")
public class AnexoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeOriginalArquivo; // Nome original do arquivo enviado

    @Column(nullable = false)
    private String tipoConteudo; // Ex: "application/pdf", "image/jpeg"

    @Column(nullable = false, unique = true) // O nome armazenado deve ser único para evitar colisões
    private String nomeArquivoArmazenado; // Nome do arquivo como está salvo no servidor/storage

    private Long tamanhoBytes; // Tamanho do arquivo em bytes

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataUpload;

    // Você pode adicionar relacionamentos aqui se um anexo pertencer
    // diretamente a uma entidade específica. Por exemplo, a um Exame:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "exame_id") // Supondo que ExameEntity tenha um @OneToMany para AnexoEntity
    // private ExameEntity exame;

    @PrePersist
    protected void onCreate() {
        dataUpload = LocalDateTime.now();
    }

    // Construtores
    public AnexoEntity() {
    }

    public AnexoEntity(String nomeOriginalArquivo, String tipoConteudo, String nomeArquivoArmazenado, Long tamanhoBytes) {
        this.nomeOriginalArquivo = nomeOriginalArquivo;
        this.tipoConteudo = tipoConteudo;
        this.nomeArquivoArmazenado = nomeArquivoArmazenado;
        this.tamanhoBytes = tamanhoBytes;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeOriginalArquivo() {
        return nomeOriginalArquivo;
    }

    public void setNomeOriginalArquivo(String nomeOriginalArquivo) {
        this.nomeOriginalArquivo = nomeOriginalArquivo;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public String getNomeArquivoArmazenado() {
        return nomeArquivoArmazenado;
    }

    public void setNomeArquivoArmazenado(String nomeArquivoArmazenado) {
        this.nomeArquivoArmazenado = nomeArquivoArmazenado;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    // Getter e Setter para 'exame' se o relacionamento for adicionado
    // public ExameEntity getExame() {
    //     return exame;
    // }
    //
    // public void setExame(ExameEntity exame) {
    //     this.exame = exame;
    // }
}