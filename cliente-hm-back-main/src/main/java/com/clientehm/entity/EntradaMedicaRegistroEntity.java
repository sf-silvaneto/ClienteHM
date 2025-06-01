// src/main/java/com/clientehm/entity/EntradaMedicaRegistroEntity.java
package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "registros_consultas") // Nome da tabela já sugerido
public class EntradaMedicaRegistroEntity { // Pode ser renomeada para ConsultaRegistroEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private ProntuarioEntity prontuario;

    @Column(nullable = false)
    private LocalDateTime dataHoraConsulta;

    @Column(columnDefinition = "TEXT")
    private String motivoConsulta;

    @Column(columnDefinition = "TEXT")
    private String queixasPrincipais;

    private String pressaoArterial;
    private String temperatura;
    private String frequenciaCardiaca;
    private String saturacao;

    // Campos de antecedentes como alergias, comorbidades, etc., foram removidos
    // do formulário de CADA consulta. Se você decidiu que eles NÃO DEVEM MAIS
    // ser armazenados AQUI (nesta entidade de consulta), então remova os campos abaixo:
    // @Column(columnDefinition = "TEXT")
    // private String alergiasDetalhe;
    // private Boolean semAlergiasConhecidas;
    // private Boolean temComorbidades;
    // @Column(columnDefinition = "TEXT")
    // private String comorbidadesDetalhes;
    // private Boolean usaMedicamentosContinuos;
    // @Column(columnDefinition = "TEXT")
    // private String medicamentosContinuosDetalhes;
    // @Column(columnDefinition = "TEXT")
    // private String historicoFamiliarRelevante;

    @Column(columnDefinition = "TEXT")
    private String exameFisico;

    @Column(columnDefinition = "TEXT")
    private String hipoteseDiagnostica;

    @Column(columnDefinition = "TEXT")
    private String condutaPlanoTerapeutico;

    // NOVOS CAMPOS
    @Column(columnDefinition = "TEXT")
    private String detalhesConsulta;

    @Column(columnDefinition = "TEXT")
    private String observacoesConsulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_admin_id")
    private AdministradorEntity responsavelAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_medico_id")
    private MedicoEntity responsavelMedico;

    @Column(nullable = false)
    private String nomeResponsavelDisplay;

    @OneToMany(mappedBy = "entradaMedica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AnexoEntradaMedicaEntity> anexos = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (this.dataHoraConsulta == null) this.dataHoraConsulta = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters e Setters (incluir para os novos campos e remover os de antecedentes se foram excluídos da entidade)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProntuarioEntity getProntuario() { return prontuario; }
    public void setProntuario(ProntuarioEntity prontuario) { this.prontuario = prontuario; }
    public LocalDateTime getDataHoraConsulta() { return dataHoraConsulta; }
    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) { this.dataHoraConsulta = dataHoraConsulta; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }
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

    // Se removeu os campos de antecedentes da entidade, remova os getters/setters abaixo
    // public String getAlergiasDetalhe() { return alergiasDetalhe; }
    // public void setAlergiasDetalhe(String alergiasDetalhe) { this.alergiasDetalhe = alergiasDetalhe; }
    // public Boolean getSemAlergiasConhecidas() { return semAlergiasConhecidas; }
    // public void setSemAlergiasConhecidas(Boolean semAlergiasConhecidas) { this.semAlergiasConhecidas = semAlergiasConhecidas; }
    // public Boolean getTemComorbidades() { return temComorbidades; }
    // public void setTemComorbidades(Boolean temComorbidades) { this.temComorbidades = temComorbidades; }
    // public String getComorbidadesDetalhes() { return comorbidadesDetalhes; }
    // public void setComorbidadesDetalhes(String comorbidadesDetalhes) { this.comorbidadesDetalhes = comorbidadesDetalhes; }
    // public Boolean getUsaMedicamentosContinuos() { return usaMedicamentosContinuos; }
    // public void setUsaMedicamentosContinuos(Boolean usaMedicamentosContinuos) { this.usaMedicamentosContinuos = usaMedicamentosContinuos; }
    // public String getMedicamentosContinuosDetalhes() { return medicamentosContinuosDetalhes; }
    // public void setMedicamentosContinuosDetalhes(String medicamentosContinuosDetalhes) { this.medicamentosContinuosDetalhes = medicamentosContinuosDetalhes; }
    // public String getHistoricoFamiliarRelevante() { return historicoFamiliarRelevante; }
    // public void setHistoricoFamiliarRelevante(String historicoFamiliarRelevante) { this.historicoFamiliarRelevante = historicoFamiliarRelevante; }

    public String getExameFisico() { return exameFisico; }
    public void setExameFisico(String exameFisico) { this.exameFisico = exameFisico; }
    public String getHipoteseDiagnostica() { return hipoteseDiagnostica; }
    public void setHipoteseDiagnostica(String hipoteseDiagnostica) { this.hipoteseDiagnostica = hipoteseDiagnostica; }
    public String getCondutaPlanoTerapeutico() { return condutaPlanoTerapeutico; }
    public void setCondutaPlanoTerapeutico(String condutaPlanoTerapeutico) { this.condutaPlanoTerapeutico = condutaPlanoTerapeutico; }

    public String getDetalhesConsulta() { return detalhesConsulta; }
    public void setDetalhesConsulta(String detalhesConsulta) { this.detalhesConsulta = detalhesConsulta; }
    public String getObservacoesConsulta() { return observacoesConsulta; }
    public void setObservacoesConsulta(String observacoesConsulta) { this.observacoesConsulta = observacoesConsulta; }

    public AdministradorEntity getResponsavelAdmin() { return responsavelAdmin; }
    public void setResponsavelAdmin(AdministradorEntity responsavelAdmin) { this.responsavelAdmin = responsavelAdmin; }
    public MedicoEntity getResponsavelMedico() { return responsavelMedico; }
    public void setResponsavelMedico(MedicoEntity responsavelMedico) { this.responsavelMedico = responsavelMedico; }
    public String getNomeResponsavelDisplay() { return nomeResponsavelDisplay; }
    public void setNomeResponsavelDisplay(String nomeResponsavelDisplay) { this.nomeResponsavelDisplay = nomeResponsavelDisplay; }
    public List<AnexoEntradaMedicaEntity> getAnexos() { return anexos; }
    public void setAnexos(List<AnexoEntradaMedicaEntity> anexos) { this.anexos = anexos; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}