package com.example.BackEndProjetOCSpringBoot.Interfaces;

import com.example.BackEndProjetOCSpringBoot.DTOs.MessageRequestDTO;
import com.example.BackEndProjetOCSpringBoot.Models.Message;

public interface MessageServiceInterface {

    public Message saveMessage(MessageRequestDTO messageRequestDTO);
    
}
