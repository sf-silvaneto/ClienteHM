package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pacientes")
public class PacienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... outros campos existentes ...
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

    @Column(nullable = false)
    private String telefone;

    @Column(unique = true, nullable = false)
    private String email;

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

    // NOVOS CAMPOS PARA O CABEÇALHO FIXO
    @Column(columnDefinition = "TEXT")
    private String alergiasDeclaradas;

    @Column(columnDefinition = "TEXT")
    private String comorbidadesDeclaradas;

    @Column(columnDefinition = "TEXT")
    private String medicamentosContinuos;
    // FIM DOS NOVOS CAMPOS

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="logradouro", column=@Column(name="endereco_logradouro")),
            @AttributeOverride(name="numero", column=@Column(name="endereco_numero")),
            @AttributeOverride(name="complemento", column=@Column(name="endereco_complemento")),
            @AttributeOverride(name="bairro", column=@Column(name="endereco_bairro")),
            @AttributeOverride(name="cidade", column=@Column(name="endereco_cidade")),
            @AttributeOverride(name="estado", column=@Column(name="endereco_estado")),
            @AttributeOverride(name="cep", column=@Column(name="endereco_cep"))
    })
    private Endereco endereco;

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

    // --- Getters e Setters para campos existentes ...
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
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public List<ProntuarioEntity> getProntuarios() { return prontuarios; }
    public void setProntuarios(List<ProntuarioEntity> prontuarios) { this.prontuarios = prontuarios; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // --- Getters e Setters para os NOVOS CAMPOS ---
    public String getAlergiasDeclaradas() { return alergiasDeclaradas; }
    public void setAlergiasDeclaradas(String alergiasDeclaradas) { this.alergiasDeclaradas = alergiasDeclaradas; }
    public String getComorbidadesDeclaradas() { return comorbidadesDeclaradas; }
    public void setComorbidadesDeclaradas(String comorbidadesDeclaradas) { this.comorbidadesDeclaradas = comorbidadesDeclaradas; }
    public String getMedicamentosContinuos() { return medicamentosContinuos; }
    public void setMedicamentosContinuos(String medicamentosContinuos) { this.medicamentosContinuos = medicamentosContinuos; }
}