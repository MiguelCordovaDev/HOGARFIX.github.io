package com.hogarfix.repository;

import com.hogarfix.model.Qualification;
import com.hogarfix.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {
    List<Qualification> findByTecnico(Technician tecnico);
}
