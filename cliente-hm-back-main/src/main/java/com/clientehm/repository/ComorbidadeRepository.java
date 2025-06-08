package com.clientehm.repository;

import com.clientehm.entity.ComorbidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComorbidadeRepository extends JpaRepository<ComorbidadeEntity, Long> {
}