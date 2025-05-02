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
        // Arrange
        Float amount = 100.0f;
        String message = "Test donation";

        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Donation result = donationService.createDonation(userId, campaignId, amount, message);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(campaignId, result.getCampaignId());
        assertEquals(amount, result.getAmount());
        assertEquals(message, result.getMessage());
        assertEquals("Pending", result.getStateName());

        // Verify repository was called
        verify(donationRepository).save(any(Donation.class));
    }

    @Test
    void testUpdateStatus() {
        // Arrange
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Donation result = donationService.updateStatus(donationId);

        // Assert
        assertEquals("Finished", result.getStateName());

        // Verify repository was called
        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).save(testDonation);
    }

    @Test
    void testCancelDonation() {
        // Arrange
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Donation result = donationService.cancelDonation(donationId);

        // Assert
        assertEquals("Cancelled", result.getStateName());

        // Verify repository was called
        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).save(testDonation);
    }

    @Test
    void testCancelFinishedDonation() {
        // Arrange - Create finished donation
        testDonation.getState().updateStatus(); // Change to Finished state

        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            donationService.cancelDonation(donationId);
        });

        assertTrue(exception.getMessage().contains("Cannot cancel"));

        // Verify repository was called for find but not for save
        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository, never()).save(any(Donation.class));
    }

    @Test
    void testDeleteDonation() {
        // Arrange
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        // Act
        donationService.deleteDonation(donationId);

        // Verify repository was called
        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).delete(testDonation);
    }

    @Test
    void testCreateDonationWithInvalidAmount() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            donationService.createDonation(userId, campaignId, -50.0f, "Invalid donation");
        });

        assertTrue(exception.getMessage().contains("Amount must be positive"));

        // Verify repository was never called
        verify(donationRepository, never()).save(any(Donation.class));
    }
}
