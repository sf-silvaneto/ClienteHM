package com.main.domain.repository;

import com.main.domain.entity.ComorbidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComorbidadeRepository extends JpaRepository<ComorbidadeEntity, Long> {
}