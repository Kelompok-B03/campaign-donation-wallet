
package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.PaymentMethod;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy.DanaTopUpStrategy;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy.GoPayTopUpStrategy;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy.TopUpStrategyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TopUpStrategyContextTest {

    private TopUpStrategyContext context;

    @BeforeEach
    void setUp() {
        context = new TopUpStrategyContext(Map.of(
            "GOPAY", new GoPayTopUpStrategy(),
            "DANA", new DanaTopUpStrategy()
        ));
    }

    @Test
    void testExecuteTopUpWithGoPay() {
        TopUpRequestDTO request = new TopUpRequestDTO();
        request.setPaymentPhone("081111111111");
        request.setAmount(BigDecimal.valueOf(10000));
        request.setPaymentMethod(PaymentMethod.GOPAY);

        assertDoesNotThrow(() -> context.executeTopUp(request));
    }

    @Test
    void testExecuteTopUpWithDana() {
        TopUpRequestDTO request = new TopUpRequestDTO();
        request.setPaymentPhone("081111111111");
        request.setAmount(BigDecimal.valueOf(10000));
        request.setPaymentMethod(PaymentMethod.DANA);

        assertDoesNotThrow(() -> context.executeTopUp(request));
    }

    @Test
    void testExecuteTopUpWithUnsupportedMethod() {
        TopUpRequestDTO request = new TopUpRequestDTO();
        request.setPaymentPhone("081111111111");
        request.setAmount(BigDecimal.valueOf(10000));
        request.setPaymentMethod(null); // null simulates unsupported or missing method

        Exception exception = assertThrows(TransactionNotAllowedException.class, () -> context.executeTopUp(request));
        assertEquals("Payment method cannot be null.", exception.getMessage());
    }
}
