package com.clientehm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class CriarExameRequestDTO {

    @NotBlank(message = "Nome do exame é obrigatório")
    @Size(min = 3, max = 200, message = "Nome do exame deve ter entre 3 e 200 caracteres")
    private String nome;

    @NotNull(message = "Data do exame é obrigatória")
    private LocalDateTime data; // No frontend é string, aqui convertemos para LocalDateTime

    @NotBlank(message = "Resultado do exame é obrigatório")
    @Size(min = 5, max = 5000, message = "Resultado deve ter entre 5 e 5000 caracteres")
    private String resultado;

    @Size(max = 2000, message = "Observações não podem exceder 2000 caracteres")
    private String observacoes;

    // Não inclui 'arquivo' pois foi removido do frontend e não há lógica de upload no backend
    // medicoResponsavelExameId será passado como @RequestParam no controller

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}