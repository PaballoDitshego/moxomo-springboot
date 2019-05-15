package za.co.moxomo.config.xmpp;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.xmpp.inbound.ChatMessageListeningEndpoint;
import org.springframework.integration.xmpp.support.DefaultXmppHeaderMapper;
import org.springframework.integration.xmpp.support.XmppHeaderMapper;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;


public class FirebaseMessageListeningEndpoint extends ChatMessageListeningEndpoint {

    private static final Logger log = LoggerFactory.getLogger(FirebaseMessageListeningEndpoint.class);
    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";

    private StanzaListener stanzaListener = new GcmPacketListener();
    private XmppHeaderMapper headerMapper = new DefaultXmppHeaderMapper();
    private ObjectMapper mapper = new ObjectMapper();

    public FirebaseMessageListeningEndpoint(XMPPConnection connection) {
        super(connection);
        ProviderManager.addExtensionProvider(GCM_ELEMENT_NAME, GCM_NAMESPACE,
                new ExtensionElementProvider<ExtensionElement>() {
                    @Override
                    public ExtensionElement parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
                        StandardExtensionElement.Builder builder = StandardExtensionElement.builder(GCM_ELEMENT_NAME,GCM_NAMESPACE);
                        String json = parser.nextText();
                        return builder.setText(json).build();
                    }
                });

    }

    public String getComponentType() {
        return "xmpp:inbound-channel-adapter-gcm";
    }

    @Override
    public void setHeaderMapper(XmppHeaderMapper headerMapper) {
        super.setHeaderMapper(headerMapper);
        this.headerMapper = headerMapper;
    }

    @Override
    protected void doStart() {
        this.xmppConnection.addAsyncStanzaListener(this.stanzaListener, null);
    }

    @Override
    protected void doStop() {
        if (this.xmppConnection != null) {
            this.xmppConnection.removeAsyncStanzaListener(this.stanzaListener);
        }
    }

    private class GcmPacketListener implements StanzaListener {
        @Override
        public void processStanza(Stanza packet) {
            log.info("Packet received from gcm " + packet.toString());
            if (packet instanceof org.jivesoftware.smack.packet.Message) {
                org.jivesoftware.smack.packet.Message xmppMessage = (org.jivesoftware.smack.packet.Message) packet;

                final StandardExtensionElement gcmExtension = (StandardExtensionElement)xmppMessage.getExtension(GCM_NAMESPACE);
                try {
                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    final FirebaseUpstreamMessage entity = mapper.readValue(gcmExtension.getText(), FirebaseUpstreamMessage.class);
                    log.info("Relaying chat message : {}", entity.toString());
                    Map<String, Object> mappedHeaders = headerMapper.toHeadersFromRequest(xmppMessage);
                    sendMessage(MessageBuilder.withPayload(entity).copyHeaders(mappedHeaders).build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                log.warn("Unsupported Packet " + packet.toString());
            }

        }

    }
}