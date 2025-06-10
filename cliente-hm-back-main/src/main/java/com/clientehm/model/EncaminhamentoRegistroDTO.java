package com.clientehm.model;

import java.time.LocalDateTime;

public class EncaminhamentoRegistroDTO {
    private Long id;
    private Long prontuarioId;
    private String especialidadeDestino;
    private String motivoEncaminhamento;
    private Long medicoSolicitanteId;
    private String medicoSolicitanteNome;
    private String medicoSolicitanteCRM;
    private String medicoSolicitanteEspecialidade; // Adicionado especialidade
    private String observacoes;
    // private String nomeResponsavelDisplay; // Removido
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProntuarioId() { return prontuarioId; }
    public void setProntuarioId(Long prontuarioId) { this.prontuarioId = prontuarioId; }
    public String getEspecialidadeDestino() { return especialidadeDestino; }
    public void setEspecialidadeDestino(String especialidadeDestino) { this.especialidadeDestino = especialidadeDestino; }
    public String getMotivoEncaminhamento() { return motivoEncaminhamento; }
    public void setMotivoEncaminhamento(String motivoEncaminhamento) { this.motivoEncaminhamento = motivoEncaminhamento; }
    public Long getMedicoSolicitanteId() { return medicoSolicitanteId; }
    public void setMedicoSolicitanteId(Long medicoSolicitanteId) { this.medicoSolicitanteId = medicoSolicitanteId; }
    public String getMedicoSolicitanteNome() { return medicoSolicitanteNome; }
    public void setMedicoSolicitanteNome(String medicoSolicitanteNome) { this.medicoSolicitanteNome = medicoSolicitanteNome; }
    public String getMedicoSolicitanteCRM() { return medicoSolicitanteCRM; }
    public void setMedicoSolicitanteCRM(String medicoSolicitanteCRM) { this.medicoSolicitanteCRM = medicoSolicitanteCRM; }
    public String getMedicoSolicitanteEspecialidade() { return medicoSolicitanteEspecialidade; } // Getter para especialidade
    public void setMedicoSolicitanteEspecialidade(String medicoSolicitanteEspecialidade) { this.medicoSolicitanteEspecialidade = medicoSolicitanteEspecialidade; } // Setter para especialidade
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    // public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; } // Removido
    // public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; } // Removido
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}