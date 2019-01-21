package za.co.moxomo.crawlers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.services.SearchService;
import za.co.moxomo.utils.Util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class PNet {

    private static final Logger logger = LoggerFactory.getLogger(PNet.class
            .getCanonicalName());
    private static final HashSet<String> crawledUrls = new HashSet<>();
    private static final HashSet<String> capturedOffers = new HashSet<>();
    private final String PNET = "http://www.pnet.co.za/jobs/all-jobs.html";
    private final ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();
    private SearchService searchService;

    @Autowired
    public PNet(final SearchService searchService) {
        this.searchService = searchService;
    }

    @Scheduled(fixedRate = 14400000)
    public void crawl() {
        crawl(PNET);
    }

    private void crawl(final String startUrl) {
        // Add the start URL to the list of URLs to crawl
        urlsToCrawl.add(startUrl);
        while (urlsToCrawl.iterator().hasNext() && crawledUrls.size() < 20000) {
            String url = urlsToCrawl.iterator().next();
            urlsToCrawl.remove();
            if (crawledUrls.contains(url)) {
                continue;
            }
            crawledUrls.add(url);
            if (Objects.nonNull(url)) {
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
                            logger.info("link {}", _link);
                            if (_link.length() < 1) {
                                continue;
                            }
                            int index = _link.indexOf('#');
                            if (index != -1) {
                                _link = _link.substring(0, index);
                            }
                            if (urlsToCrawl.contains(_link) || crawledUrls.contains(_link)) {
                                logger.info("Contains crawled url {}", _link);
                                continue;
                            }
                            // urls that contain add info
                            if (_link.toLowerCase().contains("jobs-in--")
                                    || _link.toLowerCase().contains("jobs")
                                    ) {
                                urlsToCrawl.add(_link);
                            }
                            if (url.contains("jobs--") && url.contains("inline")) {
                                //index document
                                Vacancy vacancy = createVacancy(url, doc);
                                if (!capturedOffers.contains(vacancy.getOfferId()) && Util.validate(vacancy)) {
                                    searchService.index(vacancy);
                                    capturedOffers.add(vacancy.getOfferId());
                                    logger.info("Saved vacancy item with id {}", vacancy.getId());
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error {} encountered while crawling {}", e.getMessage(), url);
                }
            }
        }
    }

    private Vacancy createVacancy(String url, Document doc) {
        Objects.requireNonNull(url);
        Objects.requireNonNull(doc);
        Vacancy vacancy;
        try {
                String jobTitle;
                String description;
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
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", Locale.ENGLISH);

                jobTitle = doc.getElementsByClass("listingTitle").text()
                        .trim();
                logger.info("position dd: {}", jobTitle);
                for (Element element : doc.getElementsByClass("listing__apply-now_bottom")) {
                    offerId = element.select("a").first().attr("data-offerid");
                    logger.info("offerid {}", offerId);
                }
                description = doc.getElementById("company-intro").text().trim();
                logger.info("desc {}", description);

                StringBuilder builder = new StringBuilder();
                if (Objects.nonNull(doc.getElementById("job-tasks"))) {
                    for (Element responsibility : doc.getElementById("job-tasks").getAllElements()) {
                        builder.append(responsibility.text().trim()).append(System.getProperty("line.separator"));
                    }
                    responsibilities = builder.toString();
                    builder.setLength(0);
                    logger.info("responsibilities {}", responsibilities);
                }

                if (Objects.nonNull(doc.getElementById("job-requim"))) {
                    for (Element qualification : doc.getElementById("job-requim").getAllElements()) {
                        builder.append(qualification.text().trim()).append(System.getProperty("line.separator"));
                    }
                    qualifications = builder.toString();
                    logger.info("qualification {}", qualifications);
                    builder.setLength(0);
                }
                Elements companyInfo = doc.getElementsByClass("js-company-content-card");
                for (Element el : companyInfo) {
                    if (el.hasAttr("data-logo")) {
                        imageUrl = el.attr("data-logo").trim();
                    }
                    if (el.hasAttr("data-name")) {
                        company = el.attr("data-name").trim();
                    }
                }
                Elements listings = doc.getElementsByClass("listing-list");
                for (Element element : listings) {
                    if (element.className().equals("listing-list at-listing__list-icons_location")) {
                        location = element.text();
                        continue;
                    }
                    if (element.className().equals("listing-list at-listing__list-icons_contract-type")) {
                        contractType = element.text();
                        continue;
                    }
                    if (element.className().equals("listing-list at-listing__list-icons_salary")) {
                        remuneration = element.text().trim();
                        continue;
                    }
                    if (element.className().equals("listing-list at-listing__list-icons_eeaa")) {
                        affirmativeAction = element.text().trim();
                        continue;
                    }
                    if (element.className().equals("listing-list at-listing__list-icons_date")) {
                        date = element.getAllElements().last().attr("data-date");
                        logger.info("date {}", date);
                        advertDate = sdf.parse(date);
                        continue;
                    }
                }
                if (Objects.nonNull(doc.getElementsByClass("tokens-list__item__link"))) {
                    for (Element element : doc.getElementsByClass("tokens-list__item__link")) {
                        builder.append(element.text().trim()).append(", ");
                    }
                    additionalTokens = builder.toString();
                }
                vacancy = new Vacancy(jobTitle, description, offerId, company, location,
                        location, qualifications, responsibilities, advertDate,
                        contractType, imageUrl, remuneration, "PNET", additionalTokens, affirmativeAction, url);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return vacancy;
    }


}
