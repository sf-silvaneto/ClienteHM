package com.clientehm.repository;

import com.clientehm.entity.ConsultaRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntradaMedicaRegistroRepository extends JpaRepository<ConsultaRegistroEntity, Long> {
}