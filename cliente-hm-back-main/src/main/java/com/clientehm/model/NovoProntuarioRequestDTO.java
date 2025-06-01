// src/main/java/com/clientehm/model/NovoProntuarioRequestDTO.java
package com.clientehm.model;

import jakarta.validation.constraints.NotNull;

// Este DTO pode não ser mais usado diretamente para criar um ProntuarioEntity isoladamente.
// Em vez disso, os dados pacienteId e medicoId podem ser parte de um DTO maior
// para criar o primeiro evento (ex: NovaInternacaoComProntuarioRequestDTO).
// Ou, pode ser usado numa primeira etapa para verificar/selecionar paciente e médico
// antes de escolher o tipo de registro. Vamos mantê-lo simples por agora.
public class NovoProntuarioRequestDTO {

    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "ID do médico responsável é obrigatório")
    private Long medicoId;

    // O campo historicoMedico foi removido.
    // A descrição inicial será parte do DTO do primeiro evento clínico (ex: motivo da internação).

    // Getters e Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
}