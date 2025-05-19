
package id.ac.ui.cs.gatherlove.campaigndonationwallet.strategy;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.dto.WalletDTOs.TopUpRequestDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DanaTopUpStrategyTest {

    private final DanaTopUpStrategy strategy = new DanaTopUpStrategy();

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
        request.setPaymentPhone("xyz");
        request.setAmount(BigDecimal.valueOf(75000));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> strategy.topUp(request));
        assertEquals("Invalid DANA phone number.", exception.getMessage());
    }
}
