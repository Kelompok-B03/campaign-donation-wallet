package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

public class FinishedState implements DonationState {

    private Donation donation;

    @Override
    public void updateStatus() {
        // No-op
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("Cannot cancel a finished donation");
    }

    @Override
    public String getName() {
        return "Finished";
    }

    @Override
    public void setContext(Donation donation) {
        this.donation = donation;
    }
}
