package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_procedimentos")
public class ProcedimentoRegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private ProntuarioEntity prontuario;

    @Column(nullable = false)
    private LocalDateTime dataProcedimento;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricaoProcedimento;

    @Column(columnDefinition = "TEXT")
    private String relatorioProcedimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_executor_id", nullable = false)
    private MedicoEntity medicoExecutor;

    @Column(nullable = false)
    private String nomeResponsavelDisplay;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (this.dataProcedimento == null) this.dataProcedimento = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProntuarioEntity getProntuario() { return prontuario; }
    public void setProntuario(ProntuarioEntity prontuario) { this.prontuario = prontuario; }
    public LocalDateTime getDataProcedimento() { return dataProcedimento; }
    public void setDataProcedimento(LocalDateTime dataProcedimento) { this.dataProcedimento = dataProcedimento; }
    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public void setDescricaoProcedimento(String descricaoProcedimento) { this.descricaoProcedimento = descricaoProcedimento; }
    public String getRelatorioProcedimento() { return relatorioProcedimento; }
    public void setRelatorioProcedimento(String relatorioProcedimento) { this.relatorioProcedimento = relatorioProcedimento; }
    public MedicoEntity getMedicoExecutor() { return medicoExecutor; }
    public void setMedicoExecutor(MedicoEntity medicoExecutor) { this.medicoExecutor = medicoExecutor; }
    public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; }
    public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}