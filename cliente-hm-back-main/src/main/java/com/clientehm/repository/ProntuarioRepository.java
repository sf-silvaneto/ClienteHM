package com.clientehm.repository;

import com.clientehm.entity.ProntuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Para buscas complexas
import java.util.Optional;

public interface ProntuarioRepository extends JpaRepository<ProntuarioEntity, Long>, JpaSpecificationExecutor<ProntuarioEntity> {
    Optional<ProntuarioEntity> findByNumeroProntuario(String numeroProntuario);
    // Adicione outros métodos de busca conforme necessário
}