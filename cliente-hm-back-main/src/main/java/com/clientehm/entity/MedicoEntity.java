package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicos")
public class MedicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String crm;

    @Column(nullable = false)
    private String especialidade;

    @Column(columnDefinition = "TEXT")
    private String resumoEspecialidade;

    private String rqe;

    @Column(name = "excluded_at")
    private LocalDateTime excludedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        this.excludedAt = null;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MedicoEntity() {
    }

    public MedicoEntity(String nomeCompleto, String crm, String especialidade, String resumoEspecialidade, String rqe, LocalDateTime excludedAt) {
        this.nomeCompleto = nomeCompleto;
        this.crm = crm;
        this.especialidade = especialidade;
        this.resumoEspecialidade = resumoEspecialidade;
        this.rqe = rqe;
        this.excludedAt = excludedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getResumoEspecialidade() {
        return resumoEspecialidade;
    }

    public void setResumoEspecialidade(String resumoEspecialidade) {
        this.resumoEspecialidade = resumoEspecialidade;
    }

    public String getRqe() {
        return rqe;
    }

    public void setRqe(String rqe) {
        this.rqe = rqe;
    }

    public LocalDateTime getExcludedAt() {
        return excludedAt;
    }

    public void setExcludedAt(LocalDateTime excludedAt) {
        this.excludedAt = excludedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}