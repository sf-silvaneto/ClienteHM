package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CriarExameRequestDTO {

    @NotBlank(message = "Nome do exame é obrigatório")
    @Size(min = 3, max = 200, message = "Nome do exame deve ter entre 3 e 200 caracteres")
    private String nome;

    @NotBlank(message = "Resultado do exame é obrigatório")
    @Size(min = 5, max = 5000, message = "Resultado deve ter entre 5 e 5000 caracteres")
    private String resultado;

    @Size(max = 2000, message = "Observações não podem exceder 2000 caracteres")
    private String observacoes;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}