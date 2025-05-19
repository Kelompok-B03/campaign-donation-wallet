package id.ac.ui.cs.gatherlove.campaigndonationwallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TopUpRequestDTO;
import org.springframework.stereotype.Service;

@Service("DANA")
public class DanaTopUpStrategy implements TopUpStrategy {

    @Override
    public void topUp(TopUpRequestDTO request) {
        if (request.getPaymentPhone() == null || !request.getPaymentPhone().matches("^08\\d{8,11}$")) {
            throw new IllegalArgumentException("Invalid DANA phone number.");
        }

        System.out.println("[SIMULATION] DANA top-up for: " + request.getPaymentPhone()
                + ", amount: " + request.getAmount());
    }
}
