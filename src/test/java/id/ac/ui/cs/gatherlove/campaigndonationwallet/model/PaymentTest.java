package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void testProcessPaymentSuccess() {
        Payment payment = new Payment();
        payment.setPaymentStatus("INITIATED");

        payment.processPayment();

        assertEquals("PROCESSED", payment.getPaymentStatus());
    }

    @Test
    void testConfirmPaymentChangesStatus() {
        Payment payment = new Payment();
        payment.setPaymentStatus("PROCESSED");

        payment.confirmPayment();

        assertEquals("CONFIRMED", payment.getPaymentStatus());
    }

    @Test
    void testInvalidPaymentTransitionThrowsException() {
        Payment payment = new Payment();
        payment.setPaymentStatus("FAILED");

        assertThrows(IllegalStateException.class, payment::confirmPayment);
    }
}