package com.example.BackEndProjetOCSpringBoot.Controllers;

import com.example.BackEndProjetOCSpringBoot.DTOs.MessageRequestDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.MessageServiceInterface;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller to manage message-related operations.
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API for managing user messages")
public class MessageController {

    private final MessageServiceInterface messageService;

    public MessageController(MessageServiceInterface messageService) {
        this.messageService = messageService;
    }

    /**
     * Send a message.
     *
     * @param messageRequestDTO The message request details.
     * @return A response indicating the success or failure of the message sending.
     */
    @Operation(summary = "Send a message", description = "Send a message from a user to a rental.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequestDTO messageRequestDTO) {
        try {
            if (messageRequestDTO.getMessage() == null || messageRequestDTO.getMessage().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Message content is required"));
            }
            if (messageRequestDTO.getUser_id() == null || messageRequestDTO.getRental_id() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("User ID and Rental ID are required"));
            }

            messageService.saveMessage(messageRequestDTO);

            return ResponseEntity.ok(createSuccessResponse("Message sent successfully"));

        } catch (Exception e) {
            System.err.println("Error while sending message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An error occurred while sending the message"));
        }
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    private Map<String, String> createErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", errorMessage);
        return errorResponse;
    }
}
