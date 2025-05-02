package id.ac.ui.cs.gatherlove.campaigndonationwallet.command;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.service.CampaignService;

public class DonateCampaignCommand implements Command {
    private final Campaign campaign;
    private final int amount;

    public DonateCampaignCommand(Campaign campaign, int amount) {
        this.campaign = campaign;
        this.amount = amount;
    }

    @Override
    public void execute() {
        campaign.setFundsCollected(campaign.getFundsCollected() + amount);
    }
}