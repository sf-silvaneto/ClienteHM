package com.clientehm.model;

import com.clientehm.entity.StatusMedico;
import java.time.LocalDateTime;

public class MedicoDTO {
    private Long id;
    private String nomeCompleto;
    private String crm;
    private String especialidade;
    private String resumoEspecialidade;
    private String rqe;
    private StatusMedico status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicoDTO() {
    }

    public MedicoDTO(Long id, String nomeCompleto, String crm, String especialidade, String resumoEspecialidade, String rqe, StatusMedico status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.crm = crm;
        this.especialidade = especialidade;
        this.resumoEspecialidade = resumoEspecialidade;
        this.rqe = rqe;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public StatusMedico getStatus() {
        return status;
    }

    public void setStatus(StatusMedico status) {
        this.status = status;
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