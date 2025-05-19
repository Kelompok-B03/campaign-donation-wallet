package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.ResourceNotFoundException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void testResourceNotFoundException() {
        // Test constructor with message
        String errorMessage = "Resource not found";
        ResourceNotFoundException ex1 = new ResourceNotFoundException(errorMessage);
        assertEquals(errorMessage, ex1.getMessage());
        
        // Test constructor with message and cause
        Throwable cause = new RuntimeException("Original cause");
        ResourceNotFoundException ex2 = new ResourceNotFoundException(errorMessage, cause);
        assertEquals(errorMessage, ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
    
    @Test
    void testInsufficientBalanceException() {
        // Test constructor with message
        String errorMessage = "Insufficient balance";
        InsufficientBalanceException ex1 = new InsufficientBalanceException(errorMessage);
        assertEquals(errorMessage, ex1.getMessage());
        
        // Test constructor with message and cause
        Throwable cause = new RuntimeException("Original cause");
        InsufficientBalanceException ex2 = new InsufficientBalanceException(errorMessage, cause);
        assertEquals(errorMessage, ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
    
    @Test
    void testTransactionNotAllowedException() {
        // Test constructor with message
        String errorMessage = "Transaction not allowed";
        TransactionNotAllowedException ex1 = new TransactionNotAllowedException(errorMessage);
        assertEquals(errorMessage, ex1.getMessage());
        
        // Test constructor with message and cause
        Throwable cause = new RuntimeException("Original cause");
        TransactionNotAllowedException ex2 = new TransactionNotAllowedException(errorMessage, cause);
        assertEquals(errorMessage, ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
}