package com.main.domain.repository;

import com.main.domain.entity.ContatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContatoRepository extends JpaRepository<ContatoEntity, Long> {
    Optional<ContatoEntity> findByEmail(String email);
    Optional<ContatoEntity> findByTelefone(String telefone);
}