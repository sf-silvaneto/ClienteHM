package com.clientehm.repository;

import com.clientehm.entity.EntradaMedicaRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// import java.util.List;

@Repository
public interface EntradaMedicaRegistroRepository extends JpaRepository<EntradaMedicaRegistroEntity, Long> {
    // List<EntradaMedicaRegistroEntity> findByProntuarioIdOrderByDataHoraEntradaDesc(Long prontuarioId);
}