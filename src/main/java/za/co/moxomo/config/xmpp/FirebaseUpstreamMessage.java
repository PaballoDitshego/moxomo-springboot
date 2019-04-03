package za.co.moxomo.config.xmpp;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FirebaseUpstreamMessage {
	 private String from;

	    private String category;

	    private Map<String, Object> data = new HashMap<>();

	    private String to;

	    @JsonProperty("message_id")
	    private String messageId;


	    @JsonProperty("message_type")
	    private String messageType;

	
	    public String getFrom() {
	        return from;
	    }

	    public String getCategory() {
	        return category;
	    }

	    public Map<String, Object> getData() {
	        return data;
	    }

	
	    public String getTo() {
	        return to;
	    }

	 
	    public String getMessageUid() {
	        return messageId;
	    }


	    public String getMessageType() {
	        return messageType;
	    }

	
	    public String toString() {
	        return "GcmUpstreamMessage{" +
	                "messageType='" + messageType + '\'' +
	                ", from=" + from +
	                ", to=" + to +
	                ", category=" + category +
	                ", data=" + data +
	                '}';
	    }
}
