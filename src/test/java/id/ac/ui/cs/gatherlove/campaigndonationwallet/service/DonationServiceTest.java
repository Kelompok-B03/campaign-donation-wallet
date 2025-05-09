package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationServiceImpl donationService;

    private UUID userId;
    private UUID campaignId;
    private UUID donationId;
    private Donation testDonation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        campaignId = UUID.randomUUID();
        donationId = UUID.randomUUID();

        testDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
        testDonation.setDonationId(donationId);
    }

    @Test
    void testCreateDonation() {
        Float amount = 100.0f;
        String message = "Test donation";

        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation result = donationService.createDonation(userId, campaignId, amount, message);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(campaignId, result.getCampaignId());
        assertEquals(amount, result.getAmount());
        assertEquals(message, result.getMessage());
        assertEquals("Pending", result.getStateName());

        verify(donationRepository).save(any(Donation.class));
    }

    @Test
    void testUpdateStatus() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation result = donationService.updateStatus(donationId);

        assertEquals("Finished", result.getStateName());

        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).save(testDonation);
    }

    @Test
    void testCancelDonation() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation result = donationService.cancelDonation(donationId);

        assertEquals("Cancelled", result.getStateName());

        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).save(testDonation);
    }

    @Test
    void testCancelFinishedDonation() {
        testDonation.getState().updateStatus(); // Change to Finished state

        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            donationService.cancelDonation(donationId);
        });

        assertTrue(exception.getMessage().contains("Cannot cancel"));

        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void testDeleteDonation() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        donationService.deleteDonation(donationId);

        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).delete(testDonation);
    }

    @Test
    void testCreateDonationWithInvalidAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            donationService.createDonation(userId, campaignId, -50.0f, "Invalid donation");
        });

        assertTrue(exception.getMessage().contains("Amount must be positive"));

        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void testGetDonationById() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        Donation result = donationService.getDonationById(donationId);

        assertNotNull(result);
        assertEquals(donationId, result.getDonationId());

        verify(donationRepository).findByDonationId(donationId);
    }

    @Test
    void testGetDonationByIdWithInvalidId() {
        when(donationRepository.findByDonationId(any(UUID.class))).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            donationService.getDonationById(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("not found"));

        verify(donationRepository).findByDonationId(any(UUID.class));
    }

    @Test
    void testGetDonationsByUserId() {
        List<Donation> userDonations = Arrays.asList(testDonation);
        when(donationRepository.findByUserId(userId)).thenReturn(userDonations);

        List<Donation> result = donationService.getDonationsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());

        verify(donationRepository).findByUserId(userId);
    }

    @Test
    void testGetDonationsByCampaignId() {
        List<Donation> campaignDonations = Arrays.asList(testDonation);
        when(donationRepository.findByCampaignId(campaignId)).thenReturn(campaignDonations);

        List<Donation> result = donationService.getDonationsByCampaignId(campaignId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(campaignId, result.get(0).getCampaignId());

        verify(donationRepository).findByCampaignId(campaignId);
    }
}
