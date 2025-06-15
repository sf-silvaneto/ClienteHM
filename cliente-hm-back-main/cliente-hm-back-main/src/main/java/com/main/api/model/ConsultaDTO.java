package com.main.api.model;

import java.time.LocalDateTime;

public class ConsultaDTO {
    private Long id;
    private Long prontuarioId;
    private String motivoConsulta;
    private String queixasPrincipais;

    private SinaisVitaisDTO sinaisVitais;

    private String exameFisico;
    private String hipoteseDiagnostica;
    private String condutaPlanoTerapeutico;
    private String detalhesConsulta;
    private String observacoesConsulta;
    private Long responsavelId;
    private String responsavelNomeCompleto;
    private String responsavelEspecialidade;
    private String responsavelCRM;

    private LocalDateTime dataConsulta;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProntuarioId() { return prontuarioId; }
    public void setProntuarioId(Long prontuarioId) { this.prontuarioId = prontuarioId; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
    public String getQueixasPrincipais() { return queixasPrincipais; }
    public void setQueixasPrincipais(String queixasPrincipais) { this.queixasPrincipais = queixasPrincipais; }

    public SinaisVitaisDTO getSinaisVitais() {
        return sinaisVitais;
    }

    public void setSinaisVitais(SinaisVitaisDTO sinaisVitais) {
        this.sinaisVitais = sinaisVitais;
    }

    public String getExameFisico() { return exameFisico; }
    public void setExameFisico(String exameFisico) { this.exameFisico = exameFisico; }
    public String getHipoteseDiagnostica() { return hipoteseDiagnostica; }
    public void setHipoteseDiagnostica(String hipoteseDiagnostica) { this.hipoteseDiagnostica = hipoteseDiagnostica; }
    public String getCondutaPlanoTerapeutico() { return condutaPlanoTerapeutico; }
    public void setCondutaPlanoTerapeutico(String condutaPlanoTerapeutico) { this.condutaPlanoTerapeutico = condutaPlanoTerapeutico; }
    public String getDetalhesConsulta() { return detalhesConsulta; }
    public void setDetalhesConsulta(String detalhesConsulta) { this.detalhesConsulta = detalhesConsulta; }
    public String getObservacoesConsulta() { return observacoesConsulta; }
    public void setObservacoesConsulta(String observacoesConsulta) { this.observacoesConsulta = observacoesConsulta; }
    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }
    public String getResponsavelNomeCompleto() { return responsavelNomeCompleto; }
    public void setResponsavelNomeCompleto(String responsavelNomeCompleto) { this.responsavelNomeCompleto = responsavelNomeCompleto; }
    public String getResponsavelEspecialidade() { return responsavelEspecialidade; }
    public void setResponsavelEspecialidade(String responsavelEspecialidade) { this.responsavelEspecialidade = responsavelEspecialidade; }
    public String getResponsavelCRM() { return responsavelCRM; }
    public void setResponsavelCRM(String responsavelCRM) { this.responsavelCRM = responsavelCRM; }
    public LocalDateTime getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(LocalDateTime dataConsulta) { this.dataConsulta = dataConsulta; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}