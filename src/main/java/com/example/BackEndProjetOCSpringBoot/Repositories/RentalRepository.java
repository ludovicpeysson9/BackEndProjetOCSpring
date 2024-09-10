package com.example.BackEndProjetOCSpringBoot.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BackEndProjetOCSpringBoot.Models.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {
    
}
