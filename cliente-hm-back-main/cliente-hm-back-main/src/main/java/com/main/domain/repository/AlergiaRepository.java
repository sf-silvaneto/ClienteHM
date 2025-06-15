package com.main.domain.repository;

import com.main.domain.entity.AlergiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlergiaRepository extends JpaRepository<AlergiaEntity, Long> {
}