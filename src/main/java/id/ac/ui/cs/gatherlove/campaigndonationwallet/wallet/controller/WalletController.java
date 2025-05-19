package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.ResourceNotFoundException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<Long> createWallet(@RequestParam Long userId) {
        Wallet wallet = walletService.createWallet(userId);
        return ResponseEntity.ok(wallet.getId());
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<WalletBalanceDTO> getWalletBalance(@PathVariable Long userId) {
        try {
            WalletBalanceDTO balance = walletService.getWalletBalance(userId);
            return ResponseEntity.ok(balance);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/topup")
    public ResponseEntity<TransactionDTO> topUpWallet(@Valid @RequestBody TopUpRequestDTO request) {
        try {
            TransactionDTO transaction = walletService.topUpWallet(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/transactions/recent/{userId}")
    public ResponseEntity<List<TransactionDTO>> getRecentTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<TransactionDTO> transactions = walletService.getRecentTransactions(userId, limit);
            return ResponseEntity.ok(transactions);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<Page<TransactionDTO>> getTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionDTO> transactions = walletService.getTransactionHistory(userId, pageable);
            return ResponseEntity.ok(transactions);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/transactions/{userId}/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(
            @PathVariable Long userId,
            @PathVariable TransactionType type) {
        try {
            List<TransactionDTO> transactions = walletService.getTransactionsByType(userId, type);
            return ResponseEntity.ok(transactions);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction type");
        }
    }

    @DeleteMapping("/transactions/{userId}/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> deleteTopUpTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        try {
            boolean deleted = walletService.deleteTopUpTransaction(userId, transactionId);
            return ResponseEntity.ok(Map.of("deleted", deleted));
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (TransactionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/withdraw/{userId}/{campaignId}")
    public ResponseEntity<TransactionDTO> withdrawCampaignFunds(
            @PathVariable Long userId,
            @PathVariable Long campaignId,
            @RequestBody Map<String, BigDecimal> withdrawalRequest) {
        try {
            BigDecimal amount = withdrawalRequest.get("amount");
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid withdrawal amount is required");
            }
            
            TransactionDTO transaction = walletService.withdrawCampaignFunds(userId, campaignId, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (TransactionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    // This endpoint would be called by the Donation service, not directly by users
    @PostMapping("/donate")
    public ResponseEntity<TransactionDTO> recordDonation(
            @RequestBody Map<String, Object> donationRequest) {
        try {
            Long userId = Long.valueOf(donationRequest.get("userId").toString());
            Long campaignId = Long.valueOf(donationRequest.get("campaignId").toString());
            BigDecimal amount = new BigDecimal(donationRequest.get("amount").toString());
            String description = (String) donationRequest.get("description");
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Donation amount must be positive");
            }
            
            TransactionDTO transaction = walletService.recordDonation(userId, campaignId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid numeric values");
        }
    }
}