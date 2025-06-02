package com.clientehm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "anexos_entradas_medicas")
public class AnexoEntradaMedicaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_medica_id", nullable = false)
    private EntradaMedicaRegistroEntity entradaMedica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anexo_id", nullable = false)
    private AnexoEntity anexo;

    // Construtores
    public AnexoEntradaMedicaEntity() {
    }

    public AnexoEntradaMedicaEntity(EntradaMedicaRegistroEntity entradaMedica, AnexoEntity anexo) {
        this.entradaMedica = entradaMedica;
        this.anexo = anexo;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntradaMedicaRegistroEntity getEntradaMedica() {
        return entradaMedica;
    }

    public void setEntradaMedica(EntradaMedicaRegistroEntity entradaMedica) {
        this.entradaMedica = entradaMedica;
    }

    public AnexoEntity getAnexo() {
        return anexo;
    }

    public void setAnexo(AnexoEntity anexo) {
        this.anexo = anexo;
    }
}