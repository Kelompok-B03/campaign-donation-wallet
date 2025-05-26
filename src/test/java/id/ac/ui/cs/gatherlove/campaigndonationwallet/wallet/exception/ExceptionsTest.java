package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception;

import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.ResourceNotFoundException;
import id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception.TransactionNotAllowedException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

class ExceptionsTest {

    @Test
    void testResourceNotFoundException() {
        String errorMessage = "Resource not found";
        ResourceNotFoundException ex1 = new ResourceNotFoundException(errorMessage);
        assertEquals(errorMessage, ex1.getMessage());
        
        Throwable cause = new RuntimeException("Original cause");
        ResourceNotFoundException ex2 = new ResourceNotFoundException(errorMessage, cause);
        assertEquals(errorMessage, ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
    
    @Test
    void testInsufficientBalanceException() {
        String errorMessage = "Insufficient balance";
        InsufficientBalanceException ex1 = new InsufficientBalanceException(errorMessage);
        assertEquals(errorMessage, ex1.getMessage());
        
        Throwable cause = new RuntimeException("Original cause");
        InsufficientBalanceException ex2 = new InsufficientBalanceException(errorMessage, cause);
        assertEquals(errorMessage, ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }
    
    @Test
    void testTransactionNotAllowedException() {
        String errorMessage = "Transaction not allowed";
        TransactionNotAllowedException ex1 = new TransactionNotAllowedException(errorMessage);
        assertEquals(errorMessage, ex1.getMessage());
        
        Throwable cause = new RuntimeException("Original cause");
        TransactionNotAllowedException ex2 = new TransactionNotAllowedException(errorMessage, cause);
        assertEquals(errorMessage, ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    void testHandleResponseStatusException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String reason = "Forbidden";
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.FORBIDDEN, reason);
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatusException(ex, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(reason, response.getBody().get("message"));
        assertEquals(403, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void testHandleValidationExceptions() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Mock BindingResult and FieldError
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");
        Mockito.when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("fieldName"));
        assertEquals("must not be null", response.getBody().get("fieldName"));
    }

    @Test
    void testHandleGenericException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Something went wrong"));
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleAuthenticationException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        AuthenticationException ex = new AuthenticationException("Auth failed") {};
        ResponseEntity<Map<String, String>> response = handler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication failed", response.getBody().get("error"));
        assertEquals("Auth failed", response.getBody().get("message"));
    }

    @Test
    void testHandleAccessDeniedException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        AccessDeniedException ex = new AccessDeniedException("Access denied here");
        ResponseEntity<Map<String, String>> response = handler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().get("error"));
        assertEquals("Access denied here", response.getBody().get("message"));
    }

    @Test
    void testHandleSecurityException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        SecurityException ex = new SecurityException("Security violation!");
        ResponseEntity<Map<String, String>> response = handler.handleSecurityException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Security violation", response.getBody().get("error"));
        assertEquals("Security violation!", response.getBody().get("message"));
    }

    @Test
    void testErrorResponseSetters() {
        LocalDateTime now = LocalDateTime.now();
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(400, "msg", now);

        errorResponse.setStatus(401);
        errorResponse.setMessage("changed");
        LocalDateTime newTime = now.plusMinutes(1);
        errorResponse.setTimestamp(newTime);

        assertEquals(401, errorResponse.getStatus());
        assertEquals("changed", errorResponse.getMessage());
        assertEquals(newTime, errorResponse.getTimestamp());
    }
}