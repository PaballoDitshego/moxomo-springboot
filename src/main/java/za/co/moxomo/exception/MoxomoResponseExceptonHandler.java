package com.vodacom.er.bulkrefunds.service.exception;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.vodacom.er.bulkrefunds.service.exception.model.BulkRefundsResponseErrorDetails;

@RestControllerAdvice
public class BulkRefundsResponseExceptonHandler extends ResponseEntityExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(BulkRefundsResponseErrorHandler.class);
	
	@ExceptionHandler(value = { Exception.class})
    public final ResponseEntity<BulkRefundsResponseErrorDetails> handleTimeout(Exception ex, WebRequest request) {
		BulkRefundsResponseErrorDetails bulkRefundResponseErrorDetails = new BulkRefundsResponseErrorDetails(new Date(),ex.getMessage(),request.getDescription(false));
		logger.info("Throwing exception {}", ex.getMessage());
		ex.printStackTrace();
		if(ex instanceof IOException) {
		 return new ResponseEntity<>(bulkRefundResponseErrorDetails, HttpStatus.GATEWAY_TIMEOUT);
		} 
		 return new ResponseEntity<>(bulkRefundResponseErrorDetails, HttpStatus.BAD_REQUEST);
	}


}
