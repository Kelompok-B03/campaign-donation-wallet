package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.command.*;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


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
    @PreAuthorize("isAuthenticated()")
    public void createCampaign(@RequestBody Campaign campaign, Authentication authentication) {
        String userId = getUserIdFromToken(authentication);
        campaign.setFundraiserId(userId);
        campaign.setStatus("MENGUNGGU_VERIFIKASI");
        campaign.setWithdrawed(false);
        CreateCampaignCommand createCommand = new CreateCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(createCommand);
        invoker.run();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<Campaign> getCampaignsByUserId(@PathVariable String userId, Authentication authentication) {
        validateUserAccess(userId, authentication);
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
    @PreAuthorize("hasRole('FUNDRAISER')")
    public void deleteCampaign(@PathVariable String id, Authentication authentication) {
        Campaign campaign = campaignService.findById(id);
        if (campaign == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }
        validateUserAccess(campaign.getFundraiserId(), authentication);
        DeleteCampaignCommand deleteCommand = new DeleteCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(deleteCommand);
        invoker.run();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FUNDRAISER')")
    public void updateCampaign(@PathVariable String id, @RequestBody Campaign campaign, Authentication authentication) {
        Campaign existing = campaignService.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }
        validateUserAccess(existing.getFundraiserId(), authentication);

        campaign.setCampaignId(id); // pastikan pakai ID dari path

        UpdateCampaignCommand updateCommand = new UpdateCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(updateCommand);
        invoker.run();
    }

    @GetMapping("/status/{status}")
    public List<Campaign> getCampaignsByStatus(@PathVariable String status) {
        return campaignService.findCampaignsByStatus(status);
    }

    private void validateUserAccess(String ownerId, Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            String userIdFromToken = jwtToken.getToken().getClaimAsString("userId");
            if (ownerId.equals(userIdFromToken)) return;

            boolean isAdmin = jwtToken.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) return;

            throw new SecurityException("Access denied");
        }
        throw new SecurityException("Invalid authentication");
    }

    private String getUserIdFromToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return jwtToken.getToken().getClaimAsString("userId");
        }
        throw new SecurityException("Invalid authentication");
    }
}


