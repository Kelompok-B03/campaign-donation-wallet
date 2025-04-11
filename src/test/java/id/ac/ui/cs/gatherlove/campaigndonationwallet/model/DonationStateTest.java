package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DonationStateTest {

    @Test
    void testPendingDonationCanBeCancelled() {
        Donation donation = new Donation();
        donation.setState(new PendingState());

        assertDoesNotThrow(donation::cancel);
    }

    @Test
    void testFinishedDonationCannotBeCancelled() {
        Donation donation = new Donation();
        donation.setState(new FinishedState());

        assertThrows(IllegalStateException.class, donation::cancel);
    }

    @Test
    void testPendingDonationStatusUpdate() {
        Donation donation = new Donation();
        donation.setState(new PendingState());

        donation.updateStatus();

        assertTrue(donation.getState() instanceof FinishedState); // Assuming state transition
    }
}
