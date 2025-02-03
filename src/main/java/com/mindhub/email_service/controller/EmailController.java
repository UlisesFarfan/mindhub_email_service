package com.mindhub.email_service.controller;

import com.mindhub.email_service.service.AppService;
import com.mindhub.email_service.service.EmailService;
import com.mindhub.email_service.utils.PDFService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppService appService;

    @Autowired
    PDFService pdfService;

    @PostMapping("/notifyOrder")
    public ResponseEntity<?> sendOrderResume(HttpServletRequest request, @RequestBody Map<String, Object> body) throws Exception {
        String orderId = body.get("id").toString();
        String token = appService.extraerToken(request);
        String userUrl = "http://localhost:8080/api/users/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Map<String, Object>> userdata = new HttpEntity<>(headers);
        ResponseEntity<Map> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, userdata, Map.class);
        Map userDetails = userResponse.getBody();

        assert userDetails != null;
        String username = (String) userDetails.get("username");
        String userEmail = (String) userDetails.get("email");
        byte[] pdf = pdfService.generatePurchasePDF(body, username, userEmail, token);
        System.out.println(userResponse);
        emailService.sendSimpleMessage(userEmail, "Gracias por su compra", "desde Micro Service APP le agradecemos mucho por la compra, adjuntado estan los detalles", pdf, orderId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/welcome")
    public ResponseEntity<?> welcome(@RequestParam(required = true) String email)  {
        emailService.sendSimpleMessage(email, "Bienvenido a Micro Services App", "Bienvenido", null, null);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
