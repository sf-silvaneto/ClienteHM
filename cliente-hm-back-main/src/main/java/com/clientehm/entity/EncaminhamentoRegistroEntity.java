package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_encaminhamentos")
public class EncaminhamentoRegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private ProntuarioEntity prontuario;

    @Column(nullable = false)
    private LocalDateTime dataEncaminhamento;

    @Column(nullable = false)
    private String especialidadeDestino;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivoEncaminhamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_solicitante_id", nullable = false)
    private MedicoEntity medicoSolicitante;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    private String nomeResponsavelDisplay;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (this.dataEncaminhamento == null) this.dataEncaminhamento = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProntuarioEntity getProntuario() { return prontuario; }
    public void setProntuario(ProntuarioEntity prontuario) { this.prontuario = prontuario; }
    public LocalDateTime getDataEncaminhamento() { return dataEncaminhamento; }
    public void setDataEncaminhamento(LocalDateTime dataEncaminhamento) { this.dataEncaminhamento = dataEncaminhamento; }
    public String getEspecialidadeDestino() { return especialidadeDestino; }
    public void setEspecialidadeDestino(String especialidadeDestino) { this.especialidadeDestino = especialidadeDestino; }
    public String getMotivoEncaminhamento() { return motivoEncaminhamento; }
    public void setMotivoEncaminhamento(String motivoEncaminhamento) { this.motivoEncaminhamento = motivoEncaminhamento; }
    public MedicoEntity getMedicoSolicitante() { return medicoSolicitante; }
    public void setMedicoSolicitante(MedicoEntity medicoSolicitante) { this.medicoSolicitante = medicoSolicitante; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; }
    public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}