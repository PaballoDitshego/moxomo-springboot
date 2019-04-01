package za.co.moxomo.config.xmpp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.TextUtils;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import za.co.moxomo.enums.AlertType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class FirebaseXmppMessageCodec {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseXmppMessageCodec.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String DEFAULT_ACTION = "NOTIFICATION";
    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";

    private static final String notificationIcon = "@drawable/app_icon";

    private FirebaseXmppMessageCodec() {
        // utility
    }

    /**
     * Used currently of ACK messages
     */
    public static org.springframework.messaging.Message<Message> encode(String registrationID, String messageId, String messageType) {
        FirebaseEntity gcmPayload = new FirebaseEntity(messageId, registrationID, messageType);
        return constructGcmMessage(gcmPayload);
    }

    public static org.springframework.messaging.Message<Message> encode(String registrationID, String messageId, String collapseKey,
                                                                        Map<String, Object> dataPart) {
        logger.debug("Generated collapseKey " + collapseKey);

        FirebaseEntity gcmPayload = new FirebaseEntity(messageId, registrationID, collapseKey, dataPart, null);
        return constructGcmMessage(gcmPayload);
    }

    private static org.springframework.messaging.Message<Message> constructGcmMessage(FirebaseEntity gcmPayload) {
        Message xmppMessage = new Message();
        try {
            String gcmPayloadJson = mapper.writeValueAsString(gcmPayload);
            logger.info("payload "+gcmPayloadJson);
            StandardExtensionElement.Builder builder = StandardExtensionElement.builder(GCM_ELEMENT_NAME, GCM_NAMESPACE);
            xmppMessage.addExtension(builder.setText(gcmPayloadJson).build());

            return MessageBuilder.withPayload(xmppMessage).build();

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error while trying to encode GCM XMPP message: " + e.getMessage(), e);
        }
    }

    public static Map<String, Object> createDataPart(String alertId, String title,  String description, String entityId,
                                                     Instant createdDateTime, AlertType alertType, String entityType,
                                                     AndroidClickActionType clickAction, int priority) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);

        

      
        data.put("notificationUid", alertId);
        data.put("body", description);
        data.put("id", entityId);
        data.put("created_date_time", createdDateTime);
        data.put("alert_type", alertType);
        data.put("entity_type", entityType);
        data.put("click_action", clickAction);
        data.put("priority", priority);

        return data;
    }


}