package za.co.moxomo.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import za.co.moxomo.domain.Notification;

@Component
public class OutboundMessageRouter {

    private Logger log = LoggerFactory.getLogger(OutboundMessageRouter.class);

    @Router(inputChannel="requestChannel")
    public String route(Message<Notification> message) {
        String route = (String) message.getHeaders().get("route");
        String outputChannel;

        if (route !=null) {
            switch (route) {
                case "sms":
                    log.info("routing to sms channel");
                    outputChannel = "smsOutboundChannel";
                    break;
                case "fcm": //
                    log.info("routing to fcm channel");
                    outputChannel = "fcmOutboundChannel";
                    break;
                default:
                    log.info("badly formed route={}, defaulting to sms channel", route);
                    outputChannel = "smsOutboundChannel";
                    break;
            }
        } else {
            log.info("Route not specified defaulting to sms");
            outputChannel = "smsOutboundChannel";
        }

        return outputChannel;
    }

}