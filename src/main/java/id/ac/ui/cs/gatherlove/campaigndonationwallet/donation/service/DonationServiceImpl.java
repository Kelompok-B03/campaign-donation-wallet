package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final WebClient requestWebClient;

    @Autowired
    public DonationServiceImpl(DonationRepository donationRepository, WebClient requestWebClient) {
        this.donationRepository = donationRepository;
        this.requestWebClient = requestWebClient;
    }

    @Override
    @Transactional
    public Donation createDonation(String campaignId, Float amount, String message) {
        // Get user ID from JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = ((AbstractAuthenticationToken) authentication).getPrincipal() instanceof Jwt
                ? (Jwt) ((AbstractAuthenticationToken) authentication).getPrincipal()
                : null;

        if (jwt == null) {
            throw new RuntimeException("Invalid JWT token: missing principal");
        }

        UUID userId = UUID.fromString(jwt.getSubject());
        String token = jwt.getTokenValue();

        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        // Prepare the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);
        requestBody.put("campaignId", campaignId);
        requestBody.put("amount", BigDecimal.valueOf((double) amount));
        requestBody.put("description", message);

        // Send request to payment
        ResponseEntity<String> paymentResponse = requestWebClient.post()
            .uri("/api/donate")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(response -> response.isError(), clientResponse ->
                clientResponse.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new RuntimeException("Error: " + errorMessage))))
            .toEntity(String.class)
            .block();

        // Create donation if payment succeed
        if (paymentResponse.getStatusCode().is2xxSuccessful()) {
            Donation donation = new Donation(userId, campaignId, amount, message);
            return donationRepository.save(donation);
        } else {
            throw new RuntimeException("Failed to create donation. HTTP Status: " + paymentResponse.getStatusCode());
        }
    }

    @Override
    @Transactional
    public Donation updateStatus(UUID donationId) {
        Donation donation = findDonationById(donationId);

        donation.getState().updateStatus();

        return donationRepository.save(donation);
    }

    @Override
    @Transactional
    public void deleteDonation(UUID donationId) {
        Donation donation = findDonationById(donationId);
        donationRepository.delete(donation);
    }

    @Override
    @Transactional(readOnly = true)
    public Donation getDonationById(UUID donationId) {
        Donation donation = donationRepository.findByDonationId(donationId);
        if (donation == null) {
            throw new IllegalArgumentException("Donation with ID " + donationId + " not found");
        }
        return donation;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> getDonationsByUserId(UUID userId) {
        return donationRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> getDonationsByCampaignId(String campaignId) {
        return donationRepository.findByCampaignId(campaignId);
    }

    // Helper 'find-by-Id' method
    private Donation findDonationById(UUID donationId) {
        return donationRepository.findByDonationId(donationId);
    }
}
