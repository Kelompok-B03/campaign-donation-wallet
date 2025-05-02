package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.*;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;

    @Autowired
    public DonationServiceImpl(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Override
    @Transactional
    public Donation createDonation(UUID userId, UUID campaignId, Float amount, String message) {
        Donation donation = new Donation(userId, campaignId, amount, message);

        return donationRepository.save(donation);
    }

    @Override
    @Transactional
    public Donation updateStatus(UUID donationId) {
        Donation donation = findDonationById(donationId);

        donation.getState().updateStatus();

        return donationRepository.save(donation);
    }

    @Override
    @Transactional
    public Donation cancelDonation(UUID donationId) {
        Donation donation = findDonationById(donationId);

        try {
            // Cancel donation if possible
            donation.cancel();

            return donationRepository.save(donation);
        } catch (IllegalStateException e) {
            // Rethrow the exception with additional context
            throw new IllegalStateException("Cannot cancel donation with ID " + donationId + ": " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteDonation(UUID donationId) {
        Donation donation = findDonationById(donationId);
        donationRepository.delete(donation);
    }

    // Helper 'find-by-Id' method
    private Donation findDonationById(UUID donationId) {
        return donationRepository.findByDonationId(donationId);
    }
}
