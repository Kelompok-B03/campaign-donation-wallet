package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.controller.WalletController;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.ResourceNotFoundException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateWallet_ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUserId(userId);
        
        when(walletService.createWallet(userId)).thenReturn(wallet);
        
        mockMvc.perform(post("/api/wallet")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
                
        verify(walletService).createWallet(userId);
    }

    @Test
    void testGetWalletBalance_ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        WalletBalanceDTO balanceDTO = WalletBalanceDTO.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletService.getWalletBalance(userId)).thenReturn(balanceDTO);

        mockMvc.perform(get("/api/wallet/balance")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }
    
    @Test
    void testGetWalletBalance_NotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(walletService.getWalletBalance(userId)).thenThrow(new ResourceNotFoundException("Wallet not found"));
        
        mockMvc.perform(get("/api/wallet/balance")
                .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTopUpWallet_ReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(BigDecimal.TEN)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id(1L)
                .amount(BigDecimal.TEN)
                .type(TransactionType.TOP_UP)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        when(walletService.topUpWallet(any(TopUpRequestDTO.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/wallet/top-ups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(10));
    }
    
    @Test
    void testTopUpWallet_WalletNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(BigDecimal.TEN)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        when(walletService.topUpWallet(any(TopUpRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));

        mockMvc.perform(post("/api/wallet/top-ups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testTopUpWallet_BadRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(BigDecimal.valueOf(-10)) // Negative amount
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        when(walletService.topUpWallet(any(TopUpRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Amount must be positive"));

        mockMvc.perform(post("/api/wallet/top-ups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetTransactions_WithLimit_ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(2L).amount(BigDecimal.valueOf(20)).type(TransactionType.DONATION).build()
        );
        
        when(walletService.getRecentTransactions(eq(userId), eq(5))).thenReturn(transactions);
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", userId.toString())
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[1].amount").value(20));
    }
    
    @Test
    void testGetTransactions_WithType_ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(3L).amount(BigDecimal.valueOf(30)).type(TransactionType.TOP_UP).build()
        );
        
        when(walletService.getTransactionsByType(eq(userId), eq(TransactionType.TOP_UP))).thenReturn(transactions);
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", userId.toString())
                .param("type", "TOP_UP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[1].amount").value(30));
    }
    
    @Test
    void testGetTransactions_WithPagination_ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(2L).amount(BigDecimal.valueOf(20)).type(TransactionType.DONATION).build()
        );
        
        Page<TransactionDTO> page = new PageImpl<>(transactions);
        
        when(walletService.getTransactionHistory(eq(userId), any(Pageable.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", userId.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(10))
                .andExpect(jsonPath("$.content[1].amount").value(20));
    }
    
    @Test
    void testGetTransactions_ResourceNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(walletService.getTransactionHistory(eq(userId), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetTransactions_InvalidType() throws Exception {
        UUID userId = UUID.randomUUID();
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", userId.toString())
                .param("type", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testDeleteTopUpTransaction_ReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        when(walletService.deleteTopUpTransaction(userId, 1L)).thenReturn(true);
        
        mockMvc.perform(delete("/api/wallet/transactions/1")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }
    
    @Test
    void testDeleteTopUpTransaction_NotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(walletService.deleteTopUpTransaction(userId, 999L))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));
        
        mockMvc.perform(delete("/api/wallet/transactions/999")
                .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteTopUpTransaction_NotAllowed() throws Exception {
        UUID userId = UUID.randomUUID();
        when(walletService.deleteTopUpTransaction(userId, 2L))
                .thenThrow(new TransactionNotAllowedException("Cannot delete this transaction"));
        
        mockMvc.perform(delete("/api/wallet/transactions/2")
                .param("userId", userId.toString()))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testWithdrawCampaignFunds_ReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        String campaignId = "campaign-5";
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.WITHDRAWAL)
                .timestamp(LocalDateTime.now())
                .build();
        
        Map<String, BigDecimal> withdrawalRequest = new HashMap<>();
        withdrawalRequest.put("amount", BigDecimal.valueOf(100));
        
        when(walletService.withdrawCampaignFunds(eq(userId), eq(campaignId), any(BigDecimal.class)))
                .thenReturn(transactionDTO);
        
        mockMvc.perform(post("/api/wallet/" + campaignId + "/withdrawals")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"));
    }
    
    @Test
    void testWithdrawCampaignFunds_InvalidAmount() throws Exception {
        UUID userId = UUID.randomUUID();
        String campaignId = "campaign-5";
        Map<String, BigDecimal> withdrawalRequest = new HashMap<>();
        withdrawalRequest.put("amount", BigDecimal.valueOf(-50));
        
        mockMvc.perform(post("/api/wallet/" + campaignId + "/withdrawals")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRecordDonation_ReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        String campaignId = "campaign-5";
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .description("Support for campaign")
                .build();
        
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", userId.toString());
        donationRequest.put("campaignId", campaignId);
        donationRequest.put("amount", "50");
        donationRequest.put("description", "Support for campaign");
        
        when(walletService.recordDonation(eq(userId), eq(campaignId), any(BigDecimal.class), eq("Support for campaign")))
                .thenReturn(transactionDTO);
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50))
                .andExpect(jsonPath("$.type").value("DONATION"))
                .andExpect(jsonPath("$.description").value("Support for campaign"));
    }
    
    @Test
    void testRecordDonation_InvalidAmount() throws Exception {
        UUID userId = UUID.randomUUID();
        String campaignId = "campaign-5";
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", userId.toString());
        donationRequest.put("campaignId", campaignId);
        donationRequest.put("amount", "-50");
        donationRequest.put("description", "Support for campaign");
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRecordDonation_InsufficientBalance() throws Exception {
        UUID userId = UUID.randomUUID();
        String campaignId = "campaign-5";
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", userId.toString());
        donationRequest.put("campaignId", campaignId);
        donationRequest.put("amount", "5000");
        donationRequest.put("description", "Support for campaign");
        
        when(walletService.recordDonation(eq(userId), eq(campaignId), any(BigDecimal.class), anyString()))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRecordDonation_InvalidUUIDValues() throws Exception {
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", "invalid-uuid");
        donationRequest.put("campaignId", "campaign-5");
        donationRequest.put("amount", "50");
        donationRequest.put("description", "Support for campaign");
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest());
    }
}