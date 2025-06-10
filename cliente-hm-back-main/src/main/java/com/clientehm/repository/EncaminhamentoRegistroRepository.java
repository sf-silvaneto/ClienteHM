package com.clientehm.repository;

import com.clientehm.entity.EncaminhamentoRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncaminhamentoRegistroRepository extends JpaRepository<EncaminhamentoRegistroEntity, Long> {
}