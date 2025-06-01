package com.clientehm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NovoProntuarioRequestDTO {

    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "ID do médico responsável é obrigatório")
    private Long medicoId;

    // @NotBlank(message = "Tipo de tratamento é obrigatório") // REMOVIDO
    // private String tipoTratamento; // REMOVIDO

    @NotNull(message = "Histórico médico inicial é obrigatório")
    @Valid
    private HistoricoMedicoInicialDTO historicoMedico;

    // Getters e Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    // public String getTipoTratamento() { return tipoTratamento; } // REMOVIDO
    // public void setTipoTratamento(String tipoTratamento) { this.tipoTratamento = tipoTratamento; } // REMOVIDO

    public HistoricoMedicoInicialDTO getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(HistoricoMedicoInicialDTO historicoMedico) { this.historicoMedico = historicoMedico; }

    public static class HistoricoMedicoInicialDTO {
        @NotBlank(message = "Descrição do histórico médico é obrigatória")
        @Size(min = 10, message = "Descrição do histórico deve ter no mínimo 10 caracteres")
        private String descricao;

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }
}