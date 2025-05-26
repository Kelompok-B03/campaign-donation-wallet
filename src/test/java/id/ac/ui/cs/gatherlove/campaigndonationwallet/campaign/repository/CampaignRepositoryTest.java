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

    private Campaign campaign;

    @BeforeEach
    void setUp() {
        campaign = new Campaign();
        campaign.setCampaignId("abc123");
        campaign.setTitle("Test Campaign");
        campaign.setFundraiserId("user123");
        campaign.setStatus("MENUNGGU_VERIFIKASI");
        campaign.setStartDate(LocalDate.now());
        campaign.setEndDate(LocalDate.now().plusDays(10));
    }

    @Test
    void testFindById() {
        when(campaignRepository.findById("abc123")).thenReturn(Optional.of(campaign));

        Optional<Campaign> result = campaignRepository.findById("abc123");

        assertTrue(result.isPresent());
        assertEquals("Test Campaign", result.get().getTitle());
        verify(campaignRepository, times(1)).findById("abc123");
    }

    @Test
    void testFindByFundraiserId() {
        when(campaignRepository.findByFundraiserId("user123")).thenReturn(List.of(campaign));

        List<Campaign> result = campaignRepository.findByFundraiserId("user123");

        assertEquals(1, result.size());
        assertEquals("user123", result.get(0).getFundraiserId());
        verify(campaignRepository, times(1)).findByFundraiserId("user123");
    }

    @Test
    void testFindByStatus() {
        when(campaignRepository.findByStatus("MENUNGGU_VERIFIKASI")).thenReturn(List.of(campaign));

        List<Campaign> result = campaignRepository.findByStatus("MENUNGGU_VERIFIKASI");

        assertFalse(result.isEmpty());
        assertEquals("MENUNGGU_VERIFIKASI", result.get(0).getStatus());
        verify(campaignRepository, times(1)).findByStatus("MENUNGGU_VERIFIKASI");
    }

    @Test
    void testFindByIdNotFound() {
        when(campaignRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Campaign> result = campaignRepository.findById("nonexistent");

        assertTrue(result.isEmpty());
        verify(campaignRepository, times(1)).findById("nonexistent");
    }
}
