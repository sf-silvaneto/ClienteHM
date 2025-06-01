package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prontuarios")
public class ProntuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroProntuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private MedicoEntity medicoResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_criador_id")
    private AdministradorEntity administradorCriador;

    // @Enumerated(EnumType.STRING) // REMOVIDO
    // @Column(nullable = false) // REMOVIDO
    // private TipoTratamento tipoTratamento; // REMOVIDO

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataFim;
    private LocalDate dataAlta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProntuario status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoricoMedicoEntity> historicoMedico = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EntradaMedicaRegistroEntity> entradasMedicas = new ArrayList<>();

    // TODO: Adicionar outras listas conforme sua modelagem final para Exames, Cirurgias, etc.
    // Se forem tipos específicos de registros.


    @PrePersist
    protected void onCreate() {
        createdAt = dataUltimaAtualizacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusProntuario.ATIVO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }

    // O enum TipoTratamento foi REMOVIDO daqui
    // public enum TipoTratamento {
    // TERAPIA_INDIVIDUAL, TERAPIA_CASAL, TERAPIA_GRUPO, TERAPIA_FAMILIAR, OUTRO
    // }

    public enum StatusProntuario {
        ATIVO, INATIVO, ARQUIVADO, ALTA
    }

    // Getters e Setters (remover getter/setter de tipoTratamento)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroProntuario() { return numeroProntuario; }
    public void setNumeroProntuario(String numeroProntuario) { this.numeroProntuario = numeroProntuario; }
    public PacienteEntity getPaciente() { return paciente; }
    public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }
    public MedicoEntity getMedicoResponsavel() { return medicoResponsavel; }
    public void setMedicoResponsavel(MedicoEntity medicoResponsavel) { this.medicoResponsavel = medicoResponsavel; }
    public AdministradorEntity getAdministradorCriador() { return administradorCriador; }
    public void setAdministradorCriador(AdministradorEntity administradorCriador) { this.administradorCriador = administradorCriador; }
    // public TipoTratamento getTipoTratamento() { return tipoTratamento; } // REMOVIDO
    // public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; } // REMOVIDO
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public LocalDate getDataAlta() { return dataAlta; }
    public void setDataAlta(LocalDate dataAlta) { this.dataAlta = dataAlta; }
    public StatusProntuario getStatus() { return status; }
    public void setStatus(StatusProntuario status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public List<HistoricoMedicoEntity> getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(List<HistoricoMedicoEntity> historicoMedico) { this.historicoMedico = historicoMedico; }
    public List<EntradaMedicaRegistroEntity> getEntradasMedicas() { return entradasMedicas; }
    public void setEntradasMedicas(List<EntradaMedicaRegistroEntity> entradasMedicas) { this.entradasMedicas = entradasMedicas; }
}