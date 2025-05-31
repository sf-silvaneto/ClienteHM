package com.clientehm.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList; // Importar ArrayList

public class ProntuarioDTO {
    private Long id;
    private String numeroProntuario;
    private PacienteDTO paciente; // PacienteDTO já deve ter os campos de alergia, etc.
    private MedicoBasicDTO medicoResponsavel;
    private AdministradorBasicDTO administradorCriador;
    private String tipoTratamento;
    private LocalDate dataInicio;
    private LocalDateTime dataUltimaAtualizacao;
    private String status;
    private List<HistoricoMedicoDTO> historicoMedico;
    private List<MedicacaoDTO> medicacoes = new ArrayList<>(); // Inicializar listas
    private List<ExameDTO> exames = new ArrayList<>();
    private List<AnotacaoDTO> anotacoes = new ArrayList<>();
    private List<EntradaMedicaRegistroDTO> entradasMedicas = new ArrayList<>(); // NOVA LISTA

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate dataAlta;

    // Getters e Setters existentes ...
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
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDate getDataAlta() { return dataAlta; }
    public void setDataAlta(LocalDate dataAlta) { this.dataAlta = dataAlta; }

    // Getter e Setter para a nova lista
    public List<EntradaMedicaRegistroDTO> getEntradasMedicas() { return entradasMedicas; }
    public void setEntradasMedicas(List<EntradaMedicaRegistroDTO> entradasMedicas) { this.entradasMedicas = entradasMedicas; }

    // Classes aninhadas MedicoBasicDTO e AdministradorBasicDTO (manter como antes)
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