package com.example.BackEndProjetOCSpringBoot.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.Principal;

import com.example.BackEndProjetOCSpringBoot.DTOs.RentalResponseDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.RentalServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Interfaces.UserServiceInterface;
import com.example.BackEndProjetOCSpringBoot.Models.Rental;
import com.example.BackEndProjetOCSpringBoot.Models.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller to manage rental-related operations.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals", description = "API for managing rentals")
public class RentalController {

    private final RentalServiceInterface rentalService;
    private final UserServiceInterface userService;

    public RentalController(RentalServiceInterface rentalService, UserServiceInterface userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    /**
     * Retrieve all rentals.
     *
     * @return A list of all rental properties.
     */
    @Operation(summary = "Get all rentals", description = "Retrieve a list of all available rentals.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rentals fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Map<String, List<RentalResponseDTO>>> getAllRentals() {
        List<RentalResponseDTO> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(createSuccessResponse("rentals", rentals));
    }

    /**
     * Retrieve rental by ID.
     *
     * @param id The rental ID.
     * @return The rental details.
     */
    @Operation(summary = "Get rental by ID", description = "Retrieve a rental by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rental not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalById(@PathVariable Integer id) {
        try {
            RentalResponseDTO rental = rentalService.getRentalById(id);
            if (rental != null) {
                return ResponseEntity.ok(rental);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Rental with ID " + id + " not found."));
            }
        } catch (Exception e) {
            return handleException(e, "An error occurred while fetching the rental.");
        }
    }

    /**
     * Create a new rental.
     *
     * @param name        The name of the rental.
     * @param surface     The surface area of the rental.
     * @param price       The price of the rental.
     * @param description The description of the rental.
     * @param picture     The picture file of the rental.
     * @param principal   The authenticated user.
     * @return A response indicating the success or failure of the rental creation.
     */
    @Operation(summary = "Create a new rental", description = "Create a new rental by providing necessary details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("picture") MultipartFile picture,
            Principal principal) {

        try {
            validateRentalParams(name, surface, price, description);

            User owner = userService.findByEmail(principal.getName());
            if (owner == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User not found or unauthorized."));
            }

            Rental rental = createRentalObject(name, surface, price, description, owner);

            rentalService.createRental(rental, picture);

            return ResponseEntity.ok(createSuccessResponse("Rental created successfully"));

        } catch (Exception e) {
            return handleException(e, "An error occurred while creating the rental.");
        }
    }

    /**
     * Update an existing rental.
     *
     * @param id          The rental ID.
     * @param name        The new name of the rental.
     * @param surface     The new surface area of the rental.
     * @param price       The new price of the rental.
     * @param description The new description of the rental.
     * @param principal   The authenticated user.
     * @return A response indicating the success or failure of the rental update.
     */
    @Operation(summary = "Update a rental", description = "Update an existing rental by providing new details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rental not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRental(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            Principal principal) {

        try {
            validateRentalParams(name, surface, price, description);

            Rental existingRental = rentalService.getRentalByIdModel(id);
            if (existingRental == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Rental with ID " + id + " not found."));
            }

            updateRentalProperties(existingRental, name, surface, price, description);

            rentalService.updateRental(existingRental);

            return ResponseEntity.ok(createSuccessResponse("Rental updated successfully"));

        } catch (Exception e) {
            return handleException(e, "An error occurred while updating the rental.");
        }
    }

    /**
     * Delete a rental by its ID.
     *
     * @param id The rental ID.
     * @return A response indicating the success or failure of the rental deletion.
     */
    @Operation(summary = "Delete a rental", description = "Delete a rental by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rental not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Integer id) {
        try {
            rentalService.deleteRental(id);
            return ResponseEntity.ok(createSuccessResponse("Rental deleted successfully"));
        } catch (Exception e) {
            return handleException(e, "An error occurred while deleting the rental.");
        }
    }

    private Rental createRentalObject(String name, Double surface, Double price, String description, User owner) {
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setOwner(owner);
        return rental;
    }

    private void updateRentalProperties(Rental rental, String name, Double surface, Double price, String description) {
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
    }

    private ResponseEntity<Map<String, String>> handleException(Exception e, String defaultMessage) {
        System.err.println("Error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(defaultMessage));
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    private <T> Map<String, T> createSuccessResponse(String key, T data) {
        Map<String, T> response = new HashMap<>();
        response.put(key, data);
        return response;
    }

    private Map<String, String> createErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }

    private void validateRentalParams(String name, Double surface, Double price, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (surface == null || surface <= 0) {
            throw new IllegalArgumentException("Surface must be greater than 0");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
    }
}
