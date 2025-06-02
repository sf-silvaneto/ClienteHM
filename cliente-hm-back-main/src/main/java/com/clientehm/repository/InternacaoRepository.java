// src/main/java/com/clientehm/repository/InternacaoRepository.java
package com.clientehm.repository;

import com.clientehm.entity.InternacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternacaoRepository extends JpaRepository<InternacaoEntity, Long> {
    // Métodos de busca específicos para internações, se necessário
}