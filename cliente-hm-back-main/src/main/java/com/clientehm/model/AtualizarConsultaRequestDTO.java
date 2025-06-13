package com.clientehm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AtualizarConsultaRequestDTO {

    private LocalDateTime dataConsulta;

    @Size(min = 3, max = 500, message = "Motivo da consulta deve ter entre 3 e 500 caracteres")
    private String motivoConsulta;

    @Size(min = 5, max = 2000, message = "Queixas principais devem ter entre 5 e 2000 caracteres")
    private String queixasPrincipais;

    @Valid
    private SinaisVitaisDTO sinaisVitais;

    @Size(max = 5000, message = "Exame físico não pode exceder 5000 caracteres")
    private String exameFisico;

    @Size(max = 2000, message = "Hipótese diagnóstica não pode exceder 2000 caracteres")
    private String hipoteseDiagnostica;

    @Size(max = 5000, message = "Conduta / Plano terapêutico não pode exceder 5000 caracteres")
    private String condutaPlanoTerapeutico;

    @Size(max = 10000, message = "Detalhes da consulta não podem exceder 10000 caracteres")
    private String detalhesConsulta;

    @Size(max = 5000, message = "Observações da consulta não podem exceder 5000 caracteres")
    private String observacoesConsulta;

    @NotNull(message = "ID do médico executor é obrigatório") // TORNADO OBRIGATÓRIO
    private Long medicoExecutorId;

    public LocalDateTime getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(LocalDateTime dataConsulta) { this.dataConsulta = dataConsulta; }
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
    public Long getMedicoExecutorId() { return medicoExecutorId; }
    public void setMedicoExecutorId(Long medicoExecutorId) { this.medicoExecutorId = medicoExecutorId; }
}