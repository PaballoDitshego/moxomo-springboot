package za.co.moxomo.config.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.xmpp.config.XmppConnectionFactoryBean;
import org.springframework.integration.xmpp.outbound.ChatMessageSendingMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.net.ssl.SSLSocketFactory;

@Configuration

public class XmppConfig {


    @Value("${firebase.cloud.messaging.host}")
    private String host;

    @Value("${firebase.cloud.messaging.port}")
    private int port;

    @Value("${firebase.cloud.messaging.sender}")
    private String gcmSenderId;

    @Value("${firebase.cloud.messaging.secret}")
    private String gcmSenderKey;
    private Logger log = LoggerFactory.getLogger(XmppConfig.class);


    private  XMPPTCPConnectionConfiguration connectionConfiguration() throws XmppStringprepException {
        DomainBareJid serviceName = JidCreate.domainBareFrom(host);
        return XMPPTCPConnectionConfiguration
                .builder()
                .setCompressionEnabled(true)
                .setXmppDomain(serviceName)
                .setHost(host)
                .setPort(port)
                .setUsernameAndPassword(gcmSenderId, gcmSenderKey)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
                .build();
    }

    @Bean(name = "fcmConnection")
    public  XmppConnectionFactoryBean xmppConnectionFactoryBean() throws XmppStringprepException {
        XmppConnectionFactoryBean connectionFactoryBean = new XmppConnectionFactoryBean();
        connectionFactoryBean.setConnectionConfiguration(connectionConfiguration());
        connectionFactoryBean.setAutoStartup(true);
        Roster.setRosterLoadedAtLoginDefault(false);
        log.info("XMPP connection successfully started up");
        return connectionFactoryBean;
    }

    @Bean
    public FirebaseMessageListeningEndpoint inboundAdapter(XMPPConnection connection, MessageChannel fcmInboundChannel) {
        FirebaseMessageListeningEndpoint endpoint = new FirebaseMessageListeningEndpoint(connection);
        endpoint.setOutputChannel(fcmInboundChannel);
        endpoint.setAutoStartup(true);
        return endpoint;
    }

    @Bean
    @ServiceActivator(inputChannel = "fcmXmppOutboundChannel")
    public ChatMessageSendingMessageHandler chatMessageSendingMessageHandler(XMPPConnection connection){
        ChatMessageSendingMessageHandler adapter = new ChatMessageSendingMessageHandler(connection);
        return adapter;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(50));
        return pollerMetadata;
    }

    @Bean
    public MessageChannel fcmOutboundChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageChannel fcmXmppOutboundChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageChannel requestChannel(){return new QueueChannel();}

    @Bean
    public MessageChannel fcmInboundChannel(){return new DirectChannel();}
}


