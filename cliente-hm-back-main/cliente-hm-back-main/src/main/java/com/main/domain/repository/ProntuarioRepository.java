package com.main.domain.repository;

import com.main.domain.entity.ProntuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<ProntuarioEntity, Long>, JpaSpecificationExecutor<ProntuarioEntity> {
    Optional<ProntuarioEntity> findByPacienteId(Long pacienteId);

    @Query("SELECT p FROM ProntuarioEntity p " +
            "LEFT JOIN FETCH p.paciente pac " +
            "LEFT JOIN FETCH pac.endereco " +
            "LEFT JOIN FETCH pac.contato " +
            "LEFT JOIN FETCH p.medicoResponsavel " +
            "LEFT JOIN FETCH p.administradorCriador " +
            "LEFT JOIN FETCH p.consultas " +
            "LEFT JOIN FETCH p.examesRegistrados " +
            "LEFT JOIN FETCH p.procedimentosRegistrados " +
            "LEFT JOIN FETCH p.encaminhamentosRegistrados " +
            "WHERE p.id = :id")
    Optional<ProntuarioEntity> findByIdFetchingCollections(@Param("id") Long id);
}