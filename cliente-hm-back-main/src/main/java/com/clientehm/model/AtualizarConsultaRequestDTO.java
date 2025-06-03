package com.clientehm.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AtualizarConsultaRequestDTO {

    private LocalDateTime dataHoraConsulta;

    @Size(min = 3, max = 500, message = "Motivo da consulta deve ter entre 3 e 500 caracteres")
    private String motivoConsulta;

    @Size(min = 5, max = 2000, message = "Queixas principais devem ter entre 5 e 2000 caracteres")
    private String queixasPrincipais;

    @Size(max = 20, message = "Pressão arterial não pode exceder 20 caracteres")
    private String pressaoArterial;

    @Size(max = 10, message = "Temperatura não pode exceder 10 caracteres")
    private String temperatura;

    @Size(max = 10, message = "Frequência cardíaca não pode exceder 10 caracteres")
    private String frequenciaCardiaca;

    @Size(max = 10, message = "Saturação não pode exceder 10 caracteres")
    private String saturacao;

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

    private Long medicoExecutorId; // Para permitir alterar o médico da consulta

    // Getters e Setters

    public LocalDateTime getDataHoraConsulta() { return dataHoraConsulta; }
    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) { this.dataHoraConsulta = dataHoraConsulta; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
    public String getQueixasPrincipais() { return queixasPrincipais; }
    public void setQueixasPrincipais(String queixasPrincipais) { this.queixasPrincipais = queixasPrincipais; }
    public String getPressaoArterial() { return pressaoArterial; }
    public void setPressaoArterial(String pressaoArterial) { this.pressaoArterial = pressaoArterial; }
    public String getTemperatura() { return temperatura; }
    public void setTemperatura(String temperatura) { this.temperatura = temperatura; }
    public String getFrequenciaCardiaca() { return frequenciaCardiaca; }
    public void setFrequenciaCardiaca(String frequenciaCardiaca) { this.frequenciaCardiaca = frequenciaCardiaca; }
    public String getSaturacao() { return saturacao; }
    public void setSaturacao(String saturacao) { this.saturacao = saturacao; }
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