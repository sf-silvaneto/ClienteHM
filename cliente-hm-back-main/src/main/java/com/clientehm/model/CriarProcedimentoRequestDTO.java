package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CriarProcedimentoRequestDTO {

    @NotBlank(message = "Descrição do procedimento é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String descricaoProcedimento;

    @Size(max = 10000, message = "Relatório não pode exceder 10000 caracteres")
    private String relatorioProcedimento;

    @NotNull(message = "ID do médico executor é obrigatório")
    private Long medicoExecutorId;

    public String getDescricaoProcedimento() { return descricaoProcedimento; }
    public void setDescricaoProcedimento(String descricaoProcedimento) { this.descricaoProcedimento = descricaoProcedimento; }
    public String getRelatorioProcedimento() { return relatorioProcedimento; }
    public void setRelatorioProcedimento(String relatorioProcedimento) { this.relatorioProcedimento = relatorioProcedimento; }
    public Long getMedicoExecutorId() { return medicoExecutorId; }
    public void setMedicoExecutorId(Long medicoExecutorId) { this.medicoExecutorId = medicoExecutorId; }
}