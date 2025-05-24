package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;

import java.util.UUID;
import java.util.List;

public interface DonationService {
    Donation createDonation(String campaignId, Float amount, String message);
    Donation updateStatus(UUID donationId);
    void deleteDonation(UUID donationId);
    Donation getDonationById(UUID donationId);
    List<Donation> getDonationsByUserId(UUID userId);
    List<Donation> getDonationsByCampaignId(String campaignId);
}
