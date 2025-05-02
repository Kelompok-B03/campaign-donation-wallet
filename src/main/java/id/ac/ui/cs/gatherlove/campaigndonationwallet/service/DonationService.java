package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;

import java.util.UUID;

public interface DonationService {
    Donation createDonation(UUID userId, UUID campaignId, Float amount, String message);
    Donation updateStatus(UUID donationId);
    Donation cancelDonation(UUID donationId);
    void deleteDonation(UUID donationId);
}
