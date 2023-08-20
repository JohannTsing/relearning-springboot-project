package com.johann.binarytea.springDataJpa.repository;

import com.johann.binarytea.hibernate.model.TeaMaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeaMakerRepositoryJpa extends JpaRepository<TeaMaker, Long> {
}
