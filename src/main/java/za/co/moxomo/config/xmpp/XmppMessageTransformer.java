package za.co.moxomo.config.xmpp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

@Component
public class XmppMessageTransformer {

    private static final Logger log = LoggerFactory.getLogger(XmppMessageTransformer.class);

    private static final String TOPICS = "/topics/";


    @Transformer(inputChannel = "gcmOutboundChannel", outputChannel = "gcmXmppOutboundChannel")
    public Message<org.jivesoftware.smack.packet.Message> transform(Message message) throws Exception {

        /*Message<org.jivesoftware.smack.packet.Message> gcmMessage;
        if(message.getPayload() instanceof Notification){
            Notification notification = (Notification) message.getPayload();
            gcmMessage = constructGcmMessage(notification);
            log.debug("Message with id {}, transformed to {}", notification.getUid(),
                    gcmMessage != null && gcmMessage.getPayload() != null ? gcmMessage.getPayload().toXML().toString() : "null");
        }else{
            gcmMessage = GcmXmppMessageCodec.encode(TOPICS.concat("keepalive"), UIDGenerator.generateId(), null);
        }
        return gcmMessage;*/
        return null;
    }



}