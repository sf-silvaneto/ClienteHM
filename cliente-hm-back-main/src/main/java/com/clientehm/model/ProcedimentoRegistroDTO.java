package com.clientehm.model;

import java.time.LocalDateTime;

public class ProcedimentoRegistroDTO {
    private Long id;
    private Long prontuarioId;
    private String descricaoProcedimento;
    private String relatorioProcedimento;
    private Long medicoExecutorId;
    private String medicoExecutorNome;
    private String medicoExecutorEspecialidade;
    private String medicoExecutorCRM;
    private LocalDateTime dataProcedimento;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProntuarioId() { return prontuarioId; }
    public void setProntuarioId(Long prontuarioId) { this.prontuarioId = prontuarioId; }
    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public void setDescricaoProcedimento(String descricaoProcedimento) { this.descricaoProcedimento = descricaoProcedimento; }
    public String getRelatorioProcedimento() { return relatorioProcedimento; }
    public void setRelatorioProcedimento(String relatorioProcedimento) { this.relatorioProcedimento = relatorioProcedimento; }
    public Long getMedicoExecutorId() { return medicoExecutorId; }
    public void setMedicoExecutorId(Long medicoExecutorId) { this.medicoExecutorId = medicoExecutorId; }
    public String getMedicoExecutorNome() { return medicoExecutorNome; }
    public void setMedicoExecutorNome(String medicoExecutorNome) { this.medicoExecutorNome = medicoExecutorNome; }
    public String getMedicoExecutorEspecialidade() { return medicoExecutorEspecialidade; }
    public void setMedicoExecutorEspecialidade(String medicoExecutorEspecialidade) { this.medicoExecutorEspecialidade = medicoExecutorEspecialidade; }
    public String getMedicoExecutorCRM() { return medicoExecutorCRM; }
    public void setMedicoExecutorCRM(String medicoExecutorCRM) { this.medicoExecutorCRM = medicoExecutorCRM; }
    public LocalDateTime getDataProcedimento() { return dataProcedimento; }
    public void setDataProcedimento(LocalDateTime dataProcedimento) { this.dataProcedimento = dataProcedimento; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}