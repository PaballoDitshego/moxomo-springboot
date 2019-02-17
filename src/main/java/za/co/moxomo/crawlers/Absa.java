package za.co.moxomo.crawlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.crawlers.model.absa.response.AbsaResponse;
import za.co.moxomo.crawlers.model.absa.response.RequisitionList;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.services.SearchService;
import za.co.moxomo.utils.Util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Component
public class Absa {

    private static final Logger logger = LoggerFactory.getLogger(Absa.class);
    private static final String jobUrl = "https://barclays.taleo.net/careersection/ejb/jobdetail.ftl?job=";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    private SearchService searchService;

    @Autowired
    public Absa(SearchService searchService){
        this.searchService=searchService;
    }


    @Scheduled(fixedDelay=3600000, initialDelay = 600000)
    public void crawl() {

        logger.info("Crawling Absa started at {}", LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        try {
            Connection connection = Jsoup.connect("https://barclays.taleo.net/careersection/rest/jobboard/searchjobs?lang=en_gb&portal=150170116559")
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.3")
                    .timeout(60000).ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .header("Content-Type", "application/json")
                    .header("Content-Length", "751")
                    .header("Accept", "application/json")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("tz", "GMT+02:00")
                    .requestBody("{\"multilineEnabled\":false,\"sortingSelection\":{\"sortBySelectionParam\":\"3\",\"ascendingSortingOrder\":\"false\"},\"fieldData\":{\"fields\":{\"KEYWORD\":\"\",\"LOCATION\":\"110140251674\"},\"valid\":true},\"filterSelectionParam\":{\"searchFilterSelections\":[{\"id\":\"POSTING_DATE\",\"selectedValues\":[]},{\"id\":\"LOCATION\",\"selectedValues\":[]},{\"id\":\"JOB_FIELD\",\"selectedValues\":[]},{\"id\":\"JOB_SCHEDULE\",\"selectedValues\":[]},{\"id\":\"JOB_LEVEL\",\"selectedValues\":[]}]},\"advancedSearchFiltersSelectionParam\":{\"searchFilterSelections\":[{\"id\":\"ORGANIZATION\",\"selectedValues\":[]},{\"id\":\"LOCATION\",\"selectedValues\":[]},{\"id\":\"JOB_FIELD\",\"selectedValues\":[]},{\"id\":\"JOB_NUMBER\",\"selectedValues\":[]},{\"id\":\"URGENT_JOB\",\"selectedValues\":[]},{\"id\":\"JOB_SHIFT\",\"selectedValues\":[]}]},\"pageNo\":1}")
                    .referrer("https://barclays.taleo.net/careersection/ejb/jobsearch.ftl?lang=en_gb&location=110140251674")
                    .method(Connection.Method.POST);
            ObjectMapper mapper = new ObjectMapper();
            AbsaResponse response = mapper.readValue(connection.execute().body(), AbsaResponse.class);
            logger.info("Absa Response {}", response.toString());
            for (RequisitionList requisitionList : response.getRequisitionList()) {
                String jobId = requisitionList.getJobId();
                String url = jobUrl.concat(jobId).concat("&tz=GMT%2B02%3A00");
                logger.debug("Url {}", url);
                String jobTitle = requisitionList.getColumn().get(0);
                logger.debug("job title  {}", jobTitle);
                String date = requisitionList.getColumn().get(2);
                Date advertDate = sdf.parse(date);
                Instant instant = advertDate.toInstant().plus(Duration.ofHours(LocalDateTime.now().getHour())).plus(Duration.ofMinutes(LocalDateTime.now().getMinute()));

                logger.info("AdvertDate {}", Date.from(instant));
                String location = requisitionList.getColumn().get(1).replaceAll("\\W\\W", "").trim();
                logger.debug("Location {}", location);

                Vacancy vacancy = new Vacancy();
                vacancy.setId(UUID.randomUUID().toString());
                vacancy.setJobTitle(jobTitle);
                vacancy.setCompany("Absa Limited");
                vacancy.setLocation(location);
                vacancy.setUrl(url);
                vacancy.setAdvertDate( Date.from(instant));
                vacancy.setOfferId(jobId);
                vacancy.setImageUrl("https://cms.groupeditors.com/img/9ca23487-475a-48ee-8485-6134a1a344d8.jpg");
                try {
                    vacancy.setDescription(getDescription(url));
                } catch (DecoderException e) {
                    logger.error(e.getMessage());
                    continue;
                }
                Util.validate(vacancy);
                if (!searchService.isExists(vacancy)) {
                    searchService.index(vacancy);
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        logger.info("Absa crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);

    }

    private String getDescription(String url) throws IOException, DecoderException {
        Objects.requireNonNull(url);
        logger.debug("url {}", url);

        Connection.Response response = Jsoup
                .connect(url)
                .ignoreHttpErrors(true)
                .userAgent(
                        "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev>(KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev>")
                .timeout(60000).execute();

        Document doc = response.parse();

        Element element = doc.getElementById("initialHistory");
        URLCodec encoder = new URLCodec();

        Document document = Jsoup.parse(encoder.decode(element.attr("value")));

        String description = StringUtils.substringBetween(document.select("p")
                .text(), "Job Purpose", "Education");
        if (Objects.isNull(description)) {
            description = StringUtils.substringBetween(document.select("p")
                    .text(), "Overall Purpose of the Job", "Key Accountabilities");
        }
        if (Objects.isNull(description)) {
            int length = (document.select("p").text().length() >=600)?600:document.select("p").text().length();
            description = document.select("p").text().substring(0, length);
            description= description.substring(0, description.lastIndexOf(".")+1);
        }

        description = description.replaceAll("([^ a-zA-Z0-9,&:_])", "").replaceFirst(":", "").trim();


          //
      return description;
    }
}
