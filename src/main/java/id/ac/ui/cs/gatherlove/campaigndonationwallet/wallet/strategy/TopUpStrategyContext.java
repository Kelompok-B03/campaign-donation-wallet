package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TopUpStrategyContext {

    private final Map<String, TopUpStrategy> strategyMap;

    public void executeTopUp(TopUpRequestDTO request) {
        if (request.getPaymentMethod() == null) {
            throw new TransactionNotAllowedException("Payment method cannot be null.");
        }

        String methodKey = request.getPaymentMethod().name();
        TopUpStrategy strategy = strategyMap.get(methodKey);

        if (strategy == null) {
            throw new TransactionNotAllowedException("Unsupported top-up method: " + methodKey);
        }

        strategy.topUp(request);
    }
}
