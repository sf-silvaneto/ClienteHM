package com.clientehm.repository;

import com.clientehm.entity.MedicamentoContinuoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MedicamentoContinuoRepository extends JpaRepository<MedicamentoContinuoEntity, Long> {
}