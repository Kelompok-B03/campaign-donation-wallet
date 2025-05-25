package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;

import java.util.List;

public interface CampaignService {

    List<Campaign> findAll();

    Campaign create(Campaign campaign);

    List<Campaign> findByUserId(String userId);

    Campaign findById(String id);

    void delete(Campaign campaign);

    void update(Campaign campaign);

    List<Campaign> findCampaignsByStatus(String status);

    void updateUsageProofLink(String campaignId, String usageProofLink);
}
