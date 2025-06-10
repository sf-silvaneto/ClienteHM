package com.clientehm.repository;

import com.clientehm.entity.ExameRegistroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExameRegistroRepository extends JpaRepository<ExameRegistroEntity, Long> {
}