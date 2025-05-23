package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testWalletCreation() {
        Long id = 1L;
        UUID userId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("1000.00");
        
        Wallet wallet = Wallet.builder()
                .id(id)
                .userId(userId)
                .balance(balance)
                .build();
        
        assertEquals(id, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(balance, wallet.getBalance());
    }
    
    @Test
    void testWalletSettersAndGetters() {
        Wallet wallet = new Wallet();
        Long id = 1L;
        UUID userId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("1000.00");
        
        wallet.setId(id);
        wallet.setUserId(userId);
        wallet.setBalance(balance);
        
        assertEquals(id, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(balance, wallet.getBalance());
    }
    
    @Test
    void testNoArgsConstructor() {
        Wallet wallet = new Wallet();
        
        assertNull(wallet.getId());
        assertNull(wallet.getUserId());
        assertNull(wallet.getBalance());
    }
    
    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        UUID userId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("1000.00");
        
        Wallet wallet = new Wallet(id, userId, balance);
        
        assertEquals(id, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(balance, wallet.getBalance());
    }
}