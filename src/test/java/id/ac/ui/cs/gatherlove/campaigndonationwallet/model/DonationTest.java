package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DonationTest {

    private UUID userId;
    private UUID campaignId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        campaignId = UUID.randomUUID();
    }

    @Test
    void testDonationInitialization() {
        Donation donation = new Donation(userId, campaignId, 500f, "Wish you the best");

        assertNotNull(donation.getDonationId());
        assertEquals(userId, donation.getUserId());
        assertEquals(campaignId, donation.getCampaignId());
        assertEquals(500f, donation.getAmount());
        assertEquals("Wish you the best", donation.getMessage());
        assertNotNull(donation.getCreatedAt());
        assertTrue(donation.getState() instanceof PendingState);
    }

    @Test
    void testNegativeDonationAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Donation(userId, campaignId, -100f, "Oops");
        });
    }

    @Test
    void testNullUserOrCampaignThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new Donation(null, campaignId, 100f, null);
        });

        assertThrows(NullPointerException.class, () -> {
            new Donation(userId, null, 100f, null);
        });
    }

    @Test
    void testUpdateStateFromPendingToFinished() {
        Donation donation = new Donation(userId, campaignId, 250f, null);

        donation.setState(new FinishedState());

        assertTrue(donation.getState() instanceof FinishedState);
    }

    @Test
    void testCancelFromPending() {
        Donation donation = new Donation(userId, campaignId, 100f, "Cancelable");
        assertDoesNotThrow(donation::cancel);
        assertTrue(donation.getState() instanceof CancelledState);
    }

    @Test
    void testCancelFromFinishedThrows() {
        Donation donation = new Donation(userId, campaignId, 100f, "Final");
        donation.setState(new FinishedState());

        assertThrows(IllegalStateException.class, donation::cancel);
    }

    @Test
    void testManualStateChange() {
        Donation donation = new Donation(userId, campaignId, 100f, null);
        donation.setState(new FinishedState());

        assertEquals("Finished", donation.getState().getName());
    }
}
