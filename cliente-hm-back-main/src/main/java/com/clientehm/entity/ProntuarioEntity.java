package com.clientehm.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "prontuarios")
public class ProntuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroProntuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonManagedReference
    private PacienteEntity paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_responsavel_id", nullable = false)
    private MedicoEntity medicoResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_criador_id", nullable = false)
    private AdministradorEntity administradorCriador;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ConsultaRegistroEntity> consultas = new HashSet<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ExameRegistroEntity> examesRegistrados = new HashSet<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProcedimentoRegistroEntity> procedimentosRegistrados = new HashSet<>();

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EncaminhamentoRegistroEntity> encaminhamentosRegistrados = new HashSet<>();


    public ProntuarioEntity() {
    }

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
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Set<ConsultaRegistroEntity> getConsultas() { return consultas; }
    public void setConsultas(Set<ConsultaRegistroEntity> consultas) { this.consultas = consultas; }
    public Set<ExameRegistroEntity> getExamesRegistrados() { return examesRegistrados; }
    public void setExamesRegistrados(Set<ExameRegistroEntity> examesRegistrados) { this.examesRegistrados = examesRegistrados; }
    public Set<ProcedimentoRegistroEntity> getProcedimentosRegistrados() { return procedimentosRegistrados; }
    public void setProcedimentosRegistrados(Set<ProcedimentoRegistroEntity> procedimentosRegistrados) { this.procedimentosRegistrados = procedimentosRegistrados; }
    public Set<EncaminhamentoRegistroEntity> getEncaminhamentosRegistrados() { return encaminhamentosRegistrados; }
    public void setEncaminhamentosRegistrados(Set<EncaminhamentoRegistroEntity> encaminhamentosRegistrados) { this.encaminhamentosRegistrados = encaminhamentosRegistrados; }

    @PrePersist
    protected void onCreate() {
        this.numeroProntuario = UUID.randomUUID().toString();
        if (this.dataUltimaAtualizacao == null) {
            this.dataUltimaAtualizacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProntuarioEntity that = (ProntuarioEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(numeroProntuario, that.numeroProntuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroProntuario);
    }
}