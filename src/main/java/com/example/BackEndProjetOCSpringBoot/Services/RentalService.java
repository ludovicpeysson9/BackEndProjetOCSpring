package com.example.BackEndProjetOCSpringBoot.Services;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class RentalService implements RentalServiceInterface {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CloudinaryServiceInterface cloudinaryService;

    private RentalResponseDTO convertToDto(Rental rental) {

        RentalResponseDTO rentalResponseDTO = new RentalResponseDTO();
        rentalResponseDTO.setId(rental.getId());
        rentalResponseDTO.setName(rental.getName());
        rentalResponseDTO.setSurface(rental.getSurface());
        rentalResponseDTO.setPrice(rental.getPrice());
        rentalResponseDTO.setDescription(rental.getDescription());
        rentalResponseDTO.setPicture(rental.getPicture());
        rentalResponseDTO.setOwner_id(rental.getOwner().getId());  
        rentalResponseDTO.setCreated_at(rental.getCreated_at());
        rentalResponseDTO.setUpdated_at(rental.getUpdated_at());

        return rentalResponseDTO;
    }

    @Override
    public List<RentalResponseDTO> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream()
                      .map(this::convertToDto)  
                      .collect(Collectors.toList());
    }

    @Override
    public RentalResponseDTO getRentalById(Integer id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found"));
        return convertToDto(rental);  // Utilise la méthode de conversion
    }

    @Override
    public Rental createRental(Rental rental, MultipartFile file) {
        // Upload image to Cloudinary and get the URL
        String imageUrl = cloudinaryService.uploadImage(file);
        rental.setPicture(imageUrl);  // Set the image URL in the rental object

        // Save the rental object with the image URL in the database
        return rentalRepository.save(rental);
    }

    @Override
    public void deleteRental(Integer id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found"));
        rentalRepository.delete(rental);
    }

    @Override
    public Rental getRentalByIdModel(Integer id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found"));
    }

    @Override
    public Rental updateRental(Rental rental) {
        rental.setUpdated_at(Timestamp.from(Instant.now()));  
        return rentalRepository.save(rental);
    }
}
