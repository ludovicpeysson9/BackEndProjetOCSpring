package com.example.BackEndProjetOCSpringBoot.Interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.BackEndProjetOCSpringBoot.DTOs.RentalResponseDTO;
import com.example.BackEndProjetOCSpringBoot.Models.Rental;

public interface RentalServiceInterface {

    List<RentalResponseDTO> getAllRentals();

    RentalResponseDTO getRentalById(Integer id);

    Rental createRental(Rental rental, MultipartFile file);

    void deleteRental(Integer id);

    Rental getRentalByIdModel(Integer id);  

    Rental updateRental(Rental rental);

}
