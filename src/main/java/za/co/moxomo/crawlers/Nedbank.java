package za.co.moxomo.crawlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.repository.elasticsearch.VacancySearchRepository;
import za.co.moxomo.services.VacancySearchService;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@ConditionalOnProperty(prefix = "crawler.toggle", name = "nedbank", havingValue="true")
public class Nedbank {

    private static final Logger logger = LoggerFactory.getLogger(Nedbank.class
            .getCanonicalName());
    private static final String FOURTEEN_MIN = "PT14M";
    private VacancySearchService vacancySearchService;
    private VacancySearchRepository vacancySearchRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public Nedbank(final VacancySearchService vacancySearchService, final VacancySearchRepository vacancySearchRepository) {
        this.vacancySearchService = vacancySearchService;
        this.vacancySearchRepository=vacancySearchRepository;
    }


    @Scheduled(fixedDelay = 900000, initialDelay =900000)
    @SchedulerLock(name = "nedbank", lockAtMostForString = FOURTEEN_MIN, lockAtLeastForString = FOURTEEN_MIN)
    public void crawl() {
        logger.debug("Nedbank crawl started at {} ", LocalDateTime.now());
        long startTime = System.currentTimeMillis();

        final HashSet<String> crawledUrls = new HashSet<>();
        final ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();
        
        
        urlsToCrawl.add("https://jobs.nedbank.co.za/search/?q=&sortColumn=referencedate&sortDirection=desc");
        for (int i = 0; i <= 100; i += 25) {
            String url = (i == 0) ? "https://jobs.nedbank.co.za/search/?q=&sortColumn=referencedate&sortDirection=desc" : "https://jobs.nedbank.co.za/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=".concat(String.valueOf(i));
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
                logger.debug("Crawling Nedbank {}", url);
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
                            //  String _link = link.selectFirst("a").absUrl("href");
                            String _link = link.attr("abs:href");
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
                            if (_link.toLowerCase().contains("/job/")

                            ) {
                                urlsToCrawl.add(_link);
                            }
                        }
                        if (url.contains("/job/")) {
                            //index document
                            Vacancy vacancy = createVacancy(url, doc);
                            logger.debug("Nedbank vacancy exist {}",vacancySearchService.isExists(vacancy));
                           if (!vacancySearchService.isExists(vacancy)) {
                                vacancySearchService.index(vacancy);
                                logger.debug("Saved vacancy item with id {}", vacancy.getId());
                           }
                        }

                    }
                } catch (Exception e) {
                   // e.printStackTrace();
                    logger.error("Error {} encountered while crawling {}", e.getMessage(), url);
                    continue;
                }
            }
        }
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("Nedbank crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);
    }

    private static Vacancy createVacancy(String url, Document doc) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(doc);
        Vacancy vacancy;
        try {
            String jobTitle;
            String description = null;
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            

            jobTitle = doc.title();

            logger.debug("job title {}", jobTitle);
            location = doc.select("meta[name=keywords]").first()
                    .attr("content").trim().replace(jobTitle, "").trim();
            logger.debug("location {}", location);
            description = doc.getElementsByClass("jobdescription").first().text().trim();
            description = StringUtils.substringBetween(description, "Job Purpose", ".").trim();
            logger.debug("description {}", description);
            imageUrl = "https://rmkcdn.successfactors.com/dd82a348/1311423d-203f-422e-b24b-e.gif";
            date = doc.getElementById("job-date").text().replace("Date:", "").trim();
            logger.debug("date {}", date);
            logger.debug("formatted date {}", sdf.parse(date));

            Instant instant = sdf.parse(date).toInstant().plus(Duration.ofHours(LocalDateTime.now().getHour()))
                    .plus(Duration.ofMinutes(LocalDateTime.now().getMinute()));
            company = "Nedbank Limited";

            advertDate = Date.from(instant);
            logger.debug("advertDate {}", advertDate);
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(url);
            while (m.find()) {
                offerId = m.group();
            }
            logger.debug("offerid {}", offerId);

            vacancy = new Vacancy(jobTitle, description, offerId, company, location,
                    location, qualifications, responsibilities, advertDate,
                    contractType, imageUrl, remuneration, "Nedbank", additionalTokens, affirmativeAction, url);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return vacancy;
    }
}