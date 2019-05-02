package za.co.moxomo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import za.co.moxomo.domain.Alert;

@Service
@Slf4j
public class AlertSendingServiceImpl implements AlertSendingService {

    @Autowired
    private MessageChannel requestChannel;


    @Override
    public void sendAlert(Alert alert) {
        Message<Alert> message = createMessage(alert, alert.getRoute());
        log.info("Sending message {}", message.getPayload().toString() );
        requestChannel.send(message);

    }

    @Override
    public void sendAlert(String destination, Alert alert) {
        Message<Alert> message = createMessage(alert, destination);
        requestChannel.send(message);

    }

    private Message<Alert> createMessage(Alert alert, String route) {
        return MessageBuilder.withPayload(alert)
                .setHeader("route", route)
                .build();

    }
}