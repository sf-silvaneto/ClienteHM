package com.clientehm.model;

import java.time.LocalDateTime;

public class ConsultaDTO {
    private Long id;
    private String motivoConsulta;
    private String queixasPrincipais;

    private SinaisVitaisDTO sinaisVitais;

    private String exameFisico;
    private String hipoteseDiagnostica;
    private String condutaPlanoTerapeutico;
    private String detalhesConsulta;
    private String observacoesConsulta;
    // REMOVIDO: private String tipoResponsavel; // Não será mais necessário
    private Long responsavelId; // Será sempre o ID do médico
    private String responsavelNomeCompleto; // Será sempre o nome do médico
    private String responsavelEspecialidade; // Será sempre a especialidade do médico
    private String responsavelCRM; // Será sempre o CRM do médico

    private LocalDateTime dataConsulta;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    // REMOVIDO: public String getTipoResponsavel() { return tipoResponsavel; }
    // REMOVIDO: public void setTipoResponsavel(String tipoResponsavel) { this.tipoResponsavel = tipoResponsavel; }
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