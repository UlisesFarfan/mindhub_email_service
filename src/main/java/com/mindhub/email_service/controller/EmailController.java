package com.mindhub.email_service.controller;

import com.mindhub.email_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<String> getMessage(){
        emailService.sendSimpleMessage("ulisesfarfan2142@gmail.com", "Hola", "HOla");
        return ResponseEntity.ok("Message received");
    }

}
