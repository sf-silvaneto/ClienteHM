// src/main/java/com/clientehm/entity/ProntuarioEntity.java
package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Importar UUID

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

    // dataFim não é mais usado diretamente, a alta é controlada pela internação
    // private LocalDate dataFim;
    private LocalDate dataAltaAdministrativa; // Para casos de arquivamento manual ou outros fluxos

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProntuario status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao;

    // Mantido para histórico geral ou notas não vinculadas a um evento específico
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoricoMedicoEntity> historicoGeral = new ArrayList<>();

    // Relação com Entradas Médicas (Consultas)
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EntradaMedicaRegistroEntity> consultas = new ArrayList<>();

    // Relação com Internações
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InternacaoEntity> internacoes = new ArrayList<>();

    // TODO: Adicionar outras listas para Exames, Cirurgias se forem entidades separadas.
    // @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<ExameRegistroEntity> exames = new ArrayList<>();

    // @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<CirurgiaRegistroEntity> cirurgias = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = dataUltimaAtualizacao = LocalDateTime.now();
        if (this.numeroProntuario == null) { // Gerar número do prontuário se não existir
            this.numeroProntuario = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        // O status será definido pela lógica de criação do primeiro evento
    }

    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }

    public enum StatusProntuario {
        EM_ELABORACAO, // Status inicial antes do primeiro evento significativo
        INTERNADO,
        ARQUIVADO
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

    public LocalDate getDataAltaAdministrativa() { return dataAltaAdministrativa; }
    public void setDataAltaAdministrativa(LocalDate dataAltaAdministrativa) { this.dataAltaAdministrativa = dataAltaAdministrativa; }

    public StatusProntuario getStatus() { return status; }
    public void setStatus(StatusProntuario status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }

    public List<HistoricoMedicoEntity> getHistoricoGeral() { return historicoGeral; }
    public void setHistoricoGeral(List<HistoricoMedicoEntity> historicoGeral) { this.historicoGeral = historicoGeral; }

    public List<EntradaMedicaRegistroEntity> getConsultas() { return consultas; }
    public void setConsultas(List<EntradaMedicaRegistroEntity> consultas) { this.consultas = consultas; }

    public List<InternacaoEntity> getInternacoes() { return internacoes; }
    public void setInternacoes(List<InternacaoEntity> internacoes) { this.internacoes = internacoes; }
}