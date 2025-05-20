package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

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
        when(campaignRepository.findById("test-id")).thenReturn(Optional.of(campaign));

        Campaign result = campaignService.findById("test-id");
        assertNotNull(result);
        assertEquals("Save Oceans", result.getTitle());
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
}
