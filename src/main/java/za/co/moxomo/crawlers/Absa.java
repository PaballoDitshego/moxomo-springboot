package za.co.moxomo.crawlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.crawlers.model.absa.response.AbsaResponse;
import za.co.moxomo.crawlers.model.absa.response.RequisitionList;
import za.co.moxomo.model.Vacancy;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.codec.*;
import org.apache.commons.codec.net.*;

//@Component
public class Absa {

    private static final Logger logger = LoggerFactory.getLogger(Absa.class);
    private  static final String jobUrl = "https://barclays.taleo.net/careersection/ejb/jobdetail.ftl?job=";


    @Scheduled(fixedRate = 14400000)
    public void crawl() {

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
            for(RequisitionList requisitionList:response.getRequisitionList()){
                String jobId = requisitionList.getJobId();
                String url = jobUrl.concat(jobId).concat("&tz=GMT%2B02%3A00");
                logger.debug("Url {}", url);
                String jobTitle = requisitionList.getColumn().get(0);
                logger.debug("job title  {}",jobTitle);
                String advertDate = requisitionList.getColumn().get(2);
                logger.debug("AdvertDate {}", advertDate);
                String location = requisitionList.getColumn().get(1).replaceAll("\\W\\W", "").trim();
                logger.debug("Location {}", location);

                Vacancy vacancy = new Vacancy();
                vacancy.setId(UUID.randomUUID().toString());
                vacancy.setJobTitle(jobTitle);
                vacancy.setCompany("Absa");
                vacancy.setLocation(location);
                vacancy.setUrl(url);
                createVacancy(vacancy);
                break;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createVacancy(Vacancy vacancy){
        Objects.requireNonNull(vacancy);
        logger.info("url {}", vacancy.getUrl());
        try{
            Connection.Response response = Jsoup
                    .connect(vacancy.getUrl())
                    .ignoreHttpErrors(true)
                    .userAgent(
                            "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev>(KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev>")
                    .timeout(60000).execute();

            Document doc = response.parse();

           // logger.info("Response {}", doc.getAllElements());
            Element element = doc.getElementById("initialHistory");
          //  logger.info("elem {}", element.attr("value"));
            URLCodec encoder = new URLCodec();

            Document document = Jsoup.parse(encoder.decode(element.attr("value")));
            logger.info("elem {}",   document.select("p").text());
         /*   Elements elements = document.getElementsByTag("p");
            for(Element element1:elements){
                logger.info("Paragraph {}", element1.text());
            }
*/


          /*  for(Element element:elements) {
                for (DataNode dataNode : element.dataNodes()) {
                    String data = StringUtils.substringBetween(dataNode.getWholeData(), "descRequisition")

                    logger.info("Response {}", dataNode.getWholeData().);
                }
             //   break;
            }*/

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
