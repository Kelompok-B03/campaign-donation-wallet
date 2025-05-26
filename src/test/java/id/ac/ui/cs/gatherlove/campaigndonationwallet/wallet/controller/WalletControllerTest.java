package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.controller;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TopUpRequestDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.TransactionDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.dto.WalletDTOs.WalletBalanceDTO;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.ResourceNotFoundException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Transaction.TransactionType;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private JwtAuthenticationToken createJwtToken(UUID userId, boolean isAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claims(c -> c.putAll(claims))
                .build();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }

    @Test
    void testCreateWallet() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUserId(userId);

        when(walletService.createWallet(userId)).thenReturn(wallet);

        ResponseEntity<Long> response = walletController.createWallet(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody());
        verify(walletService).createWallet(userId);
    }

    @Test
    void testGetWalletBalance() {
        UUID userId = UUID.randomUUID();
        WalletBalanceDTO balanceDTO = WalletBalanceDTO.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletService.getWalletBalance(userId)).thenReturn(balanceDTO);

        Authentication auth = createJwtToken(userId, false);
        ResponseEntity<WalletBalanceDTO> response = walletController.getWalletBalance(userId, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(1000), response.getBody().getBalance());
        verify(walletService).getWalletBalance(userId);
    }

    @Test
    void testGetWalletBalance_NotFound() {
        UUID userId = UUID.randomUUID();
        when(walletService.getWalletBalance(userId)).thenThrow(new ResourceNotFoundException("Wallet not found"));

        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.getWalletBalance(userId, auth));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testGetWalletBalance_Forbidden() {
        UUID userId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        
        Authentication auth = createJwtToken(differentUserId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.getWalletBalance(userId, auth));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testGetWalletBalance_AdminAccess() {
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        WalletBalanceDTO balanceDTO = WalletBalanceDTO.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletService.getWalletBalance(userId)).thenReturn(balanceDTO);

        Authentication auth = createJwtToken(adminId, true);
        ResponseEntity<WalletBalanceDTO> response = walletController.getWalletBalance(userId, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(1000), response.getBody().getBalance());
    }

    @Test
    void testTopUpWallet() {
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

        Authentication auth = createJwtToken(userId, false);
        ResponseEntity<TransactionDTO> response = walletController.topUpWallet(request, auth);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(BigDecimal.TEN, response.getBody().getAmount());
        verify(walletService).topUpWallet(request);
    }

    @Test
    void testTopUpWallet_WalletNotFound() {
        UUID userId = UUID.randomUUID();
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(BigDecimal.TEN)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        when(walletService.topUpWallet(any(TopUpRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));

        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.topUpWallet(request, auth));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testTopUpWallet_BadRequest() {
        UUID userId = UUID.randomUUID();
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(userId)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        when(walletService.topUpWallet(any(TopUpRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Amount must be positive"));

        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.topUpWallet(request, auth));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testTopUpWallet_Forbidden() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        TopUpRequestDTO request = TopUpRequestDTO.builder()
                .userId(userId)
                .amount(BigDecimal.TEN)
                .paymentMethod(Transaction.PaymentMethod.GOPAY)
                .paymentPhone("081234567890")
                .build();

        Authentication auth = createJwtToken(otherUserId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> walletController.topUpWallet(request, auth));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testTopUpWallet_AdminCanTopUpOtherUser() {
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
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

        Authentication auth = createJwtToken(adminId, true);
        ResponseEntity<TransactionDTO> response = walletController.topUpWallet(request, auth);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(BigDecimal.TEN, response.getBody().getAmount());
    }

    @Test
    void testGetTransactions_WithLimit() {
        UUID userId = UUID.randomUUID();
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(2L).amount(BigDecimal.valueOf(20)).type(TransactionType.DONATION).build()
        );

        when(walletService.getRecentTransactions(eq(userId), eq(5))).thenReturn(transactions);

        Authentication auth = createJwtToken(userId, false);
        ResponseEntity<?> response = walletController.getTransactions(userId, null, 0, 10, 5, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<TransactionDTO> responseBody = (List<TransactionDTO>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals(BigDecimal.TEN, responseBody.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(20), responseBody.get(1).getAmount());
    }

    @Test
    void testGetTransactions_WithType() {
        UUID userId = UUID.randomUUID();
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(3L).amount(BigDecimal.valueOf(30)).type(TransactionType.TOP_UP).build()
        );

        when(walletService.getTransactionsByType(eq(userId), eq(TransactionType.TOP_UP))).thenReturn(transactions);

        Authentication auth = createJwtToken(userId, false);
        ResponseEntity<?> response = walletController.getTransactions(userId, "TOP_UP", 0, 10, null, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<TransactionDTO> responseBody = (List<TransactionDTO>) response.getBody();
        assertEquals(2, responseBody.size());
        assertEquals(BigDecimal.TEN, responseBody.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(30), responseBody.get(1).getAmount());
    }

    @Test
    void testGetTransactions_WithPagination() {
        UUID userId = UUID.randomUUID();
        List<TransactionDTO> transactions = Arrays.asList(
                TransactionDTO.builder().id(1L).amount(BigDecimal.TEN).type(TransactionType.TOP_UP).build(),
                TransactionDTO.builder().id(2L).amount(BigDecimal.valueOf(20)).type(TransactionType.DONATION).build()
        );

        Page<TransactionDTO> page = new PageImpl<>(transactions);

        when(walletService.getTransactionHistory(eq(userId), any(Pageable.class))).thenReturn(page);

        Authentication auth = createJwtToken(userId, false);
        ResponseEntity<?> response = walletController.getTransactions(userId, null, 0, 10, null, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<TransactionDTO> responseBody = (Page<TransactionDTO>) response.getBody();
        assertEquals(2, responseBody.getContent().size());
    }

    @Test
    void testGetTransactions_ResourceNotFound() {
        UUID userId = UUID.randomUUID();
        when(walletService.getTransactionHistory(eq(userId), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));

        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.getTransactions(userId, null, 0, 10, null, auth));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testGetTransactions_InvalidType() {
        UUID userId = UUID.randomUUID();
        
        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.getTransactions(userId, "INVALID_TYPE", 0, 10, null, auth));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testDeleteTopUpTransaction() {
        UUID userId = UUID.randomUUID();
        Long transactionId = 1L;
        when(walletService.deleteTopUpTransaction(userId, transactionId)).thenReturn(true);

        Authentication auth = createJwtToken(userId, false);
        ResponseEntity<Map<String, Boolean>> response = walletController.deleteTopUpTransaction(userId, transactionId, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("deleted"));
        verify(walletService).deleteTopUpTransaction(userId, transactionId);
    }

    @Test
    void testDeleteTopUpTransaction_NotFound() {
        UUID userId = UUID.randomUUID();
        Long transactionId = 999L;
        when(walletService.deleteTopUpTransaction(userId, transactionId))
                .thenThrow(new ResourceNotFoundException("Transaction not found"));

        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.deleteTopUpTransaction(userId, transactionId, auth));
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testDeleteTopUpTransaction_NotAllowed() {
        UUID userId = UUID.randomUUID();
        Long transactionId = 2L;
        when(walletService.deleteTopUpTransaction(userId, transactionId))
                .thenThrow(new TransactionNotAllowedException("Cannot delete this transaction"));

        Authentication auth = createJwtToken(userId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, 
            () -> walletController.deleteTopUpTransaction(userId, transactionId, auth));
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void testDeleteTopUpTransaction_Forbidden() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        Long transactionId = 1L;

        Authentication auth = createJwtToken(otherUserId, false);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> walletController.deleteTopUpTransaction(userId, transactionId, auth));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testDeleteTopUpTransaction_AdminCanDeleteOtherUser() {
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Long transactionId = 1L;

        when(walletService.deleteTopUpTransaction(userId, transactionId)).thenReturn(true);

        Authentication auth = createJwtToken(adminId, true);
        ResponseEntity<Map<String, Boolean>> response = walletController.deleteTopUpTransaction(userId, transactionId, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("deleted"));
    }
    
    @Test
    void testValidateUserAccess_InvalidAuthenticationToken() throws Exception {
        UUID userId = UUID.randomUUID();
        Authentication invalidAuth = mock(Authentication.class); // Not a JwtAuthenticationToken

        var method = WalletController.class.getDeclaredMethod("validateUserAccess", UUID.class, Authentication.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(walletController, userId, invalidAuth);
        });

        Throwable cause = exception.getCause();
        assertTrue(cause instanceof SecurityException);
        assertTrue(cause.getMessage().contains("Invalid authentication token"));
    }
}