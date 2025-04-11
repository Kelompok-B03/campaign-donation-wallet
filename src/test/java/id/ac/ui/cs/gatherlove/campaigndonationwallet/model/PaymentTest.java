package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    private Payment payment;
    private UUID dummyDonationId;

    @BeforeEach
    void setUp() {
        dummyDonationId = UUID.randomUUID();
        payment = new Payment(dummyDonationId, 100.0f);
    }

    @Test
    void testProcessPaymentSuccess() {
        payment.setPaymentStatus("INITIATED");

        payment.processPayment();

        assertEquals("PROCESSED", payment.getPaymentStatus());
    }

    @Test
    void testConfirmPaymentChangesStatus() {
        payment.setPaymentStatus("PROCESSED");

        payment.confirmPayment();

        assertEquals("CONFIRMED", payment.getPaymentStatus());
    }

    @Test
    void testInvalidPaymentTransitionThrowsException() {
        payment.setPaymentStatus("FAILED");

        assertThrows(IllegalStateException.class, payment::confirmPayment);
    }
}