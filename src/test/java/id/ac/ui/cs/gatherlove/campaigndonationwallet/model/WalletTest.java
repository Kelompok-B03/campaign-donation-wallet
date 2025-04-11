package id.ac.ui.cs.gatherlove.campaigndonationwallet.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void testWalletCreation() {
        // Arrange
        Long id = 1L;
        Long userId = 100L;
        BigDecimal balance = new BigDecimal("1000.00");
        
        // Act
        Wallet wallet = Wallet.builder()
                .id(id)
                .userId(userId)
                .balance(balance)
                .build();
        
        // Assert
        assertEquals(id, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(balance, wallet.getBalance());
    }
    
    @Test
    void testWalletSettersAndGetters() {
        // Arrange
        Wallet wallet = new Wallet();
        Long id = 1L;
        Long userId = 100L;
        BigDecimal balance = new BigDecimal("1000.00");
        
        // Act
        wallet.setId(id);
        wallet.setUserId(userId);
        wallet.setBalance(balance);
        
        // Assert
        assertEquals(id, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(balance, wallet.getBalance());
    }
    
    @Test
    void testNoArgsConstructor() {
        // Act
        Wallet wallet = new Wallet();
        
        // Assert
        assertNull(wallet.getId());
        assertNull(wallet.getUserId());
        assertNull(wallet.getBalance());
    }
    
    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        Long userId = 100L;
        BigDecimal balance = new BigDecimal("1000.00");
        
        // Act
        Wallet wallet = new Wallet(id, userId, balance);
        
        // Assert
        assertEquals(id, wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(balance, wallet.getBalance());
    }
}