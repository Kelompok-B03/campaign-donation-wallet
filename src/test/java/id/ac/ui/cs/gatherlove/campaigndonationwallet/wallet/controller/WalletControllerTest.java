package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.controller.WalletController;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    void testGetWalletBalance_ReturnsOk() throws Exception {
        WalletBalanceDTO balanceDTO = WalletBalanceDTO.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletService.getWalletBalance(1L)).thenReturn(balanceDTO);

        mockMvc.perform(get("/api/wallet/balance/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
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

        mockMvc.perform(post("/api/wallet/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(10));
    }
}
