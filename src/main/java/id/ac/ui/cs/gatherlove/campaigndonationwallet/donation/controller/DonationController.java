package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.dto.DonationRequest;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.exception.ExternalServiceException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PreAuthorize("hasRole('DONOR')")
    @PostMapping
    public ResponseEntity<?> createDonation(@RequestBody DonationRequest request) {
        try {
            Donation donation = donationService.createDonation(
                    request.getCampaignId(),
                    request.getAmount(),
                    request.getMessage()
            );
            return new ResponseEntity<>(donation, HttpStatus.CREATED);
        } catch (ExternalServiceException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated")
    @PutMapping("/{donationId}/status")
    public ResponseEntity<?> updateDonationStatus(@PathVariable UUID donationId) {
        try {
            Donation donation = donationService.updateStatus(donationId);
            return new ResponseEntity<>(donation, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{donationId}")
    public ResponseEntity<?> deleteDonation(@PathVariable UUID donationId) {
        try {
            donationService.deleteDonation(donationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated")
    @GetMapping("/{donationId}")
    public ResponseEntity<?> getDonationById(@PathVariable UUID donationId) {
        try {
            Donation donation = donationService.getDonationById(donationId);
            return new ResponseEntity<>(donation, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDonationsByUserId(@PathVariable UUID userId) {
        try {
            List<Donation> donations = donationService.getDonationsByUserId(userId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated")
    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<?> getDonationsByCampaignId(@PathVariable String campaignId) {
        try {
            List<Donation> donations = donationService.getDonationsByCampaignId(campaignId);
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('DONOR')")
    @GetMapping("/self")
    public ResponseEntity<?> getSelfDonations() {
        try {
            List<Donation> donations = donationService.getSelfDonations();
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<Long> getDonationsCount() {
        long count = donationService.getDonationsCount();
        return ResponseEntity.ok(count);
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
