package com.clientehm.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AtualizarEncaminhamentoRequestDTO {

    private LocalDateTime dataEncaminhamento;

    @Size(min = 3, max = 200, message = "Especialidade deve ter entre 3 e 200 caracteres")
    private String especialidadeDestino;

    @Size(min = 10, max = 1000, message = "Motivo deve ter entre 10 e 1000 caracteres")
    private String motivoEncaminhamento;

    private Long medicoSolicitanteId;

    @Size(max = 2000, message = "Observações não podem exceder 2000 caracteres")
    private String observacoes;

    public LocalDateTime getDataEncaminhamento() { return dataEncaminhamento; }
    public void setDataEncaminhamento(LocalDateTime dataEncaminhamento) { this.dataEncaminhamento = dataEncaminhamento; }

    public String getEspecialidadeDestino() { return especialidadeDestino; }
    public void setEspecialidadeDestino(String especialidadeDestino) { this.especialidadeDestino = especialidadeDestino; }

    public String getMotivoEncaminhamento() { return motivoEncaminhamento; }
    public void setMotivoEncaminhamento(String motivoEncaminhamento) { this.motivoEncaminhamento = motivoEncaminhamento; }

    public Long getMedicoSolicitanteId() { return medicoSolicitanteId; }
    public void setMedicoSolicitanteId(Long medicoSolicitanteId) { this.medicoSolicitanteId = medicoSolicitanteId; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}