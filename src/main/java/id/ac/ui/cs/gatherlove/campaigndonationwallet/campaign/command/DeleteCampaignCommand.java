package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.command;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service.CampaignService;

public class DeleteCampaignCommand implements Command {

    private final CampaignService campaignService;
    private final Campaign campaign;

    public DeleteCampaignCommand(CampaignService campaignService, Campaign campaign) {
        this.campaignService = campaignService;
        this.campaign = campaign;
    }

    @Override
    public void execute() {
        campaignService.delete(campaign);
    }
}
