package id.ac.ui.cs.gatherlove.campaigndonationwallet.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.command.*;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/campaign")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @GetMapping
    public List<Campaign> getAllCampaigns() {
        return campaignService.findAll();
    }

    @PostMapping
    public void createCampaign(@RequestBody Campaign campaign) {
        CreateCampaignCommand createCommand = new CreateCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(createCommand);
        invoker.run();
    }

    @GetMapping("/user/{userId}")
    public List<Campaign> getCampaignsByUserId(@PathVariable String userId) {
        return campaignService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public Campaign getCampaignById(@PathVariable String id) {
        Campaign campaign = campaignService.findById(id);
        if (campaign == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }
        return campaign;
    }

    @DeleteMapping("/{id}")
    public void deleteCampaign(@PathVariable String id) {
        Campaign campaign = campaignService.findById(id);
        if (campaign == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }

        DeleteCampaignCommand deleteCommand = new DeleteCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(deleteCommand);
        invoker.run();
    }

    @PutMapping("/{id}")
    public void updateCampaign(@PathVariable String id, @RequestBody Campaign campaign) {
        Campaign existing = campaignService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }

        campaign.setCampaignId(id); // pastikan pakai ID dari path

        UpdateCampaignCommand updateCommand = new UpdateCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(updateCommand);
        invoker.run();
    }
}
