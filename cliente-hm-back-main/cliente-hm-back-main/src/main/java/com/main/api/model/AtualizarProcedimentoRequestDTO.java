package com.main.api.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AtualizarProcedimentoRequestDTO {

    private LocalDateTime dataProcedimento;

    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricaoProcedimento;

    @Size(max = 10000, message = "Relatório não pode exceder 10000 caracteres")
    private String relatorioProcedimento;

    private Long medicoExecutorId;

    public LocalDateTime getDataProcedimento() { return dataProcedimento; }
    public void setDataProcedimento(LocalDateTime dataProcedimento) { this.dataProcedimento = dataProcedimento; }

    public String getDescricaoProcedimento() {
        return descricaoProcedimento;
    }

    public void setDescricaoProcedimento(String descricaoProcedimento) {
        this.descricaoProcedimento = descricaoProcedimento;
    }

    public String getRelatorioProcedimento() {
        return relatorioProcedimento;
    }

    public void setRelatorioProcedimento(String relatorioProcedimento) {
        this.relatorioProcedimento = relatorioProcedimento;
    }

    public Long getMedicoExecutorId() {
        return medicoExecutorId;
    }

    public void setMedicoExecutorId(Long medicoExecutorId) {
        this.medicoExecutorId = medicoExecutorId;
    }
}