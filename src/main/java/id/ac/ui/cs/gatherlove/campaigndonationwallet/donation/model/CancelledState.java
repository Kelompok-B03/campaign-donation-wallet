package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

public class CancelledState implements DonationState {

    private Donation donation;

    @Override
    public void updateStatus() {
        // No-op
    }

//    @Override
//    public void cancel() {
//        // Already cancelled
//    }

    @Override
    public String getName() {
        return "Cancelled";
    }

    @Override
    public void setContext(Donation donation) {
        this.donation = donation;
    }
}
