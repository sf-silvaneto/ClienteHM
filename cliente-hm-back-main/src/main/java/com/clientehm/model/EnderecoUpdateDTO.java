package com.clientehm.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EnderecoUpdateDTO {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
    private String estado;

    @Pattern(regexp = "^\\d{8}$", message = "CEP deve conter 8 dígitos")
    private String cep;

    // Getters e Setters
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
}