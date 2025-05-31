package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
// Se você criar uma entidade Anexo para múltiplos anexos por entrada:
// import java.util.List;
// import java.util.ArrayList;

@Entity
@Table(name = "entradas_medicas_registros") // Nome da tabela no plural
public class EntradaMedicaRegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private ProntuarioEntity prontuario;

    @Column(nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Column(columnDefinition = "TEXT")
    private String motivoEntrada;

    @Column(columnDefinition = "TEXT")
    private String queixasPrincipais;

    private String pressaoArterial; // Ex: "120/80"
    private String temperatura; // Armazenar como String para flexibilidade de formato ex: "36.5 °C"
    private String frequenciaCardiaca; // Ex: "75 bpm"
    private String saturacao; // Ex: "98%"

    @Column(columnDefinition = "TEXT")
    private String alergiasDetalhe;
    private Boolean semAlergiasConhecidas;

    private Boolean temComorbidades;
    @Column(columnDefinition = "TEXT")
    private String comorbidadesDetalhes;

    private Boolean usaMedicamentosContinuos;
    @Column(columnDefinition = "TEXT")
    private String medicamentosContinuosDetalhes;

    @Column(columnDefinition = "TEXT")
    private String historicoFamiliarRelevante;

    // Responsável pelo registro da entrada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_admin_id")
    private AdministradorEntity responsavelAdmin; // Se um admin puder registrar

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_medico_id")
    private MedicoEntity responsavelMedico; // Se um médico puder registrar

    @Column(nullable = false)
    private String nomeResponsavelDisplay; // Nome a ser exibido (do admin ou médico)


    // Para anexos (se decidir por uma relação direta simples com AnexoEntity)
    // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "anexo_id", referencedColumnName = "id")
    // private AnexoEntity anexoPrincipal;
    // Ou, para múltiplos anexos:
    // @OneToMany(mappedBy = "entradaMedica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<AnexoEntradaMedicaEntity> anexos = new ArrayList<>();


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProntuarioEntity getProntuario() { return prontuario; }
    public void setProntuario(ProntuarioEntity prontuario) { this.prontuario = prontuario; }
    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) { this.dataHoraEntrada = dataHoraEntrada; }
    public String getMotivoEntrada() { return motivoEntrada; }
    public void setMotivoEntrada(String motivoEntrada) { this.motivoEntrada = motivoEntrada; }
    public String getQueixasPrincipais() { return queixasPrincipais; }
    public void setQueixasPrincipais(String queixasPrincipais) { this.queixasPrincipais = queixasPrincipais; }
    public String getPressaoArterial() { return pressaoArterial; }
    public void setPressaoArterial(String pressaoArterial) { this.pressaoArterial = pressaoArterial; }
    public String getTemperatura() { return temperatura; }
    public void setTemperatura(String temperatura) { this.temperatura = temperatura; }
    public String getFrequenciaCardiaca() { return frequenciaCardiaca; }
    public void setFrequenciaCardiaca(String frequenciaCardiaca) { this.frequenciaCardiaca = frequenciaCardiaca; }
    public String getSaturacao() { return saturacao; }
    public void setSaturacao(String saturacao) { this.saturacao = saturacao; }
    public String getAlergiasDetalhe() { return alergiasDetalhe; }
    public void setAlergiasDetalhe(String alergiasDetalhe) { this.alergiasDetalhe = alergiasDetalhe; }
    public Boolean getSemAlergiasConhecidas() { return semAlergiasConhecidas; }
    public void setSemAlergiasConhecidas(Boolean semAlergiasConhecidas) { this.semAlergiasConhecidas = semAlergiasConhecidas; }
    public Boolean getTemComorbidades() { return temComorbidades; }
    public void setTemComorbidades(Boolean temComorbidades) { this.temComorbidades = temComorbidades; }
    public String getComorbidadesDetalhes() { return comorbidadesDetalhes; }
    public void setComorbidadesDetalhes(String comorbidadesDetalhes) { this.comorbidadesDetalhes = comorbidadesDetalhes; }
    public Boolean getUsaMedicamentosContinuos() { return usaMedicamentosContinuos; }
    public void setUsaMedicamentosContinuos(Boolean usaMedicamentosContinuos) { this.usaMedicamentosContinuos = usaMedicamentosContinuos; }
    public String getMedicamentosContinuosDetalhes() { return medicamentosContinuosDetalhes; }
    public void setMedicamentosContinuosDetalhes(String medicamentosContinuosDetalhes) { this.medicamentosContinuosDetalhes = medicamentosContinuosDetalhes; }
    public String getHistoricoFamiliarRelevante() { return historicoFamiliarRelevante; }
    public void setHistoricoFamiliarRelevante(String historicoFamiliarRelevante) { this.historicoFamiliarRelevante = historicoFamiliarRelevante; }
    public AdministradorEntity getResponsavelAdmin() { return responsavelAdmin; }
    public void setResponsavelAdmin(AdministradorEntity responsavelAdmin) { this.responsavelAdmin = responsavelAdmin; }
    public MedicoEntity getResponsavelMedico() { return responsavelMedico; }
    public void setResponsavelMedico(MedicoEntity responsavelMedico) { this.responsavelMedico = responsavelMedico; }
    public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; }
    public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // public List<AnexoEntradaMedicaEntity> getAnexos() { return anexos; }
    // public void setAnexos(List<AnexoEntradaMedicaEntity> anexos) { this.anexos = anexos; }
}