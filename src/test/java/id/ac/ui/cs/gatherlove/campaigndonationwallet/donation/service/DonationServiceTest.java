package id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.repository.CampaignRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.model.Donation;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.repository.DonationRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private Jwt jwt;

    @Mock
    private AbstractAuthenticationToken authentication;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private WalletService walletService; // mock this (not @InjectMocks)

    @InjectMocks
    private DonationServiceImpl donationService; // inject the above into this

    private UUID userId;
    private String campaignId;
    private UUID donationId;
    private Donation testDonation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        campaignId = UUID.randomUUID().toString();
        donationId = UUID.randomUUID();

        testDonation = new Donation(userId, campaignId, 100.0f, "Test donation");
        testDonation.setDonationId(donationId);
    }

    @Test
    void testCreateDonation() {
        Float amount = 100.0f;
        String message = "Test donation";

        // Mock JWT + SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("userId")).thenReturn(userId.toString());

        // Mock walletService
        when(walletService.recordDonation(eq(userId), eq(campaignId), any(BigDecimal.class), any(String.class)))
                .thenReturn(mock(TransactionDTO.class));

        when(donationRepository.save(any(Donation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Campaign campaign = new Campaign();
        campaign.setFundsCollected(1000);
        when(campaignRepository.findById(eq(campaignId))).thenReturn(Optional.of(campaign));

        Donation result = donationService.createDonation(campaignId, amount, message);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(campaignId, result.getCampaignId());
        assertEquals("Pending", result.getStateName());
        assertEquals(1100, campaign.getFundsCollected());

        verify(donationRepository).save(any(Donation.class));
        verify(campaignRepository).findById(campaignId);
    }

    @Test
    void testUpdateStatus() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);
        when(donationRepository.save(any(Donation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Donation result = donationService.updateStatus(donationId);

        assertEquals("Finished", result.getStateName());

        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).save(testDonation);
    }

    @Test
    void testDeleteDonation() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        donationService.deleteDonation(donationId);

        verify(donationRepository).findByDonationId(donationId);
        verify(donationRepository).delete(testDonation);
    }

    @Test
    void testGetDonationById() {
        when(donationRepository.findByDonationId(donationId)).thenReturn(testDonation);

        Donation result = donationService.getDonationById(donationId);

        assertNotNull(result);
        assertEquals(donationId, result.getDonationId());

        verify(donationRepository).findByDonationId(donationId);
    }

    @Test
    void testGetDonationByIdWithInvalidId() {
        when(donationRepository.findByDonationId(any(UUID.class))).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            donationService.getDonationById(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("not found"));

        verify(donationRepository).findByDonationId(any(UUID.class));
    }

    @Test
    void testGetDonationsByUserId() {
        List<Donation> userDonations = Arrays.asList(testDonation);
        when(donationRepository.findByUserId(userId)).thenReturn(userDonations);

        List<Donation> result = donationService.getDonationsByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());

        verify(donationRepository).findByUserId(userId);
    }

    @Test
    void testGetDonationsByCampaignId() {
        List<Donation> campaignDonations = Arrays.asList(testDonation);
        when(donationRepository.findByCampaignId(campaignId)).thenReturn(campaignDonations);

        List<Donation> result = donationService.getDonationsByCampaignId(campaignId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(campaignId, result.get(0).getCampaignId());

        verify(donationRepository).findByCampaignId(campaignId);
    }

    @Test
    void testGetSelfDonations() {
        // Mock JWT + SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("userId")).thenReturn(userId.toString());

        List<Donation> donations = List.of(testDonation);
        when(donationRepository.findByUserId(userId)).thenReturn(donations);

        List<Donation> result = donationService.getSelfDonations();

        assertEquals(1, result.size());
        assertEquals(testDonation, result.get(0));

        verify(donationRepository).findByUserId(userId);
    }

    @Test
    void testGetDonationsCount() {
        when(donationRepository.count()).thenReturn(5L);

        long count = donationService.getDonationsCount();

        assertEquals(5L, count);
        verify(donationRepository).count();
    }
}
