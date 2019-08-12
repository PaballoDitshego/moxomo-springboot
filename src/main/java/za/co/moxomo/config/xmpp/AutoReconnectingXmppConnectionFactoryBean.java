package za.co.moxomo.config.xmpp;


import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.util.XmppStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.StringUtils;

/**
 * This class configures an {@link XMPPTCPConnection} object.
 * This object is used for all scenarios to talk to a Smack server.
 *
 * @author Josh Long
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Florian Schmaus
 * @author Artem Bilan
 * @author Philipp Etschel
 * @author Gary Russell
 * @see XMPPTCPConnection
 * @since 2.0
 */
public class AutoReconnectingXmppConnectionFactoryBean extends AbstractFactoryBean<XMPPConnection> implements SmartLifecycle {

    private final Object lifecycleMonitor = new Object();

    private static final Logger logger = LoggerFactory.getLogger(AutoReconnectingXmppConnectionFactoryBean.class);

    private XMPPTCPConnectionConfiguration connectionConfiguration;

    private volatile String resource; // server will generate resource if not provided

    private volatile String user;

    private volatile String password;

    private volatile String serviceName;

    private volatile String host;

    private volatile int port = 5222;

    private volatile Roster.SubscriptionMode subscriptionMode = Roster.getDefaultSubscriptionMode();

    private volatile boolean autoStartup = true;

    private volatile int phase = Integer.MIN_VALUE;

    private volatile boolean running;


    public AutoReconnectingXmppConnectionFactoryBean() {
    }

    /**
     * @param connectionConfiguration the {@link XMPPTCPConnectionConfiguration} to use.
     * @since 4.2.5
     */
    public void setConnectionConfiguration(XMPPTCPConnectionConfiguration connectionConfiguration) {
        this.connectionConfiguration = connectionConfiguration;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the subscription processing mode, which dictates what action
     * Smack will take when subscription requests from other users are made.
     * The default subscription mode is {@link Roster.SubscriptionMode#accept_all}.
     * <p> To disable Roster subscription (e.g. for sub-protocol without its support such a GCM)
     * specify this option as {@code null}.
     *
     * @param subscriptionMode the {@link Roster.SubscriptionMode} to use.
     *                         Can be {@code null}.
     * @see Roster#setSubscriptionMode(Roster.SubscriptionMode)
     */
    public void setSubscriptionMode(Roster.SubscriptionMode subscriptionMode) {
        this.subscriptionMode = subscriptionMode;
    }

    @Override
    public Class<? extends XMPPConnection> getObjectType() {
        return XMPPConnection.class;
    }

    @Override
    protected XMPPConnection createInstance() throws Exception {
        XMPPTCPConnectionConfiguration connectionConfig = this.connectionConfiguration;
        if (connectionConfig == null) {
            XMPPTCPConnectionConfiguration.Builder builder =
                    XMPPTCPConnectionConfiguration.builder()
                            .setHost(this.host)
                            .setPort(this.port);

            if (StringUtils.hasText(this.resource)) {
                builder.setResource(this.resource);
            }

            if (StringUtils.hasText(this.serviceName)) {
                builder.setUsernameAndPassword(this.user, this.password)
                        .setXmppDomain(this.serviceName);
            } else {
                builder.setUsernameAndPassword(XmppStringUtils.parseLocalpart(this.user), this.password)
                        .setXmppDomain(this.user);
            }

            connectionConfig = builder.build();
        }
        return new XMPPTCPConnection(connectionConfig);
    }

    protected XMPPTCPConnection getConnection() {
        try {
            return (XMPPTCPConnection) getObject();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot obtain connection instance", e);
        }
    }

    @Override
    public void start() {
        synchronized (this.lifecycleMonitor) {
            if (this.running) {
                return;
            }
            final XMPPTCPConnection connection = getConnection();
            try {
                connection.connect();
                connection.addConnectionListener(new FCMConnectionListener());
                ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
                ReconnectionManager.getInstanceFor(connection).addReconnectionListener(new ReconnectionListener() {
                    @Override
                    public void reconnectingIn(int seconds) {
                        logger.info("Reconnecting in {} ...", seconds);
                    }

                    @Override
                    public void reconnectionFailed(Exception e) {
                        logger.info("Reconnection failed! Error: {}", e.getMessage());
                    }
                });

                Roster roster = Roster.getInstanceFor(connection);
                if (this.subscriptionMode != null) {
                    roster.setSubscriptionMode(this.subscriptionMode);
                } else {
                    roster.setRosterLoadedAtLogin(false);
                }
                connection.login();
                this.running = true;
            } catch (Exception e) {
                throw new BeanInitializationException("failed to connect to XMPP service for "
                        + connection.getXMPPServiceDomain(), e);
            }
        }
    }

    @Override
    public void stop() {
        synchronized (this.lifecycleMonitor) {
            if (this.isRunning()) {
                getConnection().disconnect();
                this.running = false;
            }
        }
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return this.phase;
    }

    @Override
    public boolean isAutoStartup() {
        return this.autoStartup;
    }


    private class FCMConnectionListener implements ConnectionListener {

        FCMConnectionListener() {
            super();
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            logger.info("FCM Connection closed on error", e);
        }

        @Override
        public void connectionClosed() {
            logger.info("FCM Connection closed, reconnecting");
            start();

        }

        @Override
        public void connected(XMPPConnection connection) {
            logger.info("FCM Connection connected");
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {
            logger.info("FCM Connection authenticated");
        }

    }

}
