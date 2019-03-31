package za.co.moxomo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import za.co.moxomo.dto.Alert;

@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private MessageChannel requestChannel;


    @Override
    public void sendAlert(Alert alert) {

    }

    @Override
    public void sendAlert(String destination, Alert alert) {

    }

    private Message<Alert> createMessage(Alert alert, String route) {
        return MessageBuilder.withPayload(alert)
                .setHeader("route", route)
                .build();

    }
}