package com.example.notifly.client;

import com.example.notifly.dto.ContactDTO;
import com.example.notifly.exception.ResourceNotFoundException;
import com.example.notifly.service.TokenService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ContactlyClient {

    private final RestTemplate restTemplate;
    private final TokenService tokenService;
    private final String contactlyBaseUrl;

    public ContactlyClient(RestTemplate restTemplate,
                           TokenService tokenService,
                           @Value("${contactly.base-url}") String contactlyBaseUrl) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.contactlyBaseUrl = contactlyBaseUrl;
    }

    @CircuitBreaker(name = "contactlyService", fallbackMethod = "fetchAllContactsFallback")
    public List<ContactDTO> fetchAllContacts(String username, String password) {
        String token = tokenService.getToken(username, password);

        String url = contactlyBaseUrl + "/contacts";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ContactDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, ContactDTO[].class);

        return List.of(Objects.requireNonNull(response.getBody()));
    }

    @CircuitBreaker(name = "contactlyService", fallbackMethod = "fetchContactByIdFallback")
    public ContactDTO fetchContactById(String username, String password, Long contactId) {
        String token = tokenService.getToken(username, password);

        String url = contactlyBaseUrl + "/contacts/" + contactId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<ContactDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, ContactDTO.class);

        return response.getBody();
    }

    private List<ContactDTO> fetchAllContactsFallback(Throwable throwable) {
        log.error("Failed to fetch all contacts: {}", throwable.getMessage());
        throw new ResourceNotFoundException("Failed to fetch contacts: " + throwable.getMessage());
    }

    private ContactDTO fetchContactByIdFallback(String username, String password, Long contactId, Throwable throwable) {
        log.error("Failed to fetch contact with ID {} for user {}: {}", contactId, username, throwable.getMessage());
        throw new ResourceNotFoundException("Failed to fetch contact with ID " + contactId + ": " + throwable.getMessage());
    }
}
