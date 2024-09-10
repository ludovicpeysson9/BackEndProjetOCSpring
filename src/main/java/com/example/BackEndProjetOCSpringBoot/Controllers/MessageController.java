package com.example.BackEndProjetOCSpringBoot.Controllers;

import com.example.BackEndProjetOCSpringBoot.DTOs.MessageRequestDTO;
import com.example.BackEndProjetOCSpringBoot.Interfaces.MessageServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageServiceInterface messageService;

    @PostMapping
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody MessageRequestDTO messageRequestDTO) {
        messageService.saveMessage(messageRequestDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message send with success");
        return ResponseEntity.ok(response);
    }
}
