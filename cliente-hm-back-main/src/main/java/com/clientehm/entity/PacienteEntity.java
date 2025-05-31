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

    // Se nomeMae for obrigatório, adicionar @Column(nullable = false)
    private String nomeMae;
    private String nomePai; // Opcional

    // Se esta data é sempre fornecida na criação, o default no @PrePersist pode ser removido.
    // Se é para ser a data atual caso não seja fornecida, e a coluna permite nulos temporariamente,
    // a lógica no @PrePersist está ok, mas nullable=false é um pouco conflitante.
    // Idealmente, o service layer define este valor.
    @Column(nullable = false)
    private LocalDate dataEntrada;

    @Column(unique = true)
    private String cartaoSus; // Opcional

    @Enumerated(EnumType.STRING)
    private RacaCor racaCor; // Opcional

    @Enumerated(EnumType.STRING)
    private TipoSanguineo tipoSanguineo; // Opcional

    private String nacionalidade; // Opcional
    private String ocupacao; // Opcional

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
    private Endereco endereco; // A obrigatoriedade dos campos de Endereco agora é definida na classe Endereco.java

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProntuarioEntity> prontuarios;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (this.dataEntrada == null) { // Se dataEntrada é obrigatória, este if é redundante ou o service deve preencher.
            this.dataEntrada = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Genero {
        MASCULINO, FEMININO, OUTRO, NAO_INFORMADO
    }

    public enum RacaCor {
        BRANCA, PRETA, PARDA, AMARELA, INDIGENA, NAO_DECLARADO
    }

    public enum TipoSanguineo {
        A_POSITIVO, A_NEGATIVO,
        B_POSITIVO, B_NEGATIVO,
        AB_POSITIVO, AB_NEGATIVO,
        O_POSITIVO, O_NEGATIVO,
        NAO_SABE, NAO_INFORMADO
    }

    // Getters e Setters (conforme você já os tem)
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
}