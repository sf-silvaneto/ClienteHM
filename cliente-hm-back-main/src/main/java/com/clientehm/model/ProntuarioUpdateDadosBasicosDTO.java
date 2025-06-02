// sf-silvaneto/clientehm/ClienteHM-057824fed8786ee29c7b4f9a2010aca3a83abc37/cliente-hm-back-main/src/main/java/com/clientehm/model/ProntuarioUpdateDadosBasicosDTO.java
package com.clientehm.model;

// import java.time.LocalDate; // Não é mais necessário

public class ProntuarioUpdateDadosBasicosDTO {
    private Long medicoResponsavelId;
    // private String status; // REMOVIDO
    // private LocalDate dataAltaAdministrativa; // REMOVIDO

    // Getters e Setters
    public Long getMedicoResponsavelId() {
        return medicoResponsavelId;
    }
    public void setMedicoResponsavelId(Long medicoResponsavelId) {
        this.medicoResponsavelId = medicoResponsavelId;
    }
    // public String getStatus() { // REMOVIDO
    //     return status;
    // }
    // public void setStatus(String status) { // REMOVIDO
    //     this.status = status;
    // }
    // public LocalDate getDataAltaAdministrativa() { // REMOVIDO
    //     return dataAltaAdministrativa;
    // }
    // public void setDataAltaAdministrativa(LocalDate dataAltaAdministrativa) { // REMOVIDO
    //     this.dataAltaAdministrativa = dataAltaAdministrativa;
    // }
}