package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_exames")
public class ExameRegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private ProntuarioEntity prontuario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDateTime dataExame;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultado;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_responsavel_exame_id")
    private MedicoEntity medicoResponsavelExame;

    @Column(nullable = false)
    private String nomeResponsavelDisplay;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (this.dataExame == null) this.dataExame = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProntuarioEntity getProntuario() { return prontuario; }
    public void setProntuario(ProntuarioEntity prontuario) { this.prontuario = prontuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getDataExame() { return dataExame; }
    public void setDataExame(LocalDateTime dataExame) { this.dataExame = dataExame; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public MedicoEntity getMedicoResponsavelExame() { return medicoResponsavelExame; }
    public void setMedicoResponsavelExame(MedicoEntity medicoResponsavelExame) { this.medicoResponsavelExame = medicoResponsavelExame; }
    public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; }
    public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}