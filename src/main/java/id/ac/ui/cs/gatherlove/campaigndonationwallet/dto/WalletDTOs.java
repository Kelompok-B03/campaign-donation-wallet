package id.ac.ui.cs.gatherlove.campaigndonationwallet.dto;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.PaymentMethod;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.model.Transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletBalanceDTO {
        private Long userId;
        private BigDecimal balance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDTO {
        private Long id;
        private Long walletId;
        private Long campaignId;
        private BigDecimal amount;
        private TransactionType type;
        private PaymentMethod paymentMethod;
        private String paymentPhone;
        private String description;
        private LocalDateTime timestamp;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUpRequestDTO {
        private Long userId;
        private BigDecimal amount;
        private PaymentMethod paymentMethod;
        private String paymentPhone;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawalRequestDTO {
        private Long userId;
        private Long campaignId;
    }
}