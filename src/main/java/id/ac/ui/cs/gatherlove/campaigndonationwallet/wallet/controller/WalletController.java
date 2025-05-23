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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Only FUNDRAISER role can create wallets
    @PostMapping
    public ResponseEntity<Long> createWallet(@RequestParam UUID userId) {
        Wallet wallet = walletService.createWallet(userId);
        return ResponseEntity.ok(wallet.getId());
    }

    // Any authenticated user can check their own wallet balance
    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletBalanceDTO> getWalletBalance(@RequestParam UUID userId, Authentication authentication) {
        try {
            validateUserAccess(userId, authentication);
            WalletBalanceDTO balance = walletService.getWalletBalance(userId);
            return ResponseEntity.ok(balance);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping("/top-ups")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionDTO> topUpWallet(@Valid @RequestBody TopUpRequestDTO request, Authentication authentication) {
        try {
            validateUserAccess(request.getUserId(), authentication);
            TransactionDTO transaction = walletService.topUpWallet(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getTransactions(
            @RequestParam UUID userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer limit, 
            Authentication authentication) {
        try {
            validateUserAccess(userId, authentication);
            
            if (limit != null) {
                List<TransactionDTO> transactions = walletService.getRecentTransactions(userId, limit);
                return ResponseEntity.ok(transactions);
            } else if (type != null) {
                TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
                List<TransactionDTO> transactions = walletService.getTransactionsByType(userId, transactionType);
                return ResponseEntity.ok(transactions);
            } else {
                Pageable pageable = PageRequest.of(page, size);
                Page<TransactionDTO> transactions = walletService.getTransactionHistory(userId, pageable);
                return ResponseEntity.ok(transactions);
            }
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction type");
        }
    }

    @DeleteMapping("/transactions/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> deleteTopUpTransaction(
            @RequestParam UUID userId,
            @PathVariable Long transactionId, 
            Authentication authentication) {
        try {
            validateUserAccess(userId, authentication);
            boolean deleted = walletService.deleteTopUpTransaction(userId, transactionId);
            return ResponseEntity.ok(Map.of("deleted", deleted));
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (TransactionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{campaignId}/withdrawals")
    @PreAuthorize("hasRole('FUNDRAISER')")
    public ResponseEntity<TransactionDTO> withdrawCampaignFunds(
            @RequestParam UUID userId,
            @PathVariable String campaignId,
            @RequestBody Map<String, BigDecimal> withdrawalRequest, 
            Authentication authentication) {
        try {
            validateUserAccess(userId, authentication);
            BigDecimal amount = withdrawalRequest.get("amount");
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid withdrawal amount is required");
            }

            TransactionDTO transaction = walletService.withdrawCampaignFunds(userId, campaignId, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (TransactionNotAllowedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/donations")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<TransactionDTO> recordDonation(
            @RequestBody Map<String, Object> donationRequest, 
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(donationRequest.get("userId").toString());
            validateUserAccess(userId, authentication);

            String campaignId = donationRequest.get("campaignId").toString();
            BigDecimal amount = new BigDecimal(donationRequest.get("amount").toString());
            String description = (String) donationRequest.get("description");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Donation amount must be positive");
            }
            TransactionDTO transaction = walletService.recordDonation(userId, campaignId, amount, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID or numeric values");
        }
    }
    
    // Utility method to validate if the authenticated user can access the resource
    private void validateUserAccess(UUID userId, Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            String userIdFromToken = jwtToken.getToken().getClaimAsString("userId");
            
            // Allow access for matching user IDs or admin users
            if (userIdFromToken != null && userIdFromToken.equals(userId.toString())) {
                return;
            }
            
            // Check if user has admin role
            boolean isAdmin = jwtToken.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
                
            if (isAdmin) {
                return;
            }
            
            throw new SecurityException("You don't have permission to access this resource");
        }
        
        throw new SecurityException("Invalid authentication token");
    }
}