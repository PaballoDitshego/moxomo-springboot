package za.co.moxomo.crawlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class MrPrice {

    private static final Logger logger = LoggerFactory.getLogger(MrPrice.class);
    private static final String ENDPOINT = "https://mrpcareers.azurewebsites.net/csod.json";
    private  static final String MOBILE_URL="https://yourjourney.csod.com/m/ats/careersite/index.html?site=4&c=yourjourney&lang=en-US&#jobRequisitions/";
    private SearchService searchService;
    private RestTemplate restTemplate;

    @Autowired
    public MrPrice(SearchService searchService, RestTemplate restTemplate) {
        this.searchService =searchService;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay=3600000, initialDelay = 0)
    public void crawl() {

        logger.info("Crawling MrPrice started at {}", LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        ResponseEntity<List<MrPriceResponse>> responseEntity = restTemplate.exchange(ENDPOINT,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<MrPriceResponse>>() {
                });
       logger.debug("MrPriceResponse {}", responseEntity.getBody().toString());
        List<MrPriceResponse> response = responseEntity.getBody();
        for(MrPriceResponse mrPriceResponse:response){
            try{
                createVacancy(mrPriceResponse);
            }
            catch (Exception e){
                e.printStackTrace();
                continue;

           }
        }

        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("Mr Price crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);


    }

    private void createVacancy(MrPriceResponse mrPriceResponse) {

        String position = mrPriceResponse.getPosition();
        logger.debug("position {}", position);
        String location = mrPriceResponse.getLocation();
        logger.debug("location {}", location);
        String description = Jsoup.parse(mrPriceResponse.getInternalDescription()).text();
        logger.debug("description {}", description);
        String qualification = Jsoup.parse(mrPriceResponse.getMinimumQualification()).text();
        logger.debug("qual {}", qualification);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Date advertDate = Date.valueOf( LocalDate.parse(mrPriceResponse.getDefaultEffectiveDate(), formatter));
        logger.debug("advertDate {}", advertDate);
        String offerId = mrPriceResponse.getId().toString();
        Elements elements =Jsoup.parse(mrPriceResponse.getExternalAd()).select("img");
        String imageUrl=null;
        for(Element element:elements){
            if (element.hasAttr("src")){
                imageUrl = element.attr("src");
            }
        }


        logger.debug("imageUrl {}",imageUrl);
        String additionalTokens = mrPriceResponse.getTitle();

        Vacancy vacancy = new Vacancy();
        vacancy.setOfferId(offerId);
        vacancy.setId(UUID.randomUUID().toString());
        vacancy.setUrl(MOBILE_URL.concat(offerId));
        vacancy.setDescription(description);
        vacancy.setQualifications(qualification);
        vacancy.setAdvertDate(advertDate);
        vacancy.setAdditionalTokens(additionalTokens);
        vacancy.setJobTitle(position);
        vacancy.setLocation(location);
        vacancy.setCompany("Mr Price");
        vacancy.setImageUrl((imageUrl!=null)?imageUrl:"https://www.mrp.com/media/vaimo/uploadlogo/default/logo-dark.png");
        vacancy.setRemuneration(mrPriceResponse.getCompensation());
        vacancy.setWebViewViewable(false);

        if(!searchService.isExists(vacancy)){
            searchService.index(vacancy);
        }
    }

}
