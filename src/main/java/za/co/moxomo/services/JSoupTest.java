package za.co.moxomo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import za.co.moxomo.crawlers.model.mrprice.MrPriceResponse;
import za.co.moxomo.crawlers.model.pnet.AdditionalInfo;
import za.co.moxomo.model.Vacancy;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JSoupTest {

    private static final Logger logger = LoggerFactory.getLogger(JSoupTest.class);
    private static final String ENDPOINT = "https://mrpcareers.azurewebsites.net/csod.json";
    private  static final String MOBILE_URL="https://yourjourney.csod.com/m/ats/careersite/index.html?site=4&c=yourjourney&lang=en-US&#jobRequisitions/";


    public static void main(String[] args) throws Exception {

        logger.info("Pnet crawl started at {} ", LocalDateTime.now());
        long startTime = System.currentTimeMillis();

        final HashSet<String> crawledUrls = new HashSet<>();
        final HashSet<String> capturedOffers = new HashSet<>();
        final ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();


        for (int i = 1; i <=15; i++) {
            String url = (i == 1) ? "https://www.careers24.com/jobs/m-true/?sort=dateposted&pagesize=100" : "https://www.careers24.com/jobs/m-true/?sort=dateposted&pagesize=100".concat("&page=").concat(String.valueOf(i));
            urlsToCrawl.add(url);

        }
        while (urlsToCrawl.iterator().hasNext()) {
            String url = urlsToCrawl.iterator().next();
            urlsToCrawl.remove();
            if (crawledUrls.contains(url)) {
                continue;
            }
            crawledUrls.add(url);
            if (Objects.nonNull(url)) {
                logger.info("Crawling Careers24 {}", url);
                try {
                    Connection.Response response = Jsoup
                            .connect(url)
                            .ignoreHttpErrors(true)
                            .userAgent(
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                            .timeout(60000).execute();

                    Document doc = response.parse();
                    if (Objects.nonNull(doc)) {
                        Elements links = doc.select("a");
                        for (Element link : links) {

                            String _link = link.selectFirst("a").absUrl("href");
                            if (_link.length() < 1) {
                                continue;
                            }
                            int index = _link.indexOf('#');
                            if (index != -1) {
                                _link = _link.substring(0, index);

                            }
                            if (urlsToCrawl.contains(_link) || crawledUrls.contains(_link)) {
                                continue;
                            }
                            // urls that contain add info
                            if (_link.toLowerCase().contains("/jobs/adverts/") && !url.contains("/jobs/adverts")) {
                                urlsToCrawl.add(_link);
                            }
                        }
                        if (url.contains("/jobs/adverts/")){
                            //index document
                            Vacancy vacancy = createVacancy(url, doc);

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("Error {} encountered while crawling {}", e.getMessage(), url);
                    continue;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("Pnet crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);
    }

    private  static Vacancy createVacancy(String url, Document doc) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(doc);
        Vacancy vacancy;
        logger.info("Creating");
        try {
            String jobTitle=null;
            String description=null;
            String company = null;
            String location = null;
            String date;
            String remuneration = null;
            String contractType = null;
            String affirmativeAction = null;
            String responsibilities = null;
            String offerId = null;
            String qualifications = null;
            String imageUrl = null;
            String additionalTokens = null;
            Date advertDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");

            jobTitle = doc.select("meta[property=og:title]").first()
                    .attr("content").trim();
            logger.info("jobtitle {}", jobTitle);
            imageUrl = doc.select("meta[property=og:image]").first()
                    .attr("content").concat(".jpeg").trim();
            logger.info("imageUrl {}", imageUrl);
            description= doc.select("meta[property=og:description]").first()
                    .attr("content").trim();
            logger.info("description {}", description);
            String postedBy  = doc.select("span.posted").first().text();
            company = StringUtils.substringBetween(postedBy, "Posted by ", "on").trim();
            logger.info("Company {}", company);
            date =  StringUtils.substringAfter(postedBy, " on ").trim();

            Instant instant = sdf.parse(date).toInstant().plus(Duration.ofHours(LocalDateTime.now().getHour()))
                    .plus(Duration.ofMinutes(LocalDateTime.now().getMinute()));

            advertDate = Date.from(instant);

            logger.info("advertDate {}", advertDate);
            String jobDetails = doc.getElementsByClass("job-details-h1").first().text();
            location = jobDetails.substring(jobDetails.lastIndexOf(",")+1).trim();
            logger.info("location {}", location);
            offerId =  doc.getElementById("btnApply")  .attr("data-vacancy-id").trim();
            logger.info("offerid {}", offerId);


            vacancy = new Vacancy(jobTitle, description, offerId, company, location,
                    location, qualifications, responsibilities, advertDate,
                    contractType, (Objects.nonNull(imageUrl) && !imageUrl.isEmpty()) ? imageUrl : "https://dash.mediaupdate.co.za/story/image/110396/110396.jpg", remuneration, "PNET", additionalTokens, affirmativeAction, url);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return vacancy;
    }

}

