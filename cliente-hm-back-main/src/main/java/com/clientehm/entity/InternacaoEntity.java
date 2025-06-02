// src/main/java/com/clientehm/entity/InternacaoEntity.java
package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internacoes")
public class InternacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private ProntuarioEntity prontuario;

    @Column(nullable = false)
    private LocalDateTime dataAdmissao;

    @Column(columnDefinition = "TEXT")
    private String motivoInternacao;

    @Lob // Para textos mais longos
    @Column(columnDefinition = "TEXT")
    private String historiaDoencaAtual; // Detalhes da internação

    // Informações do responsável pela admissão (pode ser admin ou médico)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_admissao_admin_id")
    private AdministradorEntity responsavelAdmissaoAdmin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_admissao_medico_id")
    private MedicoEntity responsavelAdmissaoMedico;

    @Column(nullable = false)
    private String nomeResponsavelAdmissaoDisplay;


    private LocalDateTime dataAltaPrevista;
    private LocalDateTime dataAltaEfetiva;

    @Column(columnDefinition = "TEXT")
    private String resumoAlta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_responsavel_alta_id")
    private MedicoEntity medicoResponsavelAlta; // Médico que deu alta

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relacionamento com Anexos da Internação (se necessário criar AnexoInternacaoEntity)
    // @OneToMany(mappedBy = "internacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private List<AnexoInternacaoEntity> anexos = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProntuarioEntity getProntuario() { return prontuario; }
    public void setProntuario(ProntuarioEntity prontuario) { this.prontuario = prontuario; }
    public LocalDateTime getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDateTime dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public String getMotivoInternacao() { return motivoInternacao; }
    public void setMotivoInternacao(String motivoInternacao) { this.motivoInternacao = motivoInternacao; }
    public String getHistoriaDoencaAtual() { return historiaDoencaAtual; }
    public void setHistoriaDoencaAtual(String historiaDoencaAtual) { this.historiaDoencaAtual = historiaDoencaAtual; }
    public AdministradorEntity getResponsavelAdmissaoAdmin() { return responsavelAdmissaoAdmin; }
    public void setResponsavelAdmissaoAdmin(AdministradorEntity responsavelAdmissaoAdmin) { this.responsavelAdmissaoAdmin = responsavelAdmissaoAdmin; }
    public MedicoEntity getResponsavelAdmissaoMedico() { return responsavelAdmissaoMedico; }
    public void setResponsavelAdmissaoMedico(MedicoEntity responsavelAdmissaoMedico) { this.responsavelAdmissaoMedico = responsavelAdmissaoMedico; }
    public String getNomeResponsavelAdmissaoDisplay() { return nomeResponsavelAdmissaoDisplay; }
    public void setNomeResponsavelAdmissaoDisplay(String nomeResponsavelAdmissaoDisplay) { this.nomeResponsavelAdmissaoDisplay = nomeResponsavelAdmissaoDisplay; }
    public LocalDateTime getDataAltaPrevista() { return dataAltaPrevista; }
    public void setDataAltaPrevista(LocalDateTime dataAltaPrevista) { this.dataAltaPrevista = dataAltaPrevista; }
    public LocalDateTime getDataAltaEfetiva() { return dataAltaEfetiva; }
    public void setDataAltaEfetiva(LocalDateTime dataAltaEfetiva) { this.dataAltaEfetiva = dataAltaEfetiva; }
    public String getResumoAlta() { return resumoAlta; }
    public void setResumoAlta(String resumoAlta) { this.resumoAlta = resumoAlta; }
    public MedicoEntity getMedicoResponsavelAlta() { return medicoResponsavelAlta; }
    public void setMedicoResponsavelAlta(MedicoEntity medicoResponsavelAlta) { this.medicoResponsavelAlta = medicoResponsavelAlta; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // public List<AnexoInternacaoEntity> getAnexos() { return anexos; }
    // public void setAnexos(List<AnexoInternacaoEntity> anexos) { this.anexos = anexos; }
}