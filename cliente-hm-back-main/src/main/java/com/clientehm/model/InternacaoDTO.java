// src/main/java/com/clientehm/model/InternacaoDTO.java
package com.clientehm.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class InternacaoDTO {
    private Long id;
    private Long prontuarioId;
    private LocalDateTime dataAdmissao;
    private String motivoInternacao;
    private String historiaDoencaAtual;

    private String tipoResponsavelAdmissao; // MEDICO ou ADMINISTRADOR
    private Long responsavelAdmissaoId;
    private String responsavelAdmissaoNomeCompleto;
    private String responsavelAdmissaoEspecialidade; // Se médico
    private String responsavelAdmissaoCRM; // Se médico

    private LocalDateTime dataAltaPrevista;
    private LocalDateTime dataAltaEfetiva;
    private String resumoAlta;

    private Long medicoResponsavelAltaId;
    private String medicoResponsavelAltaNome;

    private List<AnexoDTO> anexos = new ArrayList<>(); // Se houver anexos específicos de internação

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProntuarioId() { return prontuarioId; }
    public void setProntuarioId(Long prontuarioId) { this.prontuarioId = prontuarioId; }
    public LocalDateTime getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDateTime dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public String getMotivoInternacao() { return motivoInternacao; }
    public void setMotivoInternacao(String motivoInternacao) { this.motivoInternacao = motivoInternacao; }
    public String getHistoriaDoencaAtual() { return historiaDoencaAtual; }
    public void setHistoriaDoencaAtual(String historiaDoencaAtual) { this.historiaDoencaAtual = historiaDoencaAtual; }
    public String getTipoResponsavelAdmissao() { return tipoResponsavelAdmissao; }
    public void setTipoResponsavelAdmissao(String tipoResponsavelAdmissao) { this.tipoResponsavelAdmissao = tipoResponsavelAdmissao; }
    public Long getResponsavelAdmissaoId() { return responsavelAdmissaoId; }
    public void setResponsavelAdmissaoId(Long responsavelAdmissaoId) { this.responsavelAdmissaoId = responsavelAdmissaoId; }
    public String getResponsavelAdmissaoNomeCompleto() { return responsavelAdmissaoNomeCompleto; }
    public void setResponsavelAdmissaoNomeCompleto(String responsavelAdmissaoNomeCompleto) { this.responsavelAdmissaoNomeCompleto = responsavelAdmissaoNomeCompleto; }
    public String getResponsavelAdmissaoEspecialidade() { return responsavelAdmissaoEspecialidade; }
    public void setResponsavelAdmissaoEspecialidade(String responsavelAdmissaoEspecialidade) { this.responsavelAdmissaoEspecialidade = responsavelAdmissaoEspecialidade; }
    public String getResponsavelAdmissaoCRM() { return responsavelAdmissaoCRM; }
    public void setResponsavelAdmissaoCRM(String responsavelAdmissaoCRM) { this.responsavelAdmissaoCRM = responsavelAdmissaoCRM; }
    public LocalDateTime getDataAltaPrevista() { return dataAltaPrevista; }
    public void setDataAltaPrevista(LocalDateTime dataAltaPrevista) { this.dataAltaPrevista = dataAltaPrevista; }
    public LocalDateTime getDataAltaEfetiva() { return dataAltaEfetiva; }
    public void setDataAltaEfetiva(LocalDateTime dataAltaEfetiva) { this.dataAltaEfetiva = dataAltaEfetiva; }
    public String getResumoAlta() { return resumoAlta; }
    public void setResumoAlta(String resumoAlta) { this.resumoAlta = resumoAlta; }
    public Long getMedicoResponsavelAltaId() { return medicoResponsavelAltaId; }
    public void setMedicoResponsavelAltaId(Long medicoResponsavelAltaId) { this.medicoResponsavelAltaId = medicoResponsavelAltaId; }
    public String getMedicoResponsavelAltaNome() { return medicoResponsavelAltaNome; }
    public void setMedicoResponsavelAltaNome(String medicoResponsavelAltaNome) { this.medicoResponsavelAltaNome = medicoResponsavelAltaNome; }
    public List<AnexoDTO> getAnexos() { return anexos; }
    public void setAnexos(List<AnexoDTO> anexos) { this.anexos = anexos; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}