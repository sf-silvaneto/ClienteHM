package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.clientehm.entity.EnderecoEntity;
import com.clientehm.entity.ContatoEntity; // Importar a nova entidade

@Entity
@Table(name = "pacientes")
public class PacienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(unique = true, nullable = false)
    private String cpf;

    private String rg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    // CAMPOS REMOVIDOS:
    // private String telefone;
    // private String email;

    private String nomeMae;
    private String nomePai;

    @Column(nullable = false)
    private LocalDate dataEntrada;

    @Column(unique = true)
    private String cartaoSus;

    @Enumerated(EnumType.STRING)
    private RacaCor racaCor;

    @Enumerated(EnumType.STRING)
    private TipoSanguineo tipoSanguineo;

    private String nacionalidade;
    private String ocupacao;

    @Column(columnDefinition = "TEXT")
    private String alergiasDeclaradas;

    @Column(columnDefinition = "TEXT")
    private String comorbidadesDeclaradas;

    @Column(columnDefinition = "TEXT")
    private String medicamentosContinuos;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private EnderecoEntity endereco;

    // NOVO CAMPO E RELACIONAMENTO
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "contato_id", referencedColumnName = "id", unique = true) // unique=true se um contato pertence a um único paciente
    private ContatoEntity contato;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProntuarioEntity> prontuarios;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (this.dataEntrada == null) {
            this.dataEntrada = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Genero { MASCULINO, FEMININO, OUTRO, NAO_INFORMADO }
    public enum RacaCor { BRANCA, PRETA, PARDA, AMARELA, INDIGENA, NAO_DECLARADO }
    public enum TipoSanguineo { A_POSITIVO, A_NEGATIVO, B_POSITIVO, B_NEGATIVO, AB_POSITIVO, AB_NEGATIVO, O_POSITIVO, O_NEGATIVO, NAO_SABE, NAO_INFORMADO }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public String getNomeMae() { return nomeMae; }
    public void setNomeMae(String nomeMae) { this.nomeMae = nomeMae; }
    public String getNomePai() { return nomePai; }
    public void setNomePai(String nomePai) { this.nomePai = nomePai; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
    public String getCartaoSus() { return cartaoSus; }
    public void setCartaoSus(String cartaoSus) { this.cartaoSus = cartaoSus; }
    public RacaCor getRacaCor() { return racaCor; }
    public void setRacaCor(RacaCor racaCor) { this.racaCor = racaCor; }
    public TipoSanguineo getTipoSanguineo() { return tipoSanguineo; }
    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) { this.tipoSanguineo = tipoSanguineo; }
    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }
    public String getOcupacao() { return ocupacao; }
    public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }
    public EnderecoEntity getEndereco() { return endereco; }
    public void setEndereco(EnderecoEntity endereco) { this.endereco = endereco; }

    // Getter e Setter para ContatoEntity
    public ContatoEntity getContato() { return contato; }
    public void setContato(ContatoEntity contato) { this.contato = contato; }

    public List<ProntuarioEntity> getProntuarios() { return prontuarios; }
    public void setProntuarios(List<ProntuarioEntity> prontuarios) { this.prontuarios = prontuarios; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getAlergiasDeclaradas() { return alergiasDeclaradas; }
    public void setAlergiasDeclaradas(String alergiasDeclaradas) { this.alergiasDeclaradas = alergiasDeclaradas; }
    public String getComorbidadesDeclaradas() { return comorbidadesDeclaradas; }
    public void setComorbidadesDeclaradas(String comorbidadesDeclaradas) { this.comorbidadesDeclaradas = comorbidadesDeclaradas; }
    public String getMedicamentosContinuos() { return medicamentosContinuos; }
    public void setMedicamentosContinuos(String medicamentosContinuos) { this.medicamentosContinuos = medicamentosContinuos; }
}