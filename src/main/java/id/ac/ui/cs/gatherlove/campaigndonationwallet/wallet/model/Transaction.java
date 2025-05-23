package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "wallet_id", nullable = false)
    private Long walletId;
    
    @Column(name = "campaign_id")
    private Long campaignId; // Nullable, used for donations and withdrawals
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod; // Only for top-ups
    
    @Column(name = "payment_phone")
    private String paymentPhone; // Phone number for payment method (GOPAY/DANA)
    
    @Column(length = 255)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    
    public enum TransactionType {
        TOP_UP,
        DONATION,
        WITHDRAWAL
    }
    
    public enum PaymentMethod {
        GOPAY,
        DANA
    }
}