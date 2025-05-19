
package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.strategy.GoPayTopUpStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GoPayTopUpStrategyTest {

    private final GoPayTopUpStrategy strategy = new GoPayTopUpStrategy();

    @Test
    void testTopUpWithNullPhone_ShouldThrow() {
        TopUpRequestDTO request = new TopUpRequestDTO();
        request.setPaymentPhone(null);
        request.setAmount(BigDecimal.valueOf(10000));

        assertThrows(IllegalArgumentException.class, () -> strategy.topUp(request));
    }

    @Test
    void testTopUpWithMalformedPhone_ShouldThrow() {
        TopUpRequestDTO request = new TopUpRequestDTO();
        request.setPaymentPhone("12345");
        request.setAmount(BigDecimal.valueOf(10000));

        assertThrows(IllegalArgumentException.class, () -> strategy.topUp(request));
    }

    @Test
    void testTopUpWithInvalidPhone() {
        TopUpRequestDTO request = new TopUpRequestDTO();
        request.setPaymentPhone("123");
        request.setAmount(BigDecimal.valueOf(50000));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> strategy.topUp(request));
        assertEquals("Invalid GoPay phone number.", exception.getMessage());
    }
}
