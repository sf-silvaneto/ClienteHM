package com.main.domain.repository;

import com.main.domain.entity.EncaminhamentoRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncaminhamentoRegistroRepository extends JpaRepository<EncaminhamentoRegistroEntity, Long> {
}