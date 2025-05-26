package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;

import java.util.List;
import java.util.UUID;

public interface CampaignService {

    List<Campaign> findAll();

    Campaign create(Campaign campaign);

    List<Campaign> findByUserId(String userId);

    Campaign findById(String id);

    void delete(Campaign campaign);

    void update(Campaign campaign);

    List<Campaign> findCampaignsByStatus(String status);

    void updateUsageProofLink(String campaignId, String usageProofLink);

    void upgradeCampaignStatus(String campaignId);

    void withdrawCampaign(String campaignId);

    void completeCampaign(String campaignId, String userId);
}
