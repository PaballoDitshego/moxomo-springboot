package za.co.moxomo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import za.co.moxomo.domain.Notification;

@Service
@Slf4j
public class NotificationSendingServiceImpl implements NotificationSendingService {

    @Autowired
    private MessageChannel requestChannel;



    @Override
    public void sendAlert(Notification notification) {
        Message<Notification> message = createMessage(notification, notification.getRoute());
        log.info("Sending message {}", message.getPayload().toString() );
        requestChannel.send(message);

    }

    @Override
    public void sendAlert(String destination, Notification notification) {
        Message<Notification> message = createMessage(notification, destination);
        requestChannel.send(message);

    }

    private Message<Notification> createMessage(Notification notification, String route) {
        return MessageBuilder.withPayload(notification)
                .setHeader("route", route)
                .build();

    }
}