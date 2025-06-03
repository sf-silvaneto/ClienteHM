package com.clientehm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "contatos")
public class ContatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) // Telefone pode ser opcional
    private String telefone;

    // Email continua sendo único se preenchido, mas pode ser opcional para o paciente
    @Column(unique = true, nullable = true)
    private String email;

    // Mapeamento para PacienteEntity para navegação bidirecional (opcional, mas útil)
    // Se usar, PacienteEntity também precisa de mappedBy em seu @OneToOne com ContatoEntity
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "paciente_id", referencedColumnName = "id", unique = true)
    // private PacienteEntity paciente;
    // Com a configuração atual (ContatoEntity pertencente a PacienteEntity),
    // este mapeamento inverso não é estritamente necessário para a funcionalidade solicitada.
    // Mantendo simples por enquanto.

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

    // Construtores
    public ContatoEntity() {
    }

    public ContatoEntity(String telefone, String email) {
        this.telefone = telefone;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // public PacienteEntity getPaciente() { return paciente; }
    // public void setPaciente(PacienteEntity paciente) { this.paciente = paciente; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContatoEntity that = (ContatoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}