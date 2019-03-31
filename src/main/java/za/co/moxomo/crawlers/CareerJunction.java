package za.co.moxomo.crawlers;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.services.VacancySearchService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @author Paballo
 */
@Component
@ConditionalOnProperty(prefix = "crawler.toggle", name = "careerjunction", havingValue="true")
public class CareerJunction {

    private static String CAREER = "https://www.careerjunction.co.za/jobs/results?sort=newest";
    private static final Logger logger = LoggerFactory.getLogger(CareerJunction.class);
    private VacancySearchService vacancySearchService;

    @Autowired
    public CareerJunction(final VacancySearchService vacancySearchService) {
        this.vacancySearchService = vacancySearchService;
    }

    @Scheduled(fixedDelay = 900000, initialDelay = 1200000)
    public void crawl() {
        logger.info("CareerJunction crawl started at {} ", LocalDateTime.now());
        long startTime = System.currentTimeMillis();

        final HashSet<String> crawledUrls = new HashSet<>();
        final ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();
        for (int i = 1; i <= 10; i++) {
            String url = (i == 1) ? CAREER : CAREER.concat("&page=").concat(String.valueOf(i));
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
                logger.info("Crawling CareerJunction {}", url);
                try {
                    Connection.Response response = Jsoup
                            .connect(url)
                            .ignoreHttpErrors(true)
                            .userAgent(
                                    "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev>(KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev>")
                            .timeout(60000).execute();

                    Document doc = response.parse();
                    if (Objects.nonNull(doc)) {
                        Elements links = doc.select("a");
                        for (Element link : links) {
                            String _link = link.attr("abs:href");
                            if (_link.length() < 1) {
                                continue;
                            }
                            if (_link.contains("?unmask[]=email")){
                                _link = _link.replace("?unmask[]=email", "").trim();
                            }
                            int index = _link.indexOf('#');
                            if (index != -1) {
                                _link = _link.substring(0, index);
                            }
                            if (urlsToCrawl.contains(_link) || crawledUrls.contains(_link)) {
                                logger.debug("Contains crawled url {}", _link);
                                continue;
                            }
                            // urls that contain add info
                            if (_link.toLowerCase().contains("/jobs/view")) {
                                urlsToCrawl.add(_link);
                            }
                        }
                        if (url.contains("/jobs/view")) {
                            //index document
                            Vacancy vacancy = createVacancy(url, doc);
                            if (!vacancySearchService.isExists(vacancy)) {
                                vacancySearchService.index(vacancy);
                                logger.debug("Saved vacancy item with id {}", vacancy.getId());
                            }
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

        logger.info("CareerJunction crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);
    }

    private Vacancy createVacancy(String url, Document doc) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(doc);
        Vacancy vacancy;
        try {
            String jobTitle;
            String description;
            String company;
            String location;
            String date;
            String remuneration = null;
            String contractType = null;
            String affirmativeAction = null;
            String responsibilities = null;
            String offerId;
            String qualifications = null;
            String imageUrl;
            String additionalTokens;
            Date advertDate;
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);


            jobTitle = StringUtils.substringBefore(doc.select("meta[property=og:title]").first()
                    .attr("content").trim(), " at ");
            imageUrl = doc.select("meta[property=og:image]").first()
                    .attr("content").trim();
            location = doc.select("div.cardSummary").first().selectFirst("li:nth-child(2)").text();
            offerId = doc.select("div.cardSummary").get(1).selectFirst("li:nth-child(1").text();
            offerId = (offerId.contains("|")) ? StringUtils.substringBetween(offerId, "Job ", "|").trim()
                    : StringUtils.substringAfter(offerId, "Job").trim();

            date = StringUtils.substringBetween(doc.select("div.cardSummary").get(1).selectFirst("li:nth-child(2").text(), "Posted", "by").trim();

            advertDate = sdf.parse(date);
            company = StringUtils.substringAfter(doc.select("div.cardSummary").get(1).selectFirst("li:nth-child(2").text(), " by");
            description = StringUtils.substringBefore(doc.select("div.contentText").get(0).text().replace("About the Position", "").trim(), "Education:");
            additionalTokens = doc.select("div.contentText").get(0).text().replace("About the Position", "").trim();
            
            vacancy = new Vacancy(jobTitle, description, offerId, company, location,
                    location, qualifications, responsibilities, advertDate,
                    contractType, imageUrl, remuneration, "CAREERJUNCTION", additionalTokens, affirmativeAction, url);


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return vacancy;
    }

}
