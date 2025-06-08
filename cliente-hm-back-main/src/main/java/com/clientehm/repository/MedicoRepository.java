package com.clientehm.repository;

import com.clientehm.entity.MedicoEntity;
// import com.clientehm.entity.StatusMedico; // Remova esta importação
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<MedicoEntity, Long>, JpaSpecificationExecutor<MedicoEntity> {
    Optional<MedicoEntity> findByCrm(String crm);
    Page<MedicoEntity> findByNomeCompletoContainingIgnoreCase(String nome, Pageable pageable);

    // Antigo: Page<MedicoEntity> findByStatus(StatusMedico status, Pageable pageable);
    // NOVO: Métodos para buscar por status Ativo/Inativo usando excludedAt
    Page<MedicoEntity> findByExcludedAtIsNull(Pageable pageable); // Médicos Ativos (excludedAt é NULL)
    Page<MedicoEntity> findByExcludedAtIsNotNull(Pageable pageable); // Médicos Inativos (excludedAt NÃO é NULL)

    Page<MedicoEntity> findByEspecialidadeIgnoreCase(String especialidade, Pageable pageable);
    Page<MedicoEntity> findByCrmIgnoreCase(String crm, Pageable pageable);
}