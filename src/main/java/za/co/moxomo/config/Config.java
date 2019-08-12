package za.co.moxomo.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import org.apache.catalina.connector.Connector;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.web.client.RestTemplate;
import za.co.moxomo.enums.PercolatorIndexFields;
import za.co.moxomo.exception.MoxomoResponseErrorHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import static org.apache.http.conn.params.ConnManagerParams.DEFAULT_MAX_TOTAL_CONNECTIONS;
import static org.apache.http.conn.params.ConnPerRouteBean.DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"za.co.moxomo.repository.elasticsearch"})
@EnableMongoRepositories(basePackages = {"za.co.moxomo.repository.mongodb"})
@IntegrationComponentScan
@EnableIntegration
//@EnableSwagger2
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private static final String VACANCIES = "vacancies";
    public static final String PERCOLATOR_INDEX = "percolator_index";
    public static final String PERCOLATOR_INDEX_MAPPING_TYPE = "_doc";


    @Value("${mongodb.host}")
    private String mongoHost;

    @Value("${mongodb.port}")
    private int mongoPort;

    @Value("${mongodb.username}")
    private String mongoUser;

    @Value("${mongodb.password}")
    private String mongoPassword;



    @Value("${elasticsearch.host}")
    private String SEARCH_HOST;

    @Value("${elasticsearch.port}")
    private Integer SEARCH_PORT;


    @Value("${elasticsearch.username}")
    private String SEARCH_USERNAME;

    @Value("${elasticsearch.password}")
    private String SEARCH_PASSWORD;


    @Value("${elasticsearch.clusterid}")
    private String SEARCH_CLUSTERID;


    @PostConstruct
    public void initializePercolatorIndex() {
        try {
            Client client = elasticsearchClient();

            IndicesExistsResponse indicesExistsResponse = client.admin().indices().prepareExists(PERCOLATOR_INDEX).get();

            if (indicesExistsResponse == null || !indicesExistsResponse.isExists()) {
                XContentBuilder percolatorQueriesMapping = XContentFactory.jsonBuilder()
                        .startObject()
                        .startObject("properties");
                Arrays.stream(PercolatorIndexFields.values())
                        .forEach(field -> {
                            try {
                                percolatorQueriesMapping
                                        .startObject(field.getFieldName())
                                        .field("type", field.getFieldType())
                                        .endObject();
                            } catch (IOException e) {
                                logger.error(String.format("Error while adding field %s to mapping", field.name()), e);
                                throw new RuntimeException(
                                        String.format("Something went wrong while adding field %s to mapping", field.name()), e);
                            }
                        });

                percolatorQueriesMapping
                        .endObject()
                        .endObject();

                client.admin().indices().prepareCreate(PERCOLATOR_INDEX)
                        .addMapping(PERCOLATOR_INDEX_MAPPING_TYPE, percolatorQueriesMapping)
                        .execute()
                        .actionGet();
            }
        } catch (Exception e) {
            logger.error("Error while creating percolator index", e);
            throw new RuntimeException("Something went wrong during the creation of the percolator index", e);
        }
    }


    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainer() {
        return server -> {
            if (server instanceof TomcatServletWebServerFactory) {
                server.addAdditionalTomcatConnectors(redirectConnector());
            }
        };
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("AJP/1.3");
        connector.setScheme("http");
        connector.setPort(9090);
        connector.setSecure(false);
        connector.setAllowTrace(false);
        return connector;
    }

    @Bean
    public MongoClient mongoClient() {
        String credentials = mongoUser.concat(":").concat(mongoPassword);
        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://".concat(credentials).concat("@").concat(mongoHost));
        MongoClient mongoClient = new MongoClient(uri);
        return mongoClient;
    }


    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), VACANCIES);
    }

    @Bean
    public Client elasticsearchClient() throws Exception {

        String credentials = SEARCH_USERNAME + ":" + SEARCH_PASSWORD;
        TransportClient client = new PreBuiltXPackTransportClient(Settings.builder()
                .put("cluster.name", SEARCH_CLUSTERID)
                .put("xpack.security.transport.ssl.verification_mode", "full")
                .put("xpack.security.transport.ssl.enabled", true)
                //.put("client.transport.nodes_sampler_interval", "5s")
                .put("client.transport.sniff", false)
                .put("transport.tcp.compress", true)
                .put("request.headers.X-Found-Cluster", SEARCH_CLUSTERID)
                .put("xpack.security.user", credentials)
                .build());
        client.addTransportAddress(new TransportAddress(InetAddress.getByName(SEARCH_HOST), SEARCH_PORT));

        return client;
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(elasticsearchClient());
    }

    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(500000).setSocketTimeout(500000).build();

        return HttpClientBuilder.create().setConnectionManager(connectionManager).setDefaultRequestConfig(config)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        restTemplate.setErrorHandler(new MoxomoResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient());
        return factory;
    }

    @Bean
    public TaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Moxomo-");
        TaskExecutor taskExecutor = new ConcurrentTaskExecutor(executor);
        executor.initialize();
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        return threadPoolTaskScheduler;
    }


    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(50));
        return pollerMetadata;
    }


  /*  @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    @Bean
    public LockProvider lockProvider(MongoClient mongo) {
        return new MongoLockProvider(mongo, "vacancies");
    }


  /*  @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build();
    }*/

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


}