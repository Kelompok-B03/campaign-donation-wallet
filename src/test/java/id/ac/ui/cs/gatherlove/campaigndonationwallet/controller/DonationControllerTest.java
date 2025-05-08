package id.ac.ui.cs.gatherlove.campaigndonationwallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.DonationRequest;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.CancelledState;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.FinishedState;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(DonationController.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DonationService donationService;

    private UUID userId;
    private UUID campaignId;
    private UUID donationId;
    private Donation testDonation;
    private DonationRequest donationRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        campaignId = UUID.randomUUID();
        donationId = UUID.randomUUID();

        // Set up test donation
        testDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
        testDonation.setDonationId(donationId);

        // Set up donation request
        donationRequest = new DonationRequest();
        donationRequest.setUserId(userId);
        donationRequest.setCampaignId(campaignId);
        donationRequest.setAmount(100.0f);
        donationRequest.setMessage("Test donation");
    }

    @Test
    void testCreateDonation() throws Exception {
        when(donationService.createDonation(
                eq(userId),
                eq(campaignId),
                eq(100.0f),
                eq("Test donation")
        )).thenReturn(testDonation);

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.donationId").value(donationId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.campaignId").value(campaignId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.message").value("Test donation"))
                .andExpect(jsonPath("$.stateName").value("Pending"));

        verify(donationService).createDonation(userId, campaignId, 100.0f, "Test donation");
    }

    @Test
    void testCreateDonationWithInvalidAmount() throws Exception {
        // Set invalid amount
        donationRequest.setAmount(-50.0f);

        when(donationService.createDonation(
                any(UUID.class),
                any(UUID.class),
                eq(-50.0f),
                any(String.class)
        )).thenThrow(new IllegalArgumentException("Amount must be positive"));

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be positive"));
    }

    @Test
    void testUpdateDonationStatus() throws Exception {
        // Setup updated donation with Finished state
        Donation updatedDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
        updatedDonation.setDonationId(donationId);
        updatedDonation.setState(new FinishedState()); // State will be updated to Finished in service

        when(donationService.updateStatus(donationId)).thenReturn(updatedDonation);

        mockMvc.perform(put("/api/donations/{donationId}/status", donationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donationId").value(donationId.toString()))
                .andExpect(jsonPath("$.stateName").value("Finished"));

        verify(donationService).updateStatus(donationId);
    }

    @Test
    void testCancelDonation() throws Exception {
        // Setup cancelled donation
        Donation cancelledDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
        cancelledDonation.setDonationId(donationId);
        cancelledDonation.setState(new CancelledState()); // State will be updated to Cancelled in service
        cancelledDonation.setStateName("Cancelled");

        when(donationService.cancelDonation(donationId)).thenReturn(cancelledDonation);

        mockMvc.perform(put("/api/donations/{donationId}/cancel", donationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donationId").value(donationId.toString()))
                .andExpect(jsonPath("$.stateName").value("Cancelled"));

        verify(donationService).cancelDonation(donationId);
    }

    @Test
    void testCancelFinishedDonation() throws Exception {
        when(donationService.cancelDonation(donationId))
                .thenThrow(new IllegalStateException("Cannot cancel donation with ID " + donationId + ": Donation in Finished state cannot be cancelled"));

        mockMvc.perform(put("/api/donations/{donationId}/cancel", donationId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Cannot cancel donation")));

        verify(donationService).cancelDonation(donationId);
    }

    @Test
    void testDeleteDonation() throws Exception {
        doNothing().when(donationService).deleteDonation(donationId);

        mockMvc.perform(delete("/api/donations/{donationId}", donationId))
                .andExpect(status().isNoContent());

        verify(donationService).deleteDonation(donationId);
    }

    @Test
    void testDeleteNonExistentDonation() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Donation not found"))
                .when(donationService).deleteDonation(nonExistentId);

        mockMvc.perform(delete("/api/donations/{donationId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Donation not found"));
    }
}