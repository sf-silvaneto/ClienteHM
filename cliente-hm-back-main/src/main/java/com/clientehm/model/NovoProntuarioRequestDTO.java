package com.clientehm.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
// Remover imports de PacienteRequestDTO e EnderecoRequestDTO se não forem mais usados aqui
// import java.time.LocalDate; // Se PacienteRequestDTO for removido, este pode não ser necessário aqui

public class NovoProntuarioRequestDTO {

    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId; // ID do paciente selecionado

    @NotNull(message = "ID do médico responsável é obrigatório")
    private Long medicoId;   // ID do médico responsável selecionado

    @NotBlank(message = "Tipo de tratamento é obrigatório")
    private String tipoTratamento; // Usar String para representar o Enum vindo do frontend

    @NotNull(message = "Histórico médico inicial é obrigatório")
    @Valid
    private HistoricoMedicoInicialDTO historicoMedico;

    // Getters e Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }

    public String getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(String tipoTratamento) { this.tipoTratamento = tipoTratamento; }

    public HistoricoMedicoInicialDTO getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(HistoricoMedicoInicialDTO historicoMedico) { this.historicoMedico = historicoMedico; }

    // A classe aninhada PacienteRequestDTO e EnderecoRequestDTO podem ser removidas deste arquivo
    // se a criação de paciente for exclusivamente via ID.
    // Se você mantiver a opção de criar paciente junto com o prontuário (como fallback),
    // então elas podem permanecer, mas o DTO principal precisaria de lógica para aceitar ou um ou outro.
    // Para esta refatoração, vamos assumir que o frontend sempre enviará o pacienteId.

    public static class HistoricoMedicoInicialDTO {
        @NotBlank(message = "Descrição do histórico médico é obrigatória")
        @Size(min = 10, message = "Descrição do histórico deve ter no mínimo 10 caracteres")
        private String descricao;

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }
}