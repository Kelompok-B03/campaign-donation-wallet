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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateWallet_ReturnsOk() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUserId(1L);
        
        when(walletService.createWallet(1L)).thenReturn(wallet);
        
        mockMvc.perform(post("/api/wallet")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
                
        verify(walletService).createWallet(1L);
    }

    @Test
    void testGetWalletBalance_ReturnsOk() throws Exception {
        WalletBalanceDTO balanceDTO = WalletBalanceDTO.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletService.getWalletBalance(1L)).thenReturn(balanceDTO);

        mockMvc.perform(get("/api/wallet/balance")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }
    
    @Test
    void testGetWalletBalance_NotFound() throws Exception {
        when(walletService.getWalletBalance(999L)).thenThrow(new ResourceNotFoundException("Wallet not found"));
        
        mockMvc.perform(get("/api/wallet/balance")
                .param("userId", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTopUpWallet_ReturnsCreated() throws Exception {
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(1L)
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
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(999L)
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
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(1L)
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
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(2L).amount(BigDecimal.valueOf(20)).type(TransactionType.DONATION).build()
        );
        
        when(walletService.getRecentTransactions(eq(1L), eq(5))).thenReturn(transactions);
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", "1")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[1].amount").value(20));
    }
    
    @Test
    void testGetTransactions_WithType_ReturnsOk() throws Exception {
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(3L).amount(BigDecimal.valueOf(30)).type(TransactionType.TOP_UP).build()
        );
        
        when(walletService.getTransactionsByType(eq(1L), eq(TransactionType.TOP_UP))).thenReturn(transactions);
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", "1")
                .param("type", "TOP_UP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[1].amount").value(30));
    }
    
    @Test
    void testGetTransactions_WithPagination_ReturnsOk() throws Exception {
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(2L).amount(BigDecimal.valueOf(20)).type(TransactionType.DONATION).build()
        );
        
        Page<TransactionDTO> page = new PageImpl<>(transactions);
        
        when(walletService.getTransactionHistory(eq(1L), any(Pageable.class))).thenReturn(page);
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", "1")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].amount").value(10))
                .andExpect(jsonPath("$.content[1].amount").value(20));
    }
    
    @Test
    void testGetTransactions_ResourceNotFound() throws Exception {
        when(walletService.getTransactionHistory(eq(999L), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));
        
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", "999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetTransactions_InvalidType() throws Exception {
        mockMvc.perform(get("/api/wallet/transactions")
                .param("userId", "1")
                .param("type", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testDeleteTopUpTransaction_ReturnsOk() throws Exception {
        when(walletService.deleteTopUpTransaction(1L, 1L)).thenReturn(true);
        
        mockMvc.perform(delete("/api/wallet/transactions/1")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }
    
    @Test
    void testDeleteTopUpTransaction_NotFound() throws Exception {
        when(walletService.deleteTopUpTransaction(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));
        
        mockMvc.perform(delete("/api/wallet/transactions/999")
                .param("userId", "1"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteTopUpTransaction_NotAllowed() throws Exception {
        when(walletService.deleteTopUpTransaction(1L, 2L))
                .thenThrow(new TransactionNotAllowedException("Cannot delete this transaction"));
        
        mockMvc.perform(delete("/api/wallet/transactions/2")
                .param("userId", "1"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testWithdrawCampaignFunds_ReturnsCreated() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.WITHDRAWAL)
                .timestamp(LocalDateTime.now())
                .build();
        
        Map<String, BigDecimal> withdrawalRequest = new HashMap<>();
        withdrawalRequest.put("amount", BigDecimal.valueOf(100));
        
        when(walletService.withdrawCampaignFunds(eq(1L), eq(5L), any(BigDecimal.class)))
                .thenReturn(transactionDTO);
        
        mockMvc.perform(post("/api/wallet/5/withdrawals")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"));
    }
    
    @Test
    void testWithdrawCampaignFunds_InvalidAmount() throws Exception {
        Map<String, BigDecimal> withdrawalRequest = new HashMap<>();
        withdrawalRequest.put("amount", BigDecimal.valueOf(-50));
        
        mockMvc.perform(post("/api/wallet/5/withdrawals")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRecordDonation_ReturnsCreated() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.DONATION)
                .timestamp(LocalDateTime.now())
                .description("Support for campaign")
                .build();
        
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", 1L);
        donationRequest.put("campaignId", 5L);
        donationRequest.put("amount", "50");
        donationRequest.put("description", "Support for campaign");
        
        when(walletService.recordDonation(eq(1L), eq(5L), any(BigDecimal.class), eq("Support for campaign")))
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
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", 1L);
        donationRequest.put("campaignId", 5L);
        donationRequest.put("amount", "-50");
        donationRequest.put("description", "Support for campaign");
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRecordDonation_InsufficientBalance() throws Exception {
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", 1L);
        donationRequest.put("campaignId", 5L);
        donationRequest.put("amount", "5000");
        donationRequest.put("description", "Support for campaign");
        
        when(walletService.recordDonation(eq(1L), eq(5L), any(BigDecimal.class), anyString()))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testRecordDonation_InvalidNumericValues() throws Exception {
        Map<String, Object> donationRequest = new HashMap<>();
        donationRequest.put("userId", "invalid");
        donationRequest.put("campaignId", 5L);
        donationRequest.put("amount", "50");
        donationRequest.put("description", "Support for campaign");
        
        mockMvc.perform(post("/api/wallet/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donationRequest)))
                .andExpect(status().isBadRequest());
    }
}