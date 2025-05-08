package id.ac.ui.cs.gatherlove.campaigndonationwallet.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.DonationRequest;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public ResponseEntity<?> createDonation(@RequestBody DonationRequest request) {
        try {
            Donation donation = donationService.createDonation(
                    request.getUserId(),
                    request.getCampaignId(),
                    request.getAmount(),
                    request.getMessage()
            );
            return new ResponseEntity<>(donation, HttpStatus.CREATED);
        } catch (IllegalArgumentException | NullPointerException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @PutMapping("/{donationId}/cancel")
    public ResponseEntity<?> cancelDonation(@PathVariable UUID donationId) {
        try {
            Donation donation = donationService.cancelDonation(donationId);
            return new ResponseEntity<>(donation, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
