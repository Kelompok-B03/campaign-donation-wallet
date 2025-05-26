package id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.enums.CampaignStatus;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.campaign.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.donation.service.DonationService;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private DonationService donationService;
    @Autowired
    private WalletService walletService;

    @Override
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    @Override
    public Campaign create(Campaign campaign){
        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> findByUserId(String userId){
        return campaignRepository.findByFundraiserId(userId);
    }

    @Override
    public Campaign findById(String id) {
        Optional<Campaign> result = campaignRepository.findById(id);

        if (result.isPresent()) {
            Campaign campaign = result.get();
            if (campaign.getEndDate().isBefore(LocalDate.now())
                    && !campaign.getStatus().equals(CampaignStatus.SELESAI.name())) {
                campaign.setStatus(CampaignStatus.SELESAI.name());
                campaignRepository.save(campaign);
                donationService.updateStatusByCampaign(campaign.getCampaignId());
            }
            return campaign;
        } else {
            return null;
        }
    }

    @Override
    public void delete(Campaign campaign) {
        campaignRepository.delete(campaign);
    }

    @Override
    public void update(Campaign campaign) {
        campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> findCampaignsByStatus(String status) {
        return campaignRepository.findByStatus(status);
    }

    public void updateUsageProofLink(String campaignId, String usageProofLink) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign tidak ditemukan"));

        campaign.setUsageProofLink(usageProofLink);
        campaignRepository.save(campaign);
    }

    public void upgradeCampaignStatus(String campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign tidak ditemukan"));

        if(campaign.getStatus().equals("MENUNGGU_VERIFIKASI")) {
            campaign.setStatus("SEDANG_BERLANGSUNG");
        }else {
            throw new IllegalStateException("Status campaign sudah aktif");
        }
    }

    public void withdrawCampaign(String id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign tidak ditemukan"));

        if (!campaign.getStatus().equals(CampaignStatus.SELESAI.name())) {
            throw new IllegalStateException("Campaign belum selesai, tidak bisa melakukan withdraw.");
        }

        if (Boolean.TRUE.equals(campaign.getWithdrawed())) {
            throw new IllegalStateException("Campaign sudah pernah di-withdraw.");
        }

        campaign.setWithdrawed(true);
        campaignRepository.save(campaign);
        walletService.withdrawCampaignFunds(UUID.fromString(campaign.getFundraiserId()), campaign.getCampaignId(), BigDecimal.valueOf(campaign.getFundsCollected()));
    }

    public void completeCampaign(String id, String userId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign tidak ditemukan"));

        if (!campaign.getFundraiserId().equals(userId)) {
            throw new AccessDeniedException("Kamu tidak berhak menyelesaikan campaign ini.");
        }

        if (campaign.getStatus().equals(CampaignStatus.SELESAI.name())) {
            throw new IllegalStateException("Campaign sudah selesai.");
        }

        campaign.setStatus(CampaignStatus.SELESAI.name());
        campaignRepository.save(campaign);
        donationService.updateStatusByCampaign(campaign.getCampaignId());
    }



}
