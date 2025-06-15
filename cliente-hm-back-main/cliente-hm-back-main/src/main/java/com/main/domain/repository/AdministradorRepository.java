package com.main.domain.repository;

import com.main.domain.entity.AdministradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<AdministradorEntity, Long> {
    Optional<AdministradorEntity> findByEmail(String email);
}