package com.main.domain.repository;

import com.main.domain.entity.ExameRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExameRegistroRepository extends JpaRepository<ExameRegistroEntity, Long> {
}