package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DonationStateTest {

    @Test
    void testPendingDonationStatusUpdate() {
        Donation donation = new Donation();
        donation.setState(new PendingState());

        donation.getState().updateStatus();

        assertTrue(donation.getState() instanceof FinishedState);
    }

    @Test
    void testPendingStateContextAssignment() {
        Donation donation = new Donation();
        PendingState state = new PendingState();
        state.setContext(donation);

        state.updateStatus();
        assertTrue(donation.getState() instanceof FinishedState);
    }

    @Test
    void testFinishedStateContextAssignment() {
        Donation donation = new Donation();
        FinishedState state = new FinishedState();
        state.setContext(donation);

        assertDoesNotThrow(() -> state.setContext(donation));
    }

    @Test
    void testFullStateTransitionFlow() {
        Donation donation = new Donation();
        donation.setState(new PendingState());

        // 1st transition: should succeed
        donation.getState().updateStatus();
        assertTrue(donation.getState() instanceof FinishedState);

        // 2nd transition: should fail
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            donation.getState().updateStatus();
        });

        assertEquals("Donation status is already 'Finished' and cannot be updated further.", exception.getMessage());
    }
}