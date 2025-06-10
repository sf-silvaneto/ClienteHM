package com.clientehm.repository;

import com.clientehm.entity.MedicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<MedicoEntity, Long>, JpaSpecificationExecutor<MedicoEntity> {
    Optional<MedicoEntity> findByCrm(String crm);
    Page<MedicoEntity> findByNomeCompletoContainingIgnoreCase(String nome, Pageable pageable);

    Page<MedicoEntity> findByDeletedAtIsNull(Pageable pageable); // Atualizado de findByExcludedAtIsNull
    Page<MedicoEntity> findByDeletedAtIsNotNull(Pageable pageable); // Atualizado de findByExcludedAtIsNotNull

    Page<MedicoEntity> findByEspecialidadeIgnoreCase(String especialidade, Pageable pageable);
    Page<MedicoEntity> findByCrmIgnoreCase(String crm, Pageable pageable);
}