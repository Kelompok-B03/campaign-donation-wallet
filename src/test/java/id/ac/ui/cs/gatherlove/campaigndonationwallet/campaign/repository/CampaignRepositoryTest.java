package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.repository;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CampaignRepositoryTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Test
    public void testSaveAndFindById() {
        Campaign campaign = new Campaign();
        campaign.setTitle("Test Campaign");
        campaign.setFundraiserId("user1");
        campaign.setStartDate(LocalDate.now());
        campaign.setEndDate(LocalDate.now().plusDays(30));

        Campaign saved = campaignRepository.save(campaign);

        assertNotNull(saved.getCampaignId());
        assertEquals("Test Campaign", saved.getTitle());
    }

    @Test
    public void testFindByFundraiserId() {
        Campaign campaign1 = new Campaign();
        campaign1.setTitle("Campaign A");
        campaign1.setFundraiserId("user1");

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("Campaign B");
        campaign2.setFundraiserId("user2");

        campaignRepository.save(campaign1);
        campaignRepository.save(campaign2);

        List<Campaign> user1Campaigns = campaignRepository.findByFundraiserId("user1");
        assertEquals(1, user1Campaigns.size());
        assertEquals("Campaign A", user1Campaigns.get(0).getTitle());
    }

    @Test
    public void testNonExistId() {
        assertTrue(campaignRepository.findById("non-existing-id").isEmpty());
    }
}
