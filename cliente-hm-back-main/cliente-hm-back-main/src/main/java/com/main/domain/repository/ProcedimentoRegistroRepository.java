package com.main.domain.repository;

import com.main.domain.entity.ProcedimentoRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedimentoRegistroRepository extends JpaRepository<ProcedimentoRegistroEntity, Long> {
}