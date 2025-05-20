package id.ac.ui.cs.gatherlove.campaigndonationwallet.command;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.service.CampaignService;

public class UpdateCampaignCommand implements Command {

    private final CampaignService campaignService;
    private final Campaign campaign;

    public UpdateCampaignCommand(CampaignService campaignService, Campaign campaign) {
        this.campaignService = campaignService;
        this.campaign = campaign;
    }

    @Override
    public void execute() {
        campaignService.update(campaign);
    }
}
