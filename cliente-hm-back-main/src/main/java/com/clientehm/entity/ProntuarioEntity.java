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
    @JoinColumn(name = "medico_id", nullable = false) // Médico responsável pelo prontuário/tratamento
    private MedicoEntity medicoResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_criador_id") // Administrador que criou o registro do prontuário
    private AdministradorEntity administradorCriador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTratamento tipoTratamento;

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataFim; // Opcional, pode ser usado para indicar o fim de um tratamento específico

    private LocalDate dataAlta; // Data da alta do paciente para este tratamento/prontuário

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
    private List<EntradaMedicaRegistroEntity> entradasMedicas = new ArrayList<>(); // Nova lista

    // TODO: Adicionar outras listas conforme sua modelagem final:
    // @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<MedicacaoEntity> medicacoes = new ArrayList<>();

    // @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<ExameEntity> exames = new ArrayList<>();

    // @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<AnotacaoEntity> anotacoes = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = dataUltimaAtualizacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusProntuario.ATIVO;
        }
        // A geração do numeroProntuario é feita no ProntuarioService
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
        ATIVO, INATIVO, ARQUIVADO, ALTA // Status ALTA pode ser usado aqui
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

    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }

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

    // TODO: Getters e Setters para medicacoes, exames, anotacoes
}