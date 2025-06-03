package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EntradaMedicaRegistroEntity> consultas = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExameRegistroEntity> examesRegistrados = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProcedimentoRegistroEntity> procedimentosRegistrados = new ArrayList<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EncaminhamentoRegistroEntity> encaminhamentosRegistrados = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = dataUltimaAtualizacao = LocalDateTime.now();
        if (this.numeroProntuario == null) {
            this.numeroProntuario = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.dataInicio == null) {
            this.dataInicio = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
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
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public List<EntradaMedicaRegistroEntity> getConsultas() { return consultas; }
    public void setConsultas(List<EntradaMedicaRegistroEntity> consultas) { this.consultas = consultas; }
    public List<ExameRegistroEntity> getExamesRegistrados() { return examesRegistrados; }
    public void setExamesRegistrados(List<ExameRegistroEntity> examesRegistrados) { this.examesRegistrados = examesRegistrados; }
    public List<ProcedimentoRegistroEntity> getProcedimentosRegistrados() { return procedimentosRegistrados; }
    public void setProcedimentosRegistrados(List<ProcedimentoRegistroEntity> procedimentosRegistrados) { this.procedimentosRegistrados = procedimentosRegistrados; }
    public List<EncaminhamentoRegistroEntity> getEncaminhamentosRegistrados() { return encaminhamentosRegistrados; }
    public void setEncaminhamentosRegistrados(List<EncaminhamentoRegistroEntity> encaminhamentosRegistrados) { this.encaminhamentosRegistrados = encaminhamentosRegistrados; }
}