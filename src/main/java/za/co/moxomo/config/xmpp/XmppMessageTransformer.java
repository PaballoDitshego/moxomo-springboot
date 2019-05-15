package za.co.moxomo.config.xmpp;


import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import za.co.moxomo.domain.Notification;



@Component
public class XmppMessageTransformer {

    private static final Logger log = LoggerFactory.getLogger(XmppMessageTransformer.class);
    private static final String TOPICS = "/topics/";
    public static final String JOB_ALERT = "JOB_ALERT";


    @Transformer(inputChannel = "fcmOutboundChannel", outputChannel = "fcmXmppOutboundChannel")
    public Message<org.jivesoftware.smack.packet.Message> transform(Message message) throws Exception {

        Message<org.jivesoftware.smack.packet.Message> gcmMessage = null;
        if (message.getPayload() instanceof Notification) {
            Notification notification = (Notification) message.getPayload();
            gcmMessage = constructGcmMessage(notification);
            log.info("Message with id {}, transformed to {}", notification.getId(),
                    gcmMessage != null && gcmMessage.getPayload() != null ? gcmMessage.getPayload().toXML(FirebaseXmppMessageCodec.GCM_NAMESPACE) : "null");
        } else {
            gcmMessage = FirebaseXmppMessageCodec.encode(TOPICS.concat("keepalive"), UUID.randomUUID().toString(), null);
        }
        return gcmMessage;

    }

    private Message<org.jivesoftware.smack.packet.Message> constructGcmMessage(Notification notification) throws JsonProcessingException {

        String registrationID = notification.getGcmToken();
        String messageId = notification.getId();

        log.info("Attempting to transform message for registration ID {}, with message id {}", registrationID, messageId);
        String collapseKey = generateCollapseKey(notification);
        log.info("Generated collapseKey " + collapseKey);
        Map<String, Object> dataPart = createDataPart(notification);
        String title;
        String body;


        switch (notification.getAlertType()) {
            case JOB_ALERT:
                title = notification.getTitle();
                body = notification.getDescription();


                break;

            default:
                throw new UnsupportedOperationException("Have to add support for notification type: " + notification.getAlertType());
        }

        return FirebaseXmppMessageCodec.encode(registrationID, messageId, collapseKey, dataPart);
    }

    private String generateCollapseKey(Notification notification) {
        StringBuilder sb = new StringBuilder();
        switch (notification.getAlertType()) {
            case JOB_ALERT:
                return "fsfsfsfsfs";

            default:
                return "fsfsss";
        }
    }


    private Map<String, Object> createDataPart(Notification notification) {
        return FirebaseXmppMessageCodec.createDataPart(
                notification.getId(),
                notification.getTitle(),
                notification.getDescription(),
                notification.getEntityId(),
                notification.getCreatedDateTime(),
                notification.getAlertType(),
                null,
                null,
                notification.getPriority());
        
    }

    private Map<String, Object> createNotificationPart(Notification notification) {
        return FirebaseXmppMessageCodec.createDataPart(
                notification.getId(),
                notification.getTitle(),
                notification.getDescription(),
                notification.getEntityId(),
                notification.getCreatedDateTime(),
                notification.getAlertType(),
                null,
                null,
                notification.getPriority());

    }


}






