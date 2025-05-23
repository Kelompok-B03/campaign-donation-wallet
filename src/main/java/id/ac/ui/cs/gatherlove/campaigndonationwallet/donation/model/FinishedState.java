package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

public class FinishedState implements DonationState {

    private Donation donation;

    @Override
    public void updateStatus() {
        throw new IllegalStateException("Donation status is already 'Finished' and cannot be updated further.");
    }

//    @Override
//    public void cancel() {
//        throw new IllegalStateException("Cannot cancel a finished donation");
//    }

    @Override
    public String getName() {
        return "Finished";
    }

    @Override
    public void setContext(Donation donation) {
        this.donation = donation;
    }
}
