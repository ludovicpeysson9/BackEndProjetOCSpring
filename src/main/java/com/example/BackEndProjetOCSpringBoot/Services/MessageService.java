package com.example.BackEndProjetOCSpringBoot.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.BackEndProjetOCSpringBoot.DTOs.MessageRequestDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.MessageServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.Message;
import com.example.BackEndProjetOCSpringBoot.Repositories.MessageRepository;
import com.example.BackEndProjetOCSpringBoot.Repositories.RentalRepository;
import com.example.BackEndProjetOCSpringBoot.Repositories.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class MessageService implements MessageServiceInterface {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Message saveMessage(MessageRequestDTO messageRequestDTO) {
        Message message = new Message();
        // Vérification que les identifiants sont du bon type, ici on assume que les identifiants sont des Long
        message.setRental(rentalRepository.findById(messageRequestDTO.getRental_id()).orElseThrow());
        message.setUser(userRepository.findById(messageRequestDTO.getUser_id()).orElseThrow());
        message.setMessage(messageRequestDTO.getMessage());
        message.setCreated_at(Timestamp.from(Instant.now()));
        message.setUpdated_at(Timestamp.from(Instant.now()));
        return messageRepository.save(message);
    }
}