package za.co.moxomo.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "timestamp", "message", "details" })

public class MoxomoResponseErrorDetails {
	@JsonProperty("timestamp")
	private Date timestamp;
	@JsonProperty("message")
	private String message;
	@JsonProperty("details")
	private String details;
	
	public MoxomoResponseErrorDetails(Date timestamp, String message, String details) {
		super();
		this.message = message;
		this.details = details;
	}

	@JsonProperty("timestamp")
	public Date getTimestamp() {
		return timestamp;
	}
	@JsonProperty("timestamp")
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("message")
	public String getMessage() {
		return message;
	}

	@JsonProperty("message")
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty("details")
	public String getDetails() {
		return details;
	}

	@JsonProperty("details")
	public void setDetails(String details) {
		this.details = details;
	}



}
