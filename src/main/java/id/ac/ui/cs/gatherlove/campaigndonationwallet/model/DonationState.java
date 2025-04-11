package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

public interface DonationState {
    void updateStatus();
    void cancel();
    String getName();
    void setContext(Donation donation);
}

