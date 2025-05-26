package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignRepositoryTest {

    @Mock
    private CampaignRepository campaignRepository;

    private Campaign campaign1;
    private Campaign campaign2;

    @BeforeEach
    void setUp() {
        campaign1 = new Campaign();
        campaign1.setCampaignId("abc123");
        campaign1.setTitle("Campaign One");
        campaign1.setFundraiserId("user123");
        campaign1.setStatus("MENUNGGU_VERIFIKASI");
        campaign1.setStartDate(LocalDate.now());
        campaign1.setEndDate(LocalDate.now().plusDays(5));

        campaign2 = new Campaign();
        campaign2.setCampaignId("def456");
        campaign2.setTitle("Campaign Two");
        campaign2.setFundraiserId("user123");
        campaign2.setStatus("SELESAI");
        campaign2.setStartDate(LocalDate.now().minusDays(10));
        campaign2.setEndDate(LocalDate.now().minusDays(1));
    }

    @Test
    void testFindById_Found() {
        when(campaignRepository.findById("abc123")).thenReturn(Optional.of(campaign1));

        Optional<Campaign> result = campaignRepository.findById("abc123");

        assertTrue(result.isPresent());
        assertEquals("Campaign One", result.get().getTitle());
        verify(campaignRepository).findById("abc123");
    }

    @Test
    void testFindById_NotFound() {
        when(campaignRepository.findById("notfound")).thenReturn(Optional.empty());

        Optional<Campaign> result = campaignRepository.findById("notfound");

        assertFalse(result.isPresent());
        verify(campaignRepository).findById("notfound");
    }

    @Test
    void testFindByFundraiserId_MultipleCampaigns() {
        when(campaignRepository.findByFundraiserId("user123")).thenReturn(List.of(campaign1, campaign2));

        List<Campaign> result = campaignRepository.findByFundraiserId("user123");

        assertEquals(2, result.size());
        verify(campaignRepository).findByFundraiserId("user123");
    }

    @Test
    void testFindByStatus_SingleCampaign() {
        when(campaignRepository.findByStatus("MENUNGGU_VERIFIKASI")).thenReturn(List.of(campaign1));

        List<Campaign> result = campaignRepository.findByStatus("MENUNGGU_VERIFIKASI");

        assertEquals(1, result.size());
        assertEquals("MENUNGGU_VERIFIKASI", result.get(0).getStatus());
        verify(campaignRepository).findByStatus("MENUNGGU_VERIFIKASI");
    }

    @Test
    void testFindByStatus_NoCampaigns() {
        when(campaignRepository.findByStatus("INVALID")).thenReturn(List.of());

        List<Campaign> result = campaignRepository.findByStatus("INVALID");

        assertTrue(result.isEmpty());
        verify(campaignRepository).findByStatus("INVALID");
    }
}
