package za.co.moxomo.crawlers;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import za.co.moxomo.crawlers.model.mrprice.MrPriceResponse;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.services.SearchService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class MrPrice {

    private static final Logger logger = LoggerFactory.getLogger(MrPrice.class);
    private static final String ENDPOINT = "https://mrpcareers.azurewebsites.net/csod.json";
    private SearchService searchService;
    private RestTemplate restTemplate;

    @Autowired
    public MrPrice(SearchService searchService, RestTemplate restTemplate) {
        this.searchService =searchService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 14400000)
    public void crawl() {
        ResponseEntity<List<MrPriceResponse>> responseEntity = restTemplate.exchange(ENDPOINT,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<MrPriceResponse>>() {
                });
        logger.info("MrPriceResponse {}", responseEntity.getBody().toString());
        List<MrPriceResponse> response = responseEntity.getBody();
        logger.info("response size {}", response.size());
        response.stream()
                .forEach(this::createVacancy);
    }

    private void createVacancy(MrPriceResponse mrPriceResponse) {
        String position = mrPriceResponse.getPosition();
        logger.info("position {}", position);
        String location = mrPriceResponse.getLocation();
        logger.info("location {}", location);
        String description = Jsoup.parse(mrPriceResponse.getInternalDescription()).text();
        logger.info("description {}", description);
        String qualification = Jsoup.parse(mrPriceResponse.getMinimumQualification()).text();
        logger.info("qual {}", qualification);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Date advertDate = Date.valueOf( LocalDate.parse(mrPriceResponse.getDefaultEffectiveDate(), formatter));
        logger.info("advertDate {}", advertDate);
        String url = mrPriceResponse.getDefaultURL();
        String offerId = mrPriceResponse.getId().toString();

        String imageUrl = Jsoup.parse(mrPriceResponse.getExternalAd()).select("img").first().attr("src");
        logger.info("imageUrl {}",imageUrl);
        String additionalTokens = mrPriceResponse.getTitle();

        Vacancy vacancy = new Vacancy();
        vacancy.setOfferId(offerId);
        vacancy.setId(UUID.randomUUID().toString());
        vacancy.setUrl(url);
        vacancy.setDescription(description);
        vacancy.setQualifications(qualification);
        vacancy.setAdvertDate(advertDate);
        vacancy.setAdditionalTokens(additionalTokens);
        vacancy.setJobTitle(position);
        vacancy.setLocation(location);
        vacancy.setCompany("Mr Price");
        vacancy.setImageUrl(imageUrl);
        vacancy.setRemuneration(mrPriceResponse.getCompensation());

        if(!searchService.isExists(vacancy)){
            searchService.index(vacancy);
        }
    }

}
