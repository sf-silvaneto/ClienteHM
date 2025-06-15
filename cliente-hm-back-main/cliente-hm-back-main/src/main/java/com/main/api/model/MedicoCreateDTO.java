package com.main.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MedicoCreateDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, message = "Nome completo deve ter no mínimo 3 caracteres")
    private String nomeCompleto;

    @NotBlank(message = "CRM é obrigatório")
    @Size(min = 4, message = "CRM inválido")
    private String crm;

    @NotBlank(message = "Especialidade é obrigatória")
    private String especialidade;

    @Size(max = 1000, message = "Resumo da especialidade não pode exceder 1000 caracteres")
    private String resumoEspecialidade;

    private String rqe;

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
}