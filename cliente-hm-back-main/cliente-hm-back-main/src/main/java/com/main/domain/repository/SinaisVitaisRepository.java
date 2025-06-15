package com.main.domain.repository;

import com.main.domain.entity.SinaisVitaisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SinaisVitaisRepository extends JpaRepository<SinaisVitaisEntity, Long> {
}