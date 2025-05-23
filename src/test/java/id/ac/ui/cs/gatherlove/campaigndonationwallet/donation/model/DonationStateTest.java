package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.FinishedState;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.PendingState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DonationStateTest {

//    @Test
//    void testPendingDonationCanBeCancelled() {
//        Donation donation = new Donation();
//        donation.setState(new PendingState());
//
//        assertDoesNotThrow(donation::cancel);
//    }

//    @Test
//    void testFinishedDonationCannotBeCancelled() {
//        Donation donation = new Donation();
//        donation.setState(new FinishedState());
//
//        assertThrows(IllegalStateException.class, donation::cancel);
//    }

    @Test
    void testPendingDonationStatusUpdate() {
        Donation donation = new Donation();
        donation.setState(new PendingState());

        donation.getState().updateStatus();

        assertTrue(donation.getState() instanceof FinishedState); // Assuming state transition
    }
}