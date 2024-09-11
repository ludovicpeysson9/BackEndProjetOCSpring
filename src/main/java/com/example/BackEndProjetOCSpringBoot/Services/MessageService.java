package com.example.BackEndProjetOCSpringBoot.Services;

import com.example.BackEndProjetOCSpringBoot.DTOs.MessageRequestDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.MessageServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.Message;
import com.example.BackEndProjetOCSpringBoot.Models.Rental;
import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Repositories.MessageRepository;
import com.example.BackEndProjetOCSpringBoot.Repositories.RentalRepository;
import com.example.BackEndProjetOCSpringBoot.Repositories.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Service for handling message operations between users and rentals.
 */
@Service
public class MessageService implements MessageServiceInterface {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, RentalRepository rentalRepository,
            UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Saves a message based on the provided DTO, associating it with a specific
     * user and rental.
     * 
     * @param messageRequestDTO Data transfer object containing message details.
     * @return The persisted message entity.
     * @throws IllegalArgumentException if either user or rental does not exist.
     */
    @Override
    @Transactional
    public Message saveMessage(MessageRequestDTO messageRequestDTO) {
        Rental rental = findRentalById(messageRequestDTO.getRental_id());
        User user = findUserById(messageRequestDTO.getUser_id());

        Message message = buildMessage(messageRequestDTO, rental, user);

        return messageRepository.save(message);
    }

    /**
     * Retrieves a rental entity by its ID.
     * 
     * @param rentalId The ID of the rental.
     * @return The found rental entity.
     * @throws IllegalArgumentException if the rental does not exist.
     */
    private Rental findRentalById(Integer rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental with ID " + rentalId + " not found."));
    }

    /**
     * Retrieves a user entity by its ID.
     * 
     * @param userId The ID of the user.
     * @return The found user entity.
     * @throws IllegalArgumentException if the user does not exist.
     */
    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
    }

    /**
     * Constructs a message entity from the provided DTO, rental, and user.
     * 
     * @param messageRequestDTO Data transfer object containing the message content.
     * @param rental            The rental associated with the message.
     * @param user              The user who is sending the message.
     * @return The constructed message entity.
     */
    private Message buildMessage(MessageRequestDTO messageRequestDTO, Rental rental, User user) {
        Message message = new Message();
        message.setRental(rental);
        message.setUser(user);
        message.setMessage(messageRequestDTO.getMessage());
        message.setCreated_at(Timestamp.from(Instant.now()));
        message.setUpdated_at(Timestamp.from(Instant.now()));
        return message;
    }
}
