package za.co.moxomo.config.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import za.co.moxomo.domain.Notification;

@MessageEndpoint
public class OutboundSmsHandler {

    private static final Logger log = LoggerFactory.getLogger(OutboundSmsHandler.class);

 /*   @Autowired
    private SmsSendingService smsSendingService;

    @Autowired
    private NotificationService notificationService;*/

    @ServiceActivator(inputChannel = "smsOutboundChannel")
    public void handleMessage(Message<Notification> message) throws Exception {
        log.info("SMS outbound channel received message={}", message.getPayload());
    /*    Notification notification = message.getPayload();
        String destination = notification.getTarget().getPhoneNumber();
        String msg = notification.getMessage();
        log.info("Sms outbound channel sending forwarding message to = {}", destination);
        SmsGatewayResponse response = smsSendingService.sendSMS(msg, destination);
        if (response.isSuccessful()) {
            notificationService.markNotificationAsDelivered(notification.getUid());
            notificationService.updateNotificationReadStatus(notification.getUid(), true);
        } else {
            log.error("error delivering SMS, response from gateway: {}", response.toString());
        }*/
    }   }