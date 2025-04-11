package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    private UUID paymentId;

    private UUID donationId;

    private Float amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private Date createdAt;

    public Payment(UUID donationId, Float amount) {
        if (donationId == null) throw new NullPointerException("Donation ID must not be null");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        this.paymentId = UUID.randomUUID();
        this.donationId = donationId;
        this.amount = amount;
        this.status = PaymentStatus.INITIATED;
        this.createdAt = new Date();
    }

    public void setPaymentStatus(String statusString) {
        try {
            this.status = PaymentStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment status: " + statusString);
        }
    }

    public String getPaymentStatus() {
        return this.status.toString();
    }

    public void processPayment() {
        // Simulate external processing logic
        if (this.amount <= 0) {
            this.status = PaymentStatus.FAILED;
        } else {
            this.status = PaymentStatus.PROCESSED;
        }
    }

    public void confirmPayment() {
        if (this.status != PaymentStatus.PROCESSED) {
            throw new IllegalStateException("Only pending payments can be confirmed.");
        }
        this.status = PaymentStatus.CONFIRMED;
    }
}
