package id.ac.ui.cs.gatherlove.campaigndonationwallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TopUpRequestDTO;

public interface TopUpStrategy {
    void topUp(TopUpRequestDTO request);
}

