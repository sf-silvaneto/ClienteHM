package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlergiaDTO {
    private Long id;
    @NotBlank(message = "A descrição da alergia é obrigatória")
    @Size(min = 3, max = 500, message = "A descrição da alergia deve ter entre 3 e 500 caracteres")
    private String descricao;

    public AlergiaDTO() {
    }

    public AlergiaDTO(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}