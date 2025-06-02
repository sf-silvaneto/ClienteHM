// src/main/java/com/clientehm/model/RegistrarAltaInternacaoDTO.java
package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class RegistrarAltaInternacaoDTO {

    @NotNull(message = "Data e hora da alta efetiva são obrigatórias.")
    private LocalDateTime dataAltaEfetiva;

    @NotBlank(message = "Resumo da alta é obrigatório.")
    private String resumoAlta;

    @NotNull(message = "ID do médico responsável pela alta é obrigatório.")
    private Long medicoResponsavelAltaId;

    // Getters e Setters
    public LocalDateTime getDataAltaEfetiva() { return dataAltaEfetiva; }
    public void setDataAltaEfetiva(LocalDateTime dataAltaEfetiva) { this.dataAltaEfetiva = dataAltaEfetiva; }
    public String getResumoAlta() { return resumoAlta; }
    public void setResumoAlta(String resumoAlta) { this.resumoAlta = resumoAlta; }
    public Long getMedicoResponsavelAltaId() { return medicoResponsavelAltaId; }
    public void setMedicoResponsavelAltaId(Long medicoResponsavelAltaId) { this.medicoResponsavelAltaId = medicoResponsavelAltaId; }
}