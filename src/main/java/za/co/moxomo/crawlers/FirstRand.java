package za.co.moxomo.crawlers;

import org.apache.commons.lang3.StringUtils;
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
import za.co.moxomo.services.JSoupTest;
import za.co.moxomo.services.SearchService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

//@Component
public class FirstRand {

    private static final Logger logger = LoggerFactory.getLogger(JSoupTest.class);
    private static final String RMB = "https://www.firstrandjobs.mobi/Jobs/List";
    private static final String RMB_DETAIL_URL = "https://www.firstrandjobs.mobi/Jobs/Detail?refNumber=";

    private final SearchService searchService;

    @Autowired
    public FirstRand(SearchService searchService) {
        this.searchService = searchService;
    }

    @Scheduled(fixedRate = 14400000)
    public void crawl() {

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("includeLayoutPage", "false");
        dataMap.put("Country", "");
        dataMap.put("province", "");
        dataMap.put("city", "");
        dataMap.put("pageSize", "1000");
        dataMap.put("startIndex", "1");
        dataMap.put("getPartial", "true");
        dataMap.put("professionalArea", "");
        dataMap.put("dateSearch", "all");

        try {
            Connection.Response response
                    = Jsoup.connect(RMB)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
                    .timeout(60000).ignoreHttpErrors(true)
                    .data(dataMap)
                    .method(Connection.Method.POST)
                    .execute();

            Document document = response.parse();
            List<String> referenceNumbers = document.getElementsByClass("refNum").stream().map(e -> e.text()).collect(Collectors.toList());
            List<String> locations = document.getElementsByClass("location").stream().map(e -> e.text()).collect(Collectors.toList());
            List<String> endDates = document.getElementsByClass("endDate").stream().map(e -> e.text()).collect(Collectors.toList());

            List<Vacancy> vacancies = new ArrayList<>();
            for (int i = 0; i < referenceNumbers.size(); i++) {
                if (Objects.nonNull(endDates.get(i))) {
                    LocalDate localDate = LocalDate.parse(endDates.get(i).substring(5), DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
                    if (localDate.isAfter(LocalDate.now())) {
                        Vacancy vacancy = new Vacancy();
                        vacancy.setId(UUID.randomUUID().toString());
                        vacancy.setOfferId(referenceNumbers.get(i));
                        vacancy.setLocation(locations.get(i));
                        vacancy.setAdvertDate(new Date());
                        vacancy.setClosingDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.of("Africa/Johannesburg")).toInstant()));
                        vacancies.add(vacancy);
                    }
                }

            }
            vacancies.stream().forEach(this::getJobDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getJobDetail(Vacancy vacancy) {
        Objects.requireNonNull(vacancy.getOfferId());

        if (!searchService.isExists(vacancy)) {
            String url = RMB_DETAIL_URL.concat(vacancy.getOfferId());
            logger.info("url {}", url);
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
                        String jobHeader = doc.getElementsByClass("JobHeaders").first().text();
                        vacancy.setAdditionalTokens(jobHeader);
                        Element element = doc.getElementById("requirements");
                        logger.info("Requirements {}", element.text());
                        String description = StringUtils.substringBetween(element.text(), "purpose", "experience and qualifications");
                        vacancy.setDescription(description);
                        logger.info("description {}", description);
                        String qualifications = StringUtils.substring(element.text(), element.text().lastIndexOf("experience and qualifications"), element.text().length()).trim();
                        vacancy.setQualifications(qualifications);
                        qualifications = StringUtils.remove(qualifications, "experience and qualifications");
                        logger.info("qualifications {}", qualifications);
                        String title = doc.select("meta[name=twitter:title]").first()
                                .attr("content").trim();
                        vacancy.setJobTitle(title);
                        String imageUrl = doc.select("meta[name=twitter:image]").first()
                                .attr("content").trim();
                        vacancy.setImageUrl(imageUrl);
                        vacancy.setUrl(url);

                        logger.info("title {}", title);
                        logger.info("imageUrl {}", imageUrl);
                        Element additionalElem = doc.getElementsByClass("detail-block").last();
                        StringBuilder builder = new StringBuilder();

                        for (Element element1 : additionalElem.getElementsByTag("li")) {
                            builder.append(element1.text().trim()).append(System.getProperty("line.separator"));
                        }
                        String responsibilities = builder.toString().trim();
                        vacancy.setCompany("FirstRand");
                        vacancy.setResponsibilities(responsibilities);

                        logger.info("responsibilities {}", builder.toString());

                        searchService.index(vacancy);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
