package za.co.moxomo.crawlers;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import za.co.moxomo.crawlers.model.discovery.DiscoveryRequest;
import za.co.moxomo.crawlers.model.discovery.DiscoveryResponse;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.services.SearchService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
public class Discovery {

    private static final Logger logger = LoggerFactory.getLogger(Discovery.class);

    private static final String ENDPOINT = "https://www.discovery.co.za/portal/individual/discovery-career-search/search.do";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    private SearchService searchService;

    @Autowired
    public Discovery(final SearchService searchService) {
        this.searchService = searchService;
    }


    @Scheduled(fixedDelay=900000, initialDelay = 900000)
    public void crawl() {

        logger.info("Crawling Discovery started at {}", LocalDateTime.now());

        long startTime = System.currentTimeMillis();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("x-requested-with", "XMLHttpRequest");
        headers.add("Content-Type", "application/json");
        HttpEntity<DiscoveryRequest> request = new HttpEntity<DiscoveryRequest>(new DiscoveryRequest(), headers);
        RestTemplate restTemplate =  new RestTemplate();



        ResponseEntity<List<DiscoveryResponse>> responseEntity = restTemplate.exchange(ENDPOINT, HttpMethod.POST,
                request, new ParameterizedTypeReference<List<DiscoveryResponse>>() {
                });
        logger.debug("DiscoveryResponse {}", responseEntity.getBody().toString());
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

        logger.info("Discovery crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);


    }

    private void createVacancy(DiscoveryResponse discoveryResponse) throws IOException, DecoderException, ParseException {
        Objects.requireNonNull(discoveryResponse);

        String position = discoveryResponse.getPositionDescription();
        String location = (discoveryResponse.getLocation().equalsIgnoreCase("unknown") ? discoveryResponse.getProvince() : discoveryResponse.getLocation());
        String offerId = discoveryResponse.getReferenceNumber();
        String imageUrl = "https://www.discovery.co.za/gallery/discoverycoza/corporate/logos/opengraph-discovery-logo.png";

        Vacancy vacancy = new Vacancy();
        vacancy.setOfferId(offerId);
        vacancy.setUrl(discoveryResponse.getApplicationUrl());
        vacancy.setJobTitle(position);
        vacancy.setLocation(location);
        vacancy.setCompany("Discovery");
        vacancy.setImageUrl(imageUrl);
        setAdditionalData(vacancy);

        if(!searchService.isExists(vacancy)){
            searchService.index(vacancy);
        }


    }

    private  void setAdditionalData(Vacancy vacancy) throws IOException, DecoderException, ParseException {
        Objects.requireNonNull(vacancy);

        Connection.Response response = Jsoup
                .connect(vacancy.getUrl())
                .ignoreHttpErrors(true)
                .userAgent(
                        "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev>(KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev>")
                .timeout(60000).execute();

        Document doc = response.parse();
        String date = StringUtils.substringBetween( doc.getElementsContainingText("Posted").first().text(),
                "Posted", "-").trim();
        String description=StringUtils.substringBetween(doc.getElementsByClass("joqReqDescription").first().text(),
                "Key Purpose", "Areas of responsibility may include but not limited to").trim();

        Instant instant = sdf.parse(date).toInstant().plus(Duration.ofHours(LocalDateTime.now().getHour()))
                .plus(Duration.ofMinutes(LocalDateTime.now().getMinute()));

        java.util.Date advertDate = Date.from(instant);
        vacancy.setDescription(description);
        vacancy.setAdvertDate(advertDate);
        vacancy.setAdditionalTokens(doc.getElementsByClass("joqReqDescription").first().text());

    }

}
