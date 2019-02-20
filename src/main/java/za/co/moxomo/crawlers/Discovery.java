package za.co.moxomo.crawlers;

import jdk.nashorn.internal.ir.IdentNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import za.co.moxomo.crawlers.model.discovery.DiscoveryResponse;
import za.co.moxomo.crawlers.model.mrprice.MrPriceResponse;
import za.co.moxomo.model.Vacancy;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class Discovery {

    private static final Logger logger = LoggerFactory.getLogger(Discovery.class);
    private static final String ENDPOINT = "https://www.discovery.co.za/portal/individual/discovery-career-search/search.do";


    public void crawl() {

        logger.info("Crawling MrPrice started at {}", LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<DiscoveryResponse>> responseEntity = restTemplate.exchange(ENDPOINT,
                HttpMethod.POST, null, new ParameterizedTypeReference<List<DiscoveryResponse>>() {
                });
        logger.debug("MrPriceResponse {}", responseEntity.getBody().toString());
        List<DiscoveryResponse> response = responseEntity.getBody();
        for (DiscoveryResponse discoveryResponse : response) {
            try {
                createVacancy(discoveryResponse);
            } catch (Exception e) {
                e.printStackTrace();
                continue;

            }
        }

        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("Mr Price crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);


    }

    private void createVacancy(DiscoveryResponse discoveryResponse) {


        String position = discoveryResponse.getPositionDescription();
        logger.debug("position {}", position);
        String location = (discoveryResponse.getLocation().equalsIgnoreCase("unknown") ? discoveryResponse.getProvince() : discoveryResponse.getLocation());
        logger.debug("location {}", location);
        java.util.Date advertDate = Date.from(Instant.now());
        logger.debug("advertDate {}", advertDate);
        String offerId = discoveryResponse.getReferenceNumber();
        String imageUrl = "https://www.discovery.co.za/gallery/discoverycoza/corporate/logos/opengraph-discovery-logo.png";


        Vacancy vacancy = new Vacancy();
        vacancy.setOfferId(offerId);
        vacancy.setId(UUID.randomUUID().toString());
        vacancy.setUrl(discoveryResponse.getApplicationUrl());
        //  vacancy.setDescription(description);
        vacancy.setAdvertDate(advertDate);
        vacancy.setJobTitle(position);
        vacancy.setLocation(location);
        vacancy.setCompany("Discovery");
        vacancy.setImageUrl(imageUrl);


    }


}
