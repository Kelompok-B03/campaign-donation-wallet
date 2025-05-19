package id.ac.ui.cs.gatherlove.campaigndonationwallet.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionNotAllowedException extends RuntimeException {
    
    public TransactionNotAllowedException(String message) {
        super(message);
    }
    
    public TransactionNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}