package co.moxomo.config;

import com.mongodb.MongoClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by paballo on 2017/02/21.
 */

@Configuration
public class MoxomoConfig {

    private static final Logger logger = LoggerFactory.getLogger(MoxomoConfig.class);

    @Value("${mongodb.host}")
    private String DBHost;

    @Value("${mongodb.port}")
    private int DBPort;

    @Value("${elasticsearch.host}")
    private String SEARCH_HOST;

    @Value("${elasticsearch.port}")
    private String SEARCH_PORT;


    @Bean
    public MongoClient mongoClient() {
        return new MongoClient(DBHost, DBPort);
    }

    @Bean
    public Client elasticSearchClient() {
        Client client = null;
        try {

           client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(SEARCH_HOST), 9300));

        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
        return client;
    }
}