package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;

import java.util.UUID;
import java.util.List;

public interface DonationService {
    Donation createDonation(UUID userId, UUID campaignId, Float amount, String message);
    Donation updateStatus(UUID donationId);
    Donation cancelDonation(UUID donationId);
    void deleteDonation(UUID donationId);
    Donation getDonationById(UUID donationId);
    List<Donation> getDonationsByUserId(UUID userId);
    List<Donation> getDonationsByCampaignId(UUID campaignId);
}
