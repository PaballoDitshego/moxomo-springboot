package com.vodacom.er.bulkrefunds.service.exception;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class BulkRefundsResponseErrorHandler implements ResponseErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(BulkRefundsResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
    	   HttpStatus.Series series = response.getStatusCode().series();
           return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                   || HttpStatus.Series.SERVER_ERROR.equals(series));
       }

}
