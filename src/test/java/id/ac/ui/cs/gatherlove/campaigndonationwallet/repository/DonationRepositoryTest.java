package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DonationRepositoryTest {

    @Autowired
    private DonationRepository donationRepository;

    @Test
    void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();

        Donation donation = new Donation(userId, campaignId, 100.0f, "For good cause");
        donation.setCreatedAt(new Date());

        donationRepository.save(donation);

        List<Donation> results = donationRepository.findByUserId(userId);

        assertFalse(results.isEmpty());
        assertEquals(userId, results.get(0).getUserId());
    }

    @Test
    void testFindByCampaignId() {
        UUID userId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();

        Donation donation = new Donation(userId, campaignId, 50.0f, "Hope it helps!");
        donation.setCreatedAt(new Date());

        donationRepository.save(donation);

        List<Donation> results = donationRepository.findByCampaignId(campaignId);

        assertEquals(1, results.size());
        assertEquals(campaignId, results.get(0).getCampaignId());
    }

    @Test
    void testFindByStateName() {
        UUID userId = UUID.randomUUID();
        UUID campaignId = UUID.randomUUID();

        Donation donation = new Donation(userId, campaignId, 30.0f, null);
        donation.setCreatedAt(new Date());
        donation.setStateName("Pending");

        donationRepository.save(donation);

        List<Donation> pendingDonations = donationRepository.findByStateName("Pending");

        assertFalse(pendingDonations.isEmpty());
        assertEquals("Pending", pendingDonations.get(0).getStateName());
    }
}
