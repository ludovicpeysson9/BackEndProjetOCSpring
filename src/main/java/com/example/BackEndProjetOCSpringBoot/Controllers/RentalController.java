package com.example.BackEndProjetOCSpringBoot.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.BackEndProjetOCSpringBoot.DTOs.RentalResponseDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.RentalServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.Rental;
import com.example.BackEndProjetOCSpringBoot.Models.User;
import com.example.BackEndProjetOCSpringBoot.Services.UserService;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    @Autowired
    private RentalServiceInterface rentalService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, List<RentalResponseDTO>>> getAllRentals() {
        List<RentalResponseDTO> rentals = rentalService.getAllRentals();
        Map<String, List<RentalResponseDTO>> response = new HashMap<>();
        response.put("rentals", rentals);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalResponseDTO> getRentalById(@PathVariable Integer id) {
        RentalResponseDTO rental = rentalService.getRentalById(id);
        return ResponseEntity.ok(rental);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("picture") MultipartFile picture,
            Principal principal) throws IOException {

        User owner = userService.findByEmail(principal.getName());

        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setOwner(owner);

        rentalService.createRental(rental, picture);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Rental created !");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateRental(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            Principal principal) {

        Rental existingRental = rentalService.getRentalByIdModel(id);

        existingRental.setName(name);
        existingRental.setSurface(surface);
        existingRental.setPrice(price);
        existingRental.setDescription(description);

        rentalService.updateRental(existingRental);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Rental updated !");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public void deleteRental(@PathVariable Integer id) {
        rentalService.deleteRental(id);
    }
}
