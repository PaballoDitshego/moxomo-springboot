package za.co.moxomo.crawlers;

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
import za.co.moxomo.services.VacancySearchService;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@ConditionalOnProperty(prefix = "crawler.toggle", name = "careers24", havingValue="true")

public class Careers24 {

    private static final Logger logger = LoggerFactory.getLogger(Careers24.class
            .getCanonicalName());
    private VacancySearchService vacancySearchService;
    private static final String FOURTEEN_MIN = "PT14M";


    @Autowired
    public Careers24(final VacancySearchService vacancySearchService) {
        this.vacancySearchService = vacancySearchService;
    }

    @Scheduled(fixedDelay = 900000, initialDelay = 0)
    @SchedulerLock(name = "careers24", lockAtMostForString = FOURTEEN_MIN, lockAtLeastForString = FOURTEEN_MIN)
    public void crawl() {
        logger.info("Career24 crawl started at {} ", LocalDateTime.now());
        long startTime = System.currentTimeMillis();

        final HashSet<String> crawledUrls = new HashSet<>();
        final ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();


        for (int i = 1; i <= 2; i++) {
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
                logger.debug("Crawling Careers24 {}", url);
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
                        if (url.contains("/jobs/adverts/")) {
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

        logger.info("Careers24 crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);
    }

    private static Vacancy createVacancy(String url, Document doc) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(doc);
        Vacancy vacancy = null;
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
            String additionalTokens = null;
            Date advertDate;
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");

            jobTitle = doc.select("meta[property=og:title]").first()
                    .attr("content").trim();
            logger.debug("jobtitle {}", jobTitle);
            imageUrl = doc.select("meta[property=og:image]").first()
                    .attr("content").concat(".jpeg").trim();
            logger.debug("imageUrl {}", imageUrl);
            description = doc.select("meta[property=og:description]").first()
                    .attr("content").trim();
          //  logger.debug("description {}", description);
            String postedBy = doc.select("span.posted").first().text();
            company = StringUtils.substringBetween(postedBy, "Posted by ", "on").trim();
            logger.debug("Company {}", company);
            date = StringUtils.substringAfter(postedBy, " on ").trim();

            Instant instant = sdf.parse(date).toInstant().plus(Duration.ofHours(LocalDateTime.now().getHour()))
                    .plus(Duration.ofMinutes(LocalDateTime.now().getMinute()));

            advertDate = Date.from(instant);

            logger.debug("advertDate {}", advertDate);
            String jobDetails = doc.getElementsByClass("job-details-h1").first().text();
            location = jobDetails.substring(jobDetails.lastIndexOf(",") + 1).trim();
            logger.debug("location {}", location);
            if (Objects.nonNull(doc.getElementById("btnApply"))) {
                offerId = doc.getElementById("btnApply").attr("data-vacancy-id").trim();
            } else {
                offerId = doc.getElementById("hdVacancyId").attr("value").trim();
            }
            logger.debug("offerid {}", offerId);

            vacancy = new Vacancy(jobTitle, description, offerId, company, location,
                    location, qualifications, responsibilities, advertDate,
                    contractType, (Objects.nonNull(imageUrl) && !imageUrl.isEmpty()) ? imageUrl : "https://dash.mediaupdate.co.za/story/image/110396/110396.jpg", remuneration, "PNET", additionalTokens, affirmativeAction, url);

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

        }
        return vacancy;
    }


}
