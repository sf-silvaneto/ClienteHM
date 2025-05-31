package com.clientehm.repository;

import com.clientehm.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {
    Optional<PacienteEntity> findByCpf(String cpf);
    Optional<PacienteEntity> findByEmail(String email);
}