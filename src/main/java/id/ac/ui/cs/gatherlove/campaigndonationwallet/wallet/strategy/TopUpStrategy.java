package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;

public interface TopUpStrategy {
    void topUp(TopUpRequestDTO request);
}

