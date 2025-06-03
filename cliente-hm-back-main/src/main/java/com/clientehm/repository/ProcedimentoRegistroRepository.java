package com.clientehm.repository;

import com.clientehm.entity.ProcedimentoRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedimentoRegistroRepository extends JpaRepository<ProcedimentoRegistroEntity, Long> {
}