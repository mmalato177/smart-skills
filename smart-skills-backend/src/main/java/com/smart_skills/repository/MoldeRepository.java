package com.smart_skills.repository;

import com.smart_skills.models.Molde;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoldeRepository extends JpaRepository<Molde,Long> {

    Optional<Molde> findByWkzBauerNr(Long wkzBauerNr);
}
