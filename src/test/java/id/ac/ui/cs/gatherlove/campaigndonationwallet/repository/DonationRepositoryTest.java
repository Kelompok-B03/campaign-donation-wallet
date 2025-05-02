package id.ac.ui.cs.gatherlove.campaigndonationwallet.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.PendingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationRepositoryTest {

    @Mock
    private DonationRepository donationRepository;

    private UUID userId;
    private UUID campaignId;
    private Donation donation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        campaignId = UUID.randomUUID();

        donation = new Donation(userId, campaignId, 100.0f, "For good cause");
        donation.setCreatedAt(new Date());
        donation.setState(new PendingState());
    }

    @Test
    void testFindByDonationId() {
        List<Donation> expectedDonations = new ArrayList<>();
        expectedDonations.add(donation);

        when(donationRepository.findByDonationId(donation.getDonationId())).thenReturn(expectedDonations);

        List<Donation> results = donationRepository.findByDonationId(donation.getDonationId());

        assertFalse(results.isEmpty());
        assertInstanceOf(UUID.class, results.getFirst().getDonationId());
        verify(donationRepository, times(1)).findByDonationId(donation.getDonationId());
    }

    @Test
    void testFindByUserId() {
        List<Donation> expectedDonations = new ArrayList<>();
        expectedDonations.add(donation);

        when(donationRepository.findByUserId(userId)).thenReturn(expectedDonations);

        List<Donation> results = donationRepository.findByUserId(userId);

        assertFalse(results.isEmpty());
        assertEquals(userId, results.get(0).getUserId());
        verify(donationRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testFindByCampaignId() {
        List<Donation> expectedDonations = new ArrayList<>();
        expectedDonations.add(donation);

        when(donationRepository.findByCampaignId(campaignId)).thenReturn(expectedDonations);

        List<Donation> results = donationRepository.findByCampaignId(campaignId);

        assertEquals(1, results.size());
        assertEquals(campaignId, results.get(0).getCampaignId());
        verify(donationRepository, times(1)).findByCampaignId(campaignId);
    }

    @Test
    void testFindByStateName() {
        List<Donation> expectedDonations = new ArrayList<>();
        expectedDonations.add(donation);

        when(donationRepository.findByStateName("Pending")).thenReturn(expectedDonations);

        List<Donation> pendingDonations = donationRepository.findByStateName("Pending");

        assertFalse(pendingDonations.isEmpty());
        assertEquals("Pending", pendingDonations.get(0).getStateName());
        verify(donationRepository, times(1)).findByStateName("Pending");
    }
}