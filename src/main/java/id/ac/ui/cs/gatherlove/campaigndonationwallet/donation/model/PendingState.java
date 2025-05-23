package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model;

public class PendingState implements DonationState {

    private Donation donation;

    @Override
    public void updateStatus() {
        donation.setState(new FinishedState());
    }

//    @Override
//    public void cancel() {
//        donation.setState(new CancelledState());
//    }

    @Override
    public String getName() {
        return "Pending";
    }

    @Override
    public void setContext(Donation donation) {
        this.donation = donation;
    }
}
