package com.clientehm.model;

import java.time.LocalDateTime;
import java.util.List; // IMPORTAR LIST
import java.util.ArrayList; // IMPORTAR ARRAYLIST

public class EntradaMedicaRegistroDTO {
    private Long id;
    private LocalDateTime dataHoraEntrada;
    private String motivoEntrada;
    private String queixasPrincipais;
    private String pressaoArterial;
    private String temperatura;
    private String frequenciaCardiaca;
    private String saturacao;
    private String alergiasDetalhe;
    private Boolean semAlergiasConhecidas;
    private Boolean temComorbidades;
    private String comorbidadesDetalhes;
    private Boolean usaMedicamentosContinuos;
    private String medicamentosContinuosDetalhes;
    private String historicoFamiliarRelevante;

    // CAMPOS DO RESPONSÁVEL ATUALIZADOS/ADICIONADOS
    // private String nomeResponsavelDisplay; // Pode ser mantido ou removido se os abaixo forem suficientes
    private String tipoResponsavel; // Ex: "MEDICO", "ADMINISTRADOR"
    private Long responsavelId; // ID do médico ou admin
    private String responsavelNomeCompleto;
    private String responsavelEspecialidade; // Apenas se for médico
    private String responsavelCRM;           // Apenas se for médico

    private List<AnexoDTO> anexos = new ArrayList<>(); // NOVA LISTA DE ANEXOS

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Boolean getTemComorbidades() { return temComorbidades; }
    public void setTemComorbidades(Boolean temComorbidades) { this.temComorbidades = temComorbidades; }
    public String getComorbidadesDetalhes() { return comorbidadesDetalhes; }
    public void setComorbidadesDetalhes(String comorbidadesDetalhes) { this.comorbidadesDetalhes = comorbidadesDetalhes; }
    public Boolean getUsaMedicamentosContinuos() { return usaMedicamentosContinuos; }
    public void setUsaMedicamentosContinuos(Boolean usaMedicamentosContinuos) { this.usaMedicamentosContinuos = usaMedicamentosContinuos; }
    public String getMedicamentosContinuosDetalhes() { return medicamentosContinuosDetalhes; }
    public void setMedicamentosContinuosDetalhes(String medicamentosContinuosDetalhes) { this.medicamentosContinuosDetalhes = medicamentosContinuosDetalhes; }
    public String getHistoricoFamiliarRelevante() { return historicoFamiliarRelevante; }
    public void setHistoricoFamiliarRelevante(String historicoFamiliarRelevante) { this.historicoFamiliarRelevante = historicoFamiliarRelevante; }

    // public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; }
    // public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; }

    public String getTipoResponsavel() { return tipoResponsavel; }
    public void setTipoResponsavel(String tipoResponsavel) { this.tipoResponsavel = tipoResponsavel; }
    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }
    public String getResponsavelNomeCompleto() { return responsavelNomeCompleto; }
    public void setResponsavelNomeCompleto(String responsavelNomeCompleto) { this.responsavelNomeCompleto = responsavelNomeCompleto; }
    public String getResponsavelEspecialidade() { return responsavelEspecialidade; }
    public void setResponsavelEspecialidade(String responsavelEspecialidade) { this.responsavelEspecialidade = responsavelEspecialidade; }
    public String getResponsavelCRM() { return responsavelCRM; }
    public void setResponsavelCRM(String responsavelCRM) { this.responsavelCRM = responsavelCRM; }

    public List<AnexoDTO> getAnexos() { return anexos; }
    public void setAnexos(List<AnexoDTO> anexos) { this.anexos = anexos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}