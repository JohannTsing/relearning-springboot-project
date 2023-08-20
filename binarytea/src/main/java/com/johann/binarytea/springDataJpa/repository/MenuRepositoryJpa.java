package com.johann.binarytea.springDataJpa.repository;

import com.johann.binarytea.hibernate.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
public interface MenuRepositoryJpa extends JpaRepository<MenuItem,Long> {
}
