package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime; // Usar LocalDateTime para dataHoraEntrada

public class CriarEntradaMedicaRequestDTO {

    @NotNull(message = "Data e hora da entrada são obrigatórias")
    private LocalDateTime dataHoraEntrada;

    @NotBlank(message = "Motivo da entrada é obrigatório")
    @Size(min = 3, message = "Motivo da entrada deve ter no mínimo 3 caracteres")
    private String motivoEntrada;

    @NotBlank(message = "Queixas principais são obrigatórias")
    @Size(min = 5, message = "Queixas principais devem ter no mínimo 5 caracteres")
    private String queixasPrincipais;

    private String pressaoArterial;
    private String temperatura;
    private String frequenciaCardiaca;
    private String saturacao;

    private String alergiasDetalhe;
    private Boolean semAlergiasConhecidas;

    // Usar String para "sim"/"nao" vindo do frontend, converter no service
    private String temComorbidades;
    private String comorbidadesDetalhes;

    private String usaMedicamentosContinuos;
    private String medicamentosContinuosDetalhes;

    private String historicoFamiliarRelevante;

    // O ID do prontuário virá do PathVariable do endpoint
    // O responsável virá do @AuthenticationPrincipal

    // Getters e Setters
    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) { this.dataHoraEntrada = dataHoraEntrada; }
    public String getMotivoEntrada() { return motivoEntrada; }
    public void setMotivoEntrada(String motivoEntrada) { this.motivoEntrada = motivoEntrada; }
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
    public String getAlergiasDetalhe() { return alergiasDetalhe; }
    public void setAlergiasDetalhe(String alergiasDetalhe) { this.alergiasDetalhe = alergiasDetalhe; }
    public Boolean getSemAlergiasConhecidas() { return semAlergiasConhecidas; }
    public void setSemAlergiasConhecidas(Boolean semAlergiasConhecidas) { this.semAlergiasConhecidas = semAlergiasConhecidas; }
    public String getTemComorbidades() { return temComorbidades; }
    public void setTemComorbidades(String temComorbidades) { this.temComorbidades = temComorbidades; }
    public String getComorbidadesDetalhes() { return comorbidadesDetalhes; }
    public void setComorbidadesDetalhes(String comorbidadesDetalhes) { this.comorbidadesDetalhes = comorbidadesDetalhes; }
    public String getUsaMedicamentosContinuos() { return usaMedicamentosContinuos; }
    public void setUsaMedicamentosContinuos(String usaMedicamentosContinuos) { this.usaMedicamentosContinuos = usaMedicamentosContinuos; }
    public String getMedicamentosContinuosDetalhes() { return medicamentosContinuosDetalhes; }
    public void setMedicamentosContinuosDetalhes(String medicamentosContinuosDetalhes) { this.medicamentosContinuosDetalhes = medicamentosContinuosDetalhes; }
    public String getHistoricoFamiliarRelevante() { return historicoFamiliarRelevante; }
    public void setHistoricoFamiliarRelevante(String historicoFamiliarRelevante) { this.historicoFamiliarRelevante = historicoFamiliarRelevante; }
}