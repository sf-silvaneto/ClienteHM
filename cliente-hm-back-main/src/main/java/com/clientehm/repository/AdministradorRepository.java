package com.clientehm.repository;

import com.clientehm.entity.AdministradorEntity; // Import da entidade no pacote correto
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<AdministradorEntity, Long> {
    Optional<AdministradorEntity> findByEmail(String email);
}