package id.ac.ui.cs.gatherlove.campaigndonationwallet.service;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Campaign;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

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
    public Campaign findById(String id){
        Optional<Campaign> result = campaignRepository.findById(id);
        return result.orElse(null);
    }

    @Override
    public void delete(Campaign campaign) {
        campaignRepository.delete(campaign);
    }

    @Override
    public void update(Campaign campaign) {
        campaignRepository.save(campaign);
    }
}
