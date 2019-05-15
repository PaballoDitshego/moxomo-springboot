package za.co.moxomo.config.xmpp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class FirebaseEntity {


    @JsonProperty("message_id")
    private String messageId;
    @JsonProperty("to")
    private String to;

    @JsonProperty("collapse_key")
    private String collapseKey;
    @JsonIgnore // leaving in here to test GCM using default (4 weeks)
    private Long timeToLive = 0L; //5 minutes
    @JsonProperty("delay_while_idle")
    private boolean delayWhileIdle = false;
    @JsonProperty("delivery_receipt_requested")
    private boolean deliveryReceiptRequested =true;
    private final String priority = "normal";

    private Map<String,Object> notification;
    private Map<String,Object> data;
    @JsonProperty("message_type")
    private String messageType;
    
    
    public FirebaseEntity(String messageId, String to, String collapseKey, Map<String, Object> notification, Map<String, Object> data) {
        this.messageId=messageId;
        this.to=to;
        this.collapseKey=collapseKey;
        this.notification=notification;
        this.data=data;
    }

    public FirebaseEntity(String messageId, String to, String messageType) {
        this.messageId=messageId;
        this.to=to;

    }
}
