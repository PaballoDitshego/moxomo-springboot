package za.co.moxomo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Date;

@RestControllerAdvice
public class MoxomoResponseExceptonHandler extends ResponseEntityExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(MoxomoResponseErrorHandler.class);
	
	@ExceptionHandler(value = { Exception.class})
    public final ResponseEntity<MoxomoResponseErrorDetails> handleTimeout(Exception ex, WebRequest request) {
		MoxomoResponseErrorDetails bulkRefundResponseErrorDetails = new MoxomoResponseErrorDetails(new Date(),ex.getMessage(),request.getDescription(false));
		ex.printStackTrace();
		if(ex instanceof IOException) {
		 return new ResponseEntity<>(bulkRefundResponseErrorDetails, HttpStatus.GATEWAY_TIMEOUT);
		} 
		 return new ResponseEntity<>(bulkRefundResponseErrorDetails, HttpStatus.BAD_REQUEST);
	}


}
