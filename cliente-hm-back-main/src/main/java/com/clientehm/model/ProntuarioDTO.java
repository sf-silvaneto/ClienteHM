package com.clientehm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Importações dos outros DTOs (PacienteDTO, HistoricoMedicoDTO, etc.)
import com.clientehm.model.PacienteDTO;
import com.clientehm.model.HistoricoMedicoDTO;
import com.clientehm.model.MedicacaoDTO;
import com.clientehm.model.ExameDTO;
import com.clientehm.model.AnotacaoDTO;

public class ProntuarioDTO {
    private Long id;
    private String numeroProntuario;
    private PacienteDTO paciente;
    private String tipoTratamento;
    private LocalDate dataInicio; // Alterado para LocalDate
    private LocalDateTime dataUltimaAtualizacao; // Mantido/Alterado para LocalDateTime
    private String status;
    private List<HistoricoMedicoDTO> historicoMedico;
    private List<MedicacaoDTO> medicacoes;
    private List<ExameDTO> exames;
    private List<AnotacaoDTO> anotacoes;
    private LocalDateTime createdAt; // Alterado para LocalDateTime
    private LocalDateTime updatedAt; // Renomeado/Mapeado de dataUltimaAtualizacao (ou adicione se for um campo separado na entidade)


    // Getters e Setters atualizados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroProntuario() { return numeroProntuario; }
    public void setNumeroProntuario(String numeroProntuario) { this.numeroProntuario = numeroProntuario; }
    public PacienteDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteDTO paciente) { this.paciente = paciente; }
    public String getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(String tipoTratamento) { this.tipoTratamento = tipoTratamento; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<HistoricoMedicoDTO> getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(List<HistoricoMedicoDTO> historicoMedico) { this.historicoMedico = historicoMedico; }
    public List<MedicacaoDTO> getMedicacoes() { return medicacoes; }
    public void setMedicacoes(List<MedicacaoDTO> medicacoes) { this.medicacoes = medicacoes; }
    public List<ExameDTO> getExames() { return exames; }
    public void setExames(List<ExameDTO> exames) { this.exames = exames; }
    public List<AnotacaoDTO> getAnotacoes() { return anotacoes; }
    public void setAnotacoes(List<AnotacaoDTO> anotacoes) { this.anotacoes = anotacoes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } // Este campo pode ser mapeado de entity.getDataUltimaAtualizacao()
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}