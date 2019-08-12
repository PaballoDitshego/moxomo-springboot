package  za.co.moxomo.config.xmpp;

import lombok.extern.slf4j.Slf4j;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
@Slf4j

public class InboundFcmMessageHandler {


    @Autowired
    private MessageChannel fcmXmppOutboundChannel;




    private static final String ORIGINAL_MESSAGE_ID = "original_message_id";

    @ServiceActivator(inputChannel = "fcmInboundChannel")
    public void handleUpstreamMessage(FirebaseUpstreamMessage message) throws Exception {
        String message_type = message.getMessageType();
        if (message_type == null) {
            handleOrdinaryMessage(message);
        } else {
            switch (message_type) {
                case "ack":
                    handleAcknowledgementReceipt(message);
                    break;
                case "nack":
                    handleNotAcknowledged(message);
                    break;
                case "receipt":
                    handleDeliveryReceipts(message);
                    break;
                case "control":
                    handleControl(message);
                    break;
                default:
                    break;
            }

        }
    }

    private void handleAcknowledgementReceipt(FirebaseUpstreamMessage input) {
        String messageId = input.getMessageUid();
        String data = String.valueOf(input.getData());
        log.info("Gcm acknowledges receipt of message {}, with payload {}", messageId, data);

    }

    private void handleOrdinaryMessage(FirebaseUpstreamMessage input) {
        log.info("Ordinary message received");
        String messageId = input.getMessageUid();
        String from = input.getFrom();

        String action = String.valueOf(input.getData().get("action"));
   /*     if (action != null) {
            switch (action) {
                case "REGISTER":
                    String phoneNumber = (String) input.getData().get("phoneNumber");
                    registerUser(from, phoneNumber);
                    break;
                case "UPDATE_READ":
                    String notificationId = (String) input.getData().get("notificationId");
                    updateReadStatus(notificationId);
                    break;
                default: //action unknown ignore
                    break;

            }
        }*/
        sendAcknowledment(from, messageId);
    }

    private void handleNotAcknowledged(FirebaseUpstreamMessage input) {
        String messageId = input.getMessageUid();
      /*  Notification notification = notificationService.loadNotification(messageId);
        if (notification != null) {
            log.info("Push Notification delivery failed, now sending SMS to  {}", notification.getTarget().getPhoneNumber());

            messageSendingService.sendMessage(UserMessagingPreference.SMS.name(), notification);
        } else {
            log.info("Received an upstream message without notification, must be chat message");
        }*/
    }

    private void handleDeliveryReceipts(FirebaseUpstreamMessage input) {
        String messageId = String.valueOf(input.getData().get(ORIGINAL_MESSAGE_ID));
        log.info("Message " + messageId + " delivery successful, updating notification to delivered status.");
       // notificationService.markNotificationAsDelivered(messageId);
    }

    private void handleControl(FirebaseUpstreamMessage input) {
        log.info("FCM Connection draining");

    }



    private void sendAcknowledment(String registrationId, String messageId) {
        org.springframework.messaging.Message<Message> gcmMessage = FirebaseXmppMessageCodec.encode(registrationId, messageId, "ack");
        log.info("Acknowledging message with id ={}", messageId);
        fcmXmppOutboundChannel.send(gcmMessage);
    }

   

  
}