package za.co.moxomo.config.xmpp;


import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import za.co.moxomo.domain.Alert;

@Component
public class XmppMessageTransformer {

    private static final Logger log = LoggerFactory.getLogger(XmppMessageTransformer.class);
    private static final String TOPICS = "/topics/";


    @Transformer(inputChannel = "fcmOutboundChannel", outputChannel = "fcmXmppOutboundChannel")
    public Message<org.jivesoftware.smack.packet.Message> transform(Message message) throws Exception {

        Message<org.jivesoftware.smack.packet.Message> gcmMessage = null;
        if (message.getPayload() instanceof Alert) {
            Alert notification = (Alert) message.getPayload();
            gcmMessage = constructGcmMessage(notification);
            log.debug("Message with id {}, transformed to {}", notification.getAlertId(),
                    gcmMessage != null && gcmMessage.getPayload() != null ? gcmMessage.getPayload().toXML(FirebaseXmppMessageCodec.GCM_NAMESPACE) : "null");
        } else {
            gcmMessage = FirebaseXmppMessageCodec.encode(TOPICS.concat("keepalive"), UUID.randomUUID().toString(), null);
        }
        return gcmMessage;

    }

    private Message<org.jivesoftware.smack.packet.Message> constructGcmMessage(Alert notification) throws JsonProcessingException {

        String registrationID = notification.getAlertPreference().getGcmToken();
        String messageId = notification.getAlertId();

        log.debug("Attempting to transform message for registration ID {}, with message id {}", messageId, registrationID);
        String collapseKey = generateCollapseKey(notification);
        log.debug("Generated collapseKey " + collapseKey);
        Map<String, Object> dataPart = createDataPart(notification);


        switch (notification.getAlertType()) {
            case JOB_ALERT:

                break;

            default:
                throw new UnsupportedOperationException("Have to add support for notification type: " + notification.getAlertType());
        }

        return FirebaseXmppMessageCodec.encode(registrationID, messageId, collapseKey, dataPart);
    }

    private String generateCollapseKey(Alert notification) {
        StringBuilder sb = new StringBuilder();
        switch (notification.getAlertType()) {


            default:
                return null;
        }
    }


    private Map<String, Object> createDataPart(Alert notification) {
        return FirebaseXmppMessageCodec.createDataPart(
                notification.getAlertId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getEntityId(),
                notification.getCreatedDateTime(),
                notification.getAlertType(),
                null,
                null,
                notification.getPriority());
        
    }


}






