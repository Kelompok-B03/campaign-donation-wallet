package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import org.springframework.stereotype.Service;

@Service("GOPAY")
public class GoPayTopUpStrategy implements TopUpStrategy {

    @Override
    public void topUp(TopUpRequestDTO request) {
        if (request.getPaymentPhone() == null || !request.getPaymentPhone().matches("^08\\d{8,11}$")) {
            throw new IllegalArgumentException("Invalid GoPay phone number.");
        }

        System.out.println("[SIMULATION] GoPay top-up for: " + request.getPaymentPhone()
                + ", amount: " + request.getAmount());
    }
}
