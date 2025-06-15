package com.main.domain.repository;

import com.main.domain.entity.PacienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long>, JpaSpecificationExecutor<PacienteEntity> {
    Optional<PacienteEntity> findByCpf(String cpf);
    Page<PacienteEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<PacienteEntity> findByCpfStartingWith(String cpf, Pageable pageable);

    @EntityGraph(attributePaths = {"endereco", "contato", "alergias", "comorbidades", "medicamentosContinuos"})
    Optional<PacienteEntity> findById(Long id);
}