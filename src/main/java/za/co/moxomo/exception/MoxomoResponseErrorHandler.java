package za.co.moxomo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class MoxomoResponseErrorHandler implements ResponseErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(MoxomoResponseErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
    	   HttpStatus.Series series = response.getStatusCode().series();
           return (HttpStatus.Series.CLIENT_ERROR.equals(series)
                   || HttpStatus.Series.SERVER_ERROR.equals(series));
       }

}
