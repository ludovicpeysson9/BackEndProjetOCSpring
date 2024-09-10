package com.example.BackEndProjetOCSpringBoot.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.BackEndProjetOCSpringBoot.Models.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

}
