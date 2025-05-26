package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.repository.CampaignRepository;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service.DonationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Mock
    private DonationService donationService;

    @Mock
    private WalletService walletService;

    private Campaign campaign;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        campaign = new Campaign();
        campaign.setCampaignId("test-id");
        campaign.setTitle("Save Oceans");
        campaign.setFundraiserId("user123");
    }

    @Test
    public void testCreateCampaign_Happy() {
        when(campaignRepository.save(campaign)).thenReturn(campaign);

        Campaign result = campaignService.create(campaign);

        assertEquals("Save Oceans", result.getTitle());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testFindAll_Happy() {
        when(campaignRepository.findAll()).thenReturn(List.of(campaign));

        List<Campaign> campaigns = campaignService.findAll();
        assertEquals(1, campaigns.size());
        assertEquals("Save Oceans", campaigns.get(0).getTitle());
    }

    @Test
    public void testFindById_Happy() {
        Campaign campaign = new Campaign();
        campaign.setCampaignId("test-id");
        campaign.setTitle("Save Oceans");
        campaign.setEndDate(LocalDate.now().minusDays(1)); // Sudah lewat
        campaign.setStatus("SEDANG_BERLANGSUNG"); // Belum selesai

        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Campaign result = campaignService.findById("test-id");

        assertNotNull(result);
        assertEquals("Save Oceans", result.getTitle());
        assertEquals("SELESAI", result.getStatus()); // ✅ status seharusnya diubah

        verify(campaignRepository).save(any(Campaign.class));
        verify(donationService).updateStatusByCampaign("test-id");
    }


    @Test
    public void testFindById_Unhappy() {
        when(campaignRepository.findById("unknown")).thenReturn(Optional.empty());

        Campaign result = campaignService.findById("unknown");
        assertNull(result);
    }

    @Test
    public void testFindByUserId() {
        when(campaignRepository.findByFundraiserId("user123")).thenReturn(List.of(campaign));

        List<Campaign> result = campaignService.findByUserId("user123");

        assertEquals(1, result.size());
        assertEquals("Save Oceans", result.get(0).getTitle());
    }

    @Test
    public void testDeleteCampaign() {
        campaignService.delete(campaign);
        verify(campaignRepository, times(1)).delete(campaign);
    }

    @Test
    public void testUpdateCampaign() {
        campaignService.update(campaign);
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    public void testFindCampaignsByStatus() {
        when(campaignRepository.findByStatus("SEDANG_BERLANGSUNG")).thenReturn(List.of(campaign));

        List<Campaign> result = campaignService.findCampaignsByStatus("SEDANG_BERLANGSUNG");

        assertEquals(1, result.size());
        verify(campaignRepository).findByStatus("SEDANG_BERLANGSUNG");
    }

    @Test
    public void testUpdateUsageProofLink_Happy() {
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        campaignService.updateUsageProofLink("test-id", "https://proof.com");

        verify(campaignRepository).save(campaign);
        assertEquals("https://proof.com", campaign.getUsageProofLink());
    }

    @Test
    public void testUpgradeCampaignStatus_AlreadyActive() {
        campaign.setStatus("SEDANG_BERLANGSUNG");
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        assertThrows(IllegalStateException.class, () -> campaignService.upgradeCampaignStatus("test-id"));
    }

    @Test
    public void testWithdrawCampaign_Happy() {
        campaign.setStatus("SELESAI");
        campaign.setWithdrawed(false);
        campaign.setFundsCollected(1000);
        campaign.setFundraiserId(UUID.randomUUID().toString());

        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        campaignService.withdrawCampaign("test-id");

        verify(walletService).withdrawCampaignFunds(any(UUID.class), eq("test-id"), eq(BigDecimal.valueOf(1000)));
        verify(campaignRepository).save(campaign);
        assertTrue(campaign.getWithdrawed());
    }

    @Test
    public void testWithdrawCampaign_NotCompleted() {
        campaign.setStatus("SEDANG_BERLANGSUNG");
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        assertThrows(IllegalStateException.class, () -> campaignService.withdrawCampaign("test-id"));
    }

    @Test
    public void testWithdrawCampaign_AlreadyWithdrawed() {
        campaign.setStatus("SELESAI");
        campaign.setWithdrawed(true);
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        assertThrows(IllegalStateException.class, () -> campaignService.withdrawCampaign("test-id"));
    }

    @Test
    public void testCompleteCampaign_Happy() {
        campaign.setStatus("SEDANG_BERLANGSUNG");
        campaign.setFundraiserId("user123");
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        campaignService.completeCampaign("test-id", "user123");

        verify(campaignRepository).save(campaign);
        verify(donationService).updateStatusByCampaign("test-id");
        assertEquals("SELESAI", campaign.getStatus());
    }

    @Test
    public void testCompleteCampaign_WrongUser() {
        campaign.setFundraiserId("otherUser");
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        assertThrows(AccessDeniedException.class, () -> campaignService.completeCampaign("test-id", "user123"));
    }

    @Test
    public void testCompleteCampaign_AlreadyCompleted() {
        campaign.setFundraiserId("user123");
        campaign.setStatus("SELESAI");
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        assertThrows(IllegalStateException.class, () -> campaignService.completeCampaign("test-id", "user123"));
    }

}