package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CampaignTest {

    @Test
    public void testGetterSetter() {
        Campaign campaign = new Campaign();

        campaign.setCampaignId("abc123");
        campaign.setTitle("Save the Earth");
        campaign.setDescription("Fundraising for reforestation");
        campaign.setTargetAmount(10000);
        campaign.setFundsCollected(2000);
        campaign.setStartDate(LocalDate.of(2025, 1, 1));
        campaign.setEndDate(LocalDate.of(2025, 12, 31));
        campaign.setFundraiserId("user123");
        campaign.setStatus("MENUNGGU_VERIFIKASI");

        assertEquals("abc123", campaign.getCampaignId());
        assertEquals("Save the Earth", campaign.getTitle());
        assertEquals("Fundraising for reforestation", campaign.getDescription());
        assertEquals(10000, campaign.getTargetAmount());
        assertEquals(2000, campaign.getFundsCollected());
        assertEquals(LocalDate.of(2025, 1, 1), campaign.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), campaign.getEndDate());
        assertEquals("user123", campaign.getFundraiserId());
        assertEquals("MENUNGGU_VERIFIKASI", campaign.getStatus());
    }
}
