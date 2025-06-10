package com.clientehm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProntuarioDTO {

    private Long id;
    private String numeroProntuario;
    private PacienteDTO paciente;
    private MedicoBasicDTO medicoResponsavel;
    private AdministradorBasicDTO administradorCriador;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataUltimaAtualizacao;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<ConsultaDTO> consultas;
    private List<ExameRegistroDTO> examesRegistrados;
    private List<ProcedimentoRegistroDTO> procedimentosRegistrados;
    private List<EncaminhamentoRegistroDTO> encaminhamentosRegistrados;

    public ProntuarioDTO() {
        this.consultas = new ArrayList<>();
        this.examesRegistrados = new ArrayList<>();
        this.procedimentosRegistrados = new ArrayList<>();
        this.encaminhamentosRegistrados = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroProntuario() {
        return numeroProntuario;
    }

    public void setNumeroProntuario(String numeroProntuario) {
        this.numeroProntuario = numeroProntuario;
    }

    public PacienteDTO getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteDTO paciente) {
        this.paciente = paciente;
    }

    public MedicoBasicDTO getMedicoResponsavel() {
        return medicoResponsavel;
    }

    public void setMedicoResponsavel(MedicoBasicDTO medicoResponsavel) {
        this.medicoResponsavel = medicoResponsavel;
    }

    public AdministradorBasicDTO getAdministradorCriador() {
        return administradorCriador;
    }

    public void setAdministradorCriador(AdministradorBasicDTO administradorCriador) {
        this.administradorCriador = administradorCriador;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ConsultaDTO> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<ConsultaDTO> consultas) {
        this.consultas = consultas;
    }

    public List<ExameRegistroDTO> getExamesRegistrados() {
        return examesRegistrados;
    }

    public void setExamesRegistrados(List<ExameRegistroDTO> examesRegistrados) {
        this.examesRegistrados = examesRegistrados;
    }

    public List<ProcedimentoRegistroDTO> getProcedimentosRegistrados() {
        return procedimentosRegistrados;
    }

    public void setProcedimentosRegistrados(List<ProcedimentoRegistroDTO> procedimentosRegistrados) {
        this.procedimentosRegistrados = procedimentosRegistrados;
    }

    public List<EncaminhamentoRegistroDTO> getEncaminhamentosRegistrados() {
        return encaminhamentosRegistrados;
    }

    public void setEncaminhamentosRegistrados(List<EncaminhamentoRegistroDTO> encaminhamentosRegistrados) {
        this.encaminhamentosRegistrados = encaminhamentosRegistrados;
    }

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