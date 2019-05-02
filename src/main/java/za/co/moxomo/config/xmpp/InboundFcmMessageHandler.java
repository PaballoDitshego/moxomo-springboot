package  za.co.moxomo.config.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import za.co.moxomo.config.xmpp.FirebaseUpstreamMessage;

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
                    break;
                default:
                    break;
            }

        }
    }

    private void handleAcknowledgementReceipt(FirebaseUpstreamMessage input) {
        String messageId = input.getMessageUid();
        String data = String.valueOf(input.getData());
        log.debug("Gcm acknowledges receipt of message {}, with payload {}", messageId, data);

    }

    private void handleOrdinaryMessage(FirebaseUpstreamMessage input) {
        log.debug("Ordinary message received");
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
        log.debug("Message " + messageId + " delivery successful, updating notification to delivered status.");
       // notificationService.markNotificationAsDelivered(messageId);
    }


    private void sendAcknowledment(String registrationId, String messageId) {
        org.springframework.messaging.Message<Message> gcmMessage = FirebaseXmppMessageCodec.encode(registrationId, messageId, "ack");
        log.debug("Acknowledging message with id ={}", messageId);
        fcmXmppOutboundChannel.send(gcmMessage);
    }

   

  
}