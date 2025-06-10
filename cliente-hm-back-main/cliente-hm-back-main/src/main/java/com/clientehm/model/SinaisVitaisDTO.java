package com.clientehm.model;

import jakarta.validation.constraints.Size;

public class SinaisVitaisDTO {

    @Size(max = 20, message = "Pressão arterial não pode exceder 20 caracteres")
    private String pressaoArterial;

    @Size(max = 10, message = "Temperatura não pode exceder 10 caracteres")
    private String temperatura;

    @Size(max = 10, message = "Frequência cardíaca não pode exceder 10 caracteres")
    private String frequenciaCardiaca;

    @Size(max = 10, message = "Saturação não pode exceder 10 caracteres")
    private String saturacao;

    @Size(max = 10, message = "HGT não pode exceder 10 caracteres") // Novo campo
    private String hgt;

    public String getPressaoArterial() {
        return pressaoArterial;
    }

    public void setPressaoArterial(String pressaoArterial) {
        this.pressaoArterial = pressaoArterial;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getFrequenciaCardiaca() {
        return frequenciaCardiaca;
    }

    public void setFrequenciaCardiaca(String frequenciaCardiaca) {
        this.frequenciaCardiaca = frequenciaCardiaca;
    }

    public String getSaturacao() {
        return saturacao;
    }

    public void setSaturacao(String saturacao) {
        this.saturacao = saturacao;
    }

    public String getHgt() {
        return hgt;
    }

    public void setHgt(String hgt) {
        this.hgt = hgt;
    }
}