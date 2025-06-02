// src/main/java/com/clientehm/model/ProntuarioUpdateDadosBasicosDTO.java
package com.clientehm.model;

import java.time.LocalDate;

// DTO para atualizar campos básicos do prontuário
public class ProntuarioUpdateDadosBasicosDTO {
    private Long medicoResponsavelId;
    private String status; // "EM_ELABORACAO", "ARQUIVADO" (INTERNADO é automático)
    private LocalDate dataAltaAdministrativa;

    // Getters e Setters
    public Long getMedicoResponsavelId() {
        return medicoResponsavelId;
    }
    public void setMedicoResponsavelId(Long medicoResponsavelId) {
        this.medicoResponsavelId = medicoResponsavelId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getDataAltaAdministrativa() {
        return dataAltaAdministrativa;
    }
    public void setDataAltaAdministrativa(LocalDate dataAltaAdministrativa) {
        this.dataAltaAdministrativa = dataAltaAdministrativa;
    }
}