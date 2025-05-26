package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.command.*;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service.CampaignService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.http.ResponseEntity;


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
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign, Authentication authentication) {
        String userId = getUserIdFromToken(authentication);
        campaign.setFundraiserId(userId);
        campaign.setStatus("SEDANG_BERLANGSUNG");
        campaign.setWithdrawed(false);

        CreateCampaignCommand createCommand = new CreateCampaignCommand(campaignService, campaign);
        CampaignCommandInvoker invoker = new CampaignCommandInvoker();
        invoker.setCommand(createCommand);
        invoker.run();

        return ResponseEntity.ok(campaign);
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
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    public List<Campaign> getCampaignsByStatus(@PathVariable String status) {
        return campaignService.findCampaignsByStatus(status);
    }

    @PostMapping("/{campaignId}/usage-proof")
    @PreAuthorize("isAuthenticated()")
    public void uploadUsageProofLink(@PathVariable String campaignId, @RequestBody String usageProofLink, Authentication authentication) {
        Campaign existing = campaignService.findById(campaignId);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
        }
        validateUserAccess(existing.getFundraiserId(), authentication);
        campaignService.updateUsageProofLink(campaignId, usageProofLink);
    }

    @PostMapping("/{campaignId}/upgrade-status")
    @PreAuthorize("hasRole('ADMIN')")
    public void upgradeCampaignStatus(@PathVariable String campaignId) {
        try {
            campaignService.upgradeCampaignStatus(campaignId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/withdraw")
    public void withdrawCampaign(@PathVariable String id, Authentication authentication) {
        try {
            Campaign existing = campaignService.findById(id);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found");
            }
            validateUserAccess(existing.getFundraiserId(), authentication);
            campaignService.withdrawCampaign(id);
        } catch (IllegalStateException | EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    public void completeCampaign(@PathVariable String id, Authentication authentication) {
        Campaign campaign = campaignService.findById(id);
        if (campaign == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign tidak ditemukan");
        }

        // Validasi apakah user yang login adalah pemilik campaign
        validateUserAccess(campaign.getFundraiserId(), authentication);

        // Jalankan proses penyelesaian campaign
        campaignService.completeCampaign(id, getUserIdFromToken(authentication));
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


