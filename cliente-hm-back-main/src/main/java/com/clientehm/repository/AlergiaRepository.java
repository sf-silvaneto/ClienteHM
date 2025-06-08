package com.clientehm.repository;

import com.clientehm.entity.AlergiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlergiaRepository extends JpaRepository<AlergiaEntity, Long> {
}