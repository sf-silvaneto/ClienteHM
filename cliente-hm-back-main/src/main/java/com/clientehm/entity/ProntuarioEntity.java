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
    private String numeroProntuario; // Gerado automaticamente ou manualmente?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTratamento tipoTratamento;

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataFim; // Opcional

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProntuario status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAtualizacao; // Renomeado de updatedAt para clareza no domínio

    // Exemplo de relacionamento com Histórico Médico (similar para Medicações, Exames, Anotações)
    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistoricoMedicoEntity> historicoMedico = new ArrayList<>();

    // Adicionar outros relacionamentos (MedicacaoEntity, ExameEntity, AnotacaoEntity)

    @PrePersist
    protected void onCreate() {
        createdAt = dataUltimaAtualizacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusProntuario.ATIVO;
        }
        // Lógica para gerar numeroProntuario se necessário
    }

    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }

    // Enums (podem ser em arquivos separados ou internos)
    public enum TipoTratamento {
        TERAPIA_INDIVIDUAL, TERAPIA_CASAL, TERAPIA_GRUPO, TERAPIA_FAMILIAR, OUTRO
    }

    public enum StatusProntuario {
        ATIVO, INATIVO, ARQUIVADO
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroProntuario() { return numeroProntuario; }
    public void setNumeroProntuario(String numeroProntuario) { this.numeroProntuario = numeroProntuario; }
    public PacienteEntity getPaciente() { return paciente; }
    public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }
    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public StatusProntuario getStatus() { return status; }
    public void setStatus(StatusProntuario status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }
    public List<HistoricoMedicoEntity> getHistoricoMedico() { return historicoMedico; }
    public void setHistoricoMedico(List<HistoricoMedicoEntity> historicoMedico) { this.historicoMedico = historicoMedico; }
}