// src/main/java/com/clientehm/model/ProntuarioDTO.java
package com.clientehm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class ProntuarioDTO {
    private Long id;
    private String numeroProntuario;
    private PacienteDTO paciente; // Supondo que PacienteDTO já existe e está correto
    private MedicoBasicDTO medicoResponsavel; // Supondo que MedicoBasicDTO já existe e está correto
    private AdministradorBasicDTO administradorCriador; // Supondo que AdministradorBasicDTO já existe e está correto

    private LocalDate dataInicio;
    private LocalDateTime dataUltimaAtualizacao;
    private String status; // Será "EM_ELABORACAO", "INTERNADO", "ARQUIVADO"

    private List<HistoricoMedicoDTO> historicoGeral = new ArrayList<>(); // Renomeado
    private List<ConsultaDTO> consultas = new ArrayList<>(); // Renomeado de entradasMedicas
    private List<InternacaoDTO> internacoes = new ArrayList<>(); // Nova lista
    // private List<ExameRegistroDTO> exames = new ArrayList<>(); // Exemplo
    // private List<CirurgiaRegistroDTO> cirurgias = new ArrayList<>(); // Exemplo


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Mapeado de dataUltimaAtualizacao na entidade
    private LocalDate dataAltaAdministrativa; // Novo campo

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroProntuario() { return numeroProntuario; }
    public void setNumeroProntuario(String numeroProntuario) { this.numeroProntuario = numeroProntuario; }
    public PacienteDTO getPaciente() { return paciente; }
    public void setPaciente(PacienteDTO paciente) { this.paciente = paciente; }
    public MedicoBasicDTO getMedicoResponsavel() { return medicoResponsavel; }
    public void setMedicoResponsavel(MedicoBasicDTO medicoResponsavel) { this.medicoResponsavel = medicoResponsavel; }
    public AdministradorBasicDTO getAdministradorCriador() { return administradorCriador; }
    public void setAdministradorCriador(AdministradorBasicDTO administradorCriador) { this.administradorCriador = administradorCriador; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<HistoricoMedicoDTO> getHistoricoGeral() { return historicoGeral; }
    public void setHistoricoGeral(List<HistoricoMedicoDTO> historicoGeral) { this.historicoGeral = historicoGeral; }

    public List<ConsultaDTO> getConsultas() { return consultas; }
    public void setConsultas(List<ConsultaDTO> consultas) { this.consultas = consultas; }

    public List<InternacaoDTO> getInternacoes() { return internacoes; }
    public void setInternacoes(List<InternacaoDTO> internacoes) { this.internacoes = internacoes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDate getDataAltaAdministrativa() { return dataAltaAdministrativa; }
    public void setDataAltaAdministrativa(LocalDate dataAltaAdministrativa) { this.dataAltaAdministrativa = dataAltaAdministrativa; }

    // Sub-DTOs MedicoBasicDTO e AdministradorBasicDTO permanecem os mesmos
    // (copiados do arquivo original para referência, caso não existam em arquivos separados)
    public static class MedicoBasicDTO {
        private Long id;
        private String nomeCompleto;
        private String crm;
        private String especialidade;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNomeCompleto() { return nomeCompleto; }
        public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
        public String getCrm() { return crm; }
        public void setCrm(String crm) { this.crm = crm; }
        public String getEspecialidade() { return especialidade; }
        public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    }

    public static class AdministradorBasicDTO {
        private Long id;
        private String nome;
        private String email;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}