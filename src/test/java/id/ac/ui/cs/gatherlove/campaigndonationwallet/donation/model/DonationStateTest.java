package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.FinishedState;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.PendingState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DonationStateTest {

    @Test
    void testPendingDonationStatusUpdate() {
        Donation donation = new Donation();
        donation.setState(new PendingState());

        donation.getState().updateStatus();

        assertTrue(donation.getState() instanceof FinishedState); // Assuming state transition
    }
}