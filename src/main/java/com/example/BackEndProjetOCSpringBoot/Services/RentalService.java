package com.example.BackEndProjetOCSpringBoot.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.BackEndProjetOCSpringBoot.DTOs.RentalResponseDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.CloudinaryServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Interfaces.RentalServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.Rental;
import com.example.BackEndProjetOCSpringBoot.Repositories.RentalRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service handling rental operations, managing CRUD operations and image
 * handling for rentals.
 */
@Service
public class RentalService implements RentalServiceInterface {

    private final RentalRepository rentalRepository;
    private final CloudinaryServiceInterface cloudinaryService;

    public RentalService(RentalRepository rentalRepository, CloudinaryServiceInterface cloudinaryService) {
        this.rentalRepository = rentalRepository;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Retrieves all rentals and converts them to DTO format.
     * 
     * @return List of all rental DTOs.
     */
    @Override
    public List<RentalResponseDTO> getAllRentals() {
        return rentalRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches a single rental by its ID and converts it to DTO format.
     * 
     * @param id The ID of the rental.
     * @return The rental DTO.
     */
    @Override
    public RentalResponseDTO getRentalById(Integer id) {
        Rental rental = findRentalById(id);
        return convertToDto(rental);
    }

    /**
     * Creates a new rental and uploads its image to a cloud service.
     * 
     * @param rental The rental to create.
     * @param file   The image file associated with the rental.
     * @return The newly created rental.
     */
    @Override
    public Rental createRental(Rental rental, MultipartFile file) {
        String imageUrl = uploadRentalImage(file);
        rental.setPicture(imageUrl);
        return saveRental(rental);
    }

    /**
     * Deletes a rental by its ID.
     * 
     * @param id The ID of the rental to delete.
     */
    @Override
    public void deleteRental(Integer id) {
        Rental rental = findRentalById(id);
        rentalRepository.delete(rental);
    }

    /**
     * Fetches a rental model by its ID for operations like update.
     * 
     * @param id The ID of the rental.
     * @return The rental model.
     */
    @Override
    public Rental getRentalByIdModel(Integer id) {
        return findRentalById(id);
    }

    /**
     * Updates a rental and sets its update timestamp to now.
     * 
     * @param rental The rental to update.
     * @return The updated rental.
     */
    @Override
    public Rental updateRental(Rental rental) {
        rental.setUpdated_at(Timestamp.from(Instant.now()));
        return saveRental(rental);
    }

    private RentalResponseDTO convertToDto(Rental rental) {
        return RentalResponseDTO.builder()
                .id(rental.getId())
                .name(rental.getName())
                .surface(rental.getSurface())
                .price(rental.getPrice())
                .description(rental.getDescription())
                .picture(rental.getPicture())
                .owner_id(rental.getOwner().getId())
                .created_at(rental.getCreated_at())
                .updated_at(rental.getUpdated_at())
                .build();
    }

    private Rental findRentalById(Integer id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with ID: " + id));
    }

    private String uploadRentalImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required.");
        }
        return cloudinaryService.uploadImage(file);
    }

    private Rental saveRental(Rental rental) {
        return rentalRepository.save(rental);
    }
}
