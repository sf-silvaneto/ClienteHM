// src/main/java/com/clientehm/model/InternacaoRequestDTO.java
package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class InternacaoRequestDTO {

    @NotNull(message = "ID do Paciente é obrigatório")
    private Long pacienteId; // Necessário se o prontuário for criado junto com a internação

    @NotNull(message = "ID do Médico Responsável pela admissão é obrigatório")
    private Long medicoResponsavelAdmissaoId; // Médico que está admitindo

    @NotNull(message = "Data e hora da admissão são obrigatórias")
    private LocalDateTime dataAdmissao;

    @NotBlank(message = "Motivo da internação é obrigatório")
    @Size(min = 10, message = "Motivo da internação deve ter no mínimo 10 caracteres")
    private String motivoInternacao;

    @Size(max = 10000, message = "História da doença atual não pode exceder 10000 caracteres")
    private String historiaDoencaAtual; // Mais detalhes, pode ser opcional

    private LocalDateTime dataAltaPrevista; // Opcional

    // Getters e Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getMedicoResponsavelAdmissaoId() { return medicoResponsavelAdmissaoId; }
    public void setMedicoResponsavelAdmissaoId(Long medicoResponsavelAdmissaoId) { this.medicoResponsavelAdmissaoId = medicoResponsavelAdmissaoId; }
    public LocalDateTime getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDateTime dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public String getMotivoInternacao() { return motivoInternacao; }
    public void setMotivoInternacao(String motivoInternacao) { this.motivoInternacao = motivoInternacao; }
    public String getHistoriaDoencaAtual() { return historiaDoencaAtual; }
    public void setHistoriaDoencaAtual(String historiaDoencaAtual) { this.historiaDoencaAtual = historiaDoencaAtual; }
    public LocalDateTime getDataAltaPrevista() { return dataAltaPrevista; }
    public void setDataAltaPrevista(LocalDateTime dataAltaPrevista) { this.dataAltaPrevista = dataAltaPrevista; }
}