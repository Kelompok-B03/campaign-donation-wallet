package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.controller.CampaignControllerTest;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service.CampaignService;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.controller.DonationController;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.dto.DonationRequest;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.CancelledState;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.FinishedState;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(DonationController.class)
@AutoConfigureMockMvc(addFilters = false)
class DonationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DonationService donationService;

    private UUID userId;
    private String campaignId;
    private UUID donationId;
    private Donation testDonation;
    private DonationRequest donationRequest;
    private List<Donation> userDonations;
    private List<Donation> campaignDonations;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        campaignId = UUID.randomUUID().toString();
        donationId = UUID.randomUUID();

        // Set up test donation
        testDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
        testDonation.setDonationId(donationId);

        // Create test donations for lists
        Donation donation1 = new Donation(userId, UUID.randomUUID().toString(), 50.0f, "First donation");
        Donation donation2 = new Donation(userId, UUID.randomUUID().toString(), 75.0f, "Second donation");
        userDonations = Arrays.asList(donation1, donation2, testDonation);

        Donation donation3 = new Donation(UUID.randomUUID(), campaignId, 120.0f, "Third donation");
        Donation donation4 = new Donation(UUID.randomUUID(), campaignId, 85.0f, "Fourth donation");
        campaignDonations = Arrays.asList(donation3, donation4, testDonation);

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
                any(String.class),
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
        updatedDonation.setState(new FinishedState());

        when(donationService.updateStatus(donationId)).thenReturn(updatedDonation);

        mockMvc.perform(put("/api/donations/{donationId}/status", donationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donationId").value(donationId.toString()))
                .andExpect(jsonPath("$.stateName").value("Finished"));

        verify(donationService).updateStatus(donationId);
    }

//    @Test
//    void testCancelDonation() throws Exception {
//        // Setup cancelled donation
//        Donation cancelledDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
//        cancelledDonation.setDonationId(donationId);
//        cancelledDonation.setState(new CancelledState());
//        cancelledDonation.setStateName("Cancelled");
//
//        when(donationService.cancelDonation(donationId)).thenReturn(cancelledDonation);
//
//        mockMvc.perform(put("/api/donations/{donationId}/cancel", donationId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.donationId").value(donationId.toString()))
//                .andExpect(jsonPath("$.stateName").value("Cancelled"));
//
//        verify(donationService).cancelDonation(donationId);
//    }

//    @Test
//    void testCancelFinishedDonation() throws Exception {
//        when(donationService.cancelDonation(donationId))
//                .thenThrow(new IllegalStateException("Cannot cancel donation with ID " + donationId + ": Donation in Finished state cannot be cancelled"));
//
//        mockMvc.perform(put("/api/donations/{donationId}/cancel", donationId))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.message", containsString("Cannot cancel donation")));
//
//        verify(donationService).cancelDonation(donationId);
//    }

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

    @Test
    void testGetDonationById() throws Exception {
        when(donationService.getDonationById(donationId)).thenReturn(testDonation);

        mockMvc.perform(get("/api/donations/{donationId}", donationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donationId").value(donationId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.campaignId").value(campaignId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.message").value("Test donation"));

        verify(donationService).getDonationById(donationId);
    }

    @Test
    void testGetDonationByIdNotFound() throws Exception {
        when(donationService.getDonationById(donationId))
                .thenThrow(new IllegalArgumentException("Donation with ID " + donationId + " not found"));

        mockMvc.perform(get("/api/donations/{donationId}", donationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Donation with ID " + donationId + " not found"));

        verify(donationService).getDonationById(donationId);
    }

    @Test
    void testGetDonationsByUserId() throws Exception {
        when(donationService.getDonationsByUserId(userId)).thenReturn(userDonations);

        mockMvc.perform(get("/api/donations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].donationId").value(donationId.toString()))
                .andExpect(jsonPath("$[2].userId").value(userId.toString()))
                .andExpect(jsonPath("$[2].campaignId").value(campaignId.toString()));

        verify(donationService).getDonationsByUserId(userId);
    }

    @Test
    void testGetDonationsByCampaignId() throws Exception {
        when(donationService.getDonationsByCampaignId(campaignId)).thenReturn(campaignDonations);

        mockMvc.perform(get("/api/donations/campaign/{campaignId}", campaignId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].donationId").value(donationId.toString()))
                .andExpect(jsonPath("$[2].userId").value(userId.toString()))
                .andExpect(jsonPath("$[2].campaignId").value(campaignId.toString()));

        verify(donationService).getDonationsByCampaignId(campaignId);
    }
}