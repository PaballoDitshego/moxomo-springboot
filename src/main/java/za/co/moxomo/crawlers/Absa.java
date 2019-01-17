package za.co.moxomo.crawlers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Absa {

    public void crawl(){

              Connection connection= Jsoup.connect("https://barclays.taleo.net/careersection/rest/jobboard/searchjobs?lang=en_gb&portal=150170116559")
                .userAgent(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.3")
                .timeout(60000).ignoreHttpErrors(true)
                .ignoreContentType(true)
                .header("Content-Type", "application/json")
                .header("Content-Length", "751")
                .header("Accept", "application/json")
                .header("X-Requested-With","XMLHttpRequest")
                .header("tz","GMT+02:00")
                .requestBody("{\"multilineEnabled\":false,\"sortingSelection\":{\"sortBySelectionParam\":\"3\",\"ascendingSortingOrder\":\"false\"},\"fieldData\":{\"fields\":{\"KEYWORD\":\"\",\"LOCATION\":\"110140251674\"},\"valid\":true},\"filterSelectionParam\":{\"searchFilterSelections\":[{\"id\":\"POSTING_DATE\",\"selectedValues\":[]},{\"id\":\"LOCATION\",\"selectedValues\":[]},{\"id\":\"JOB_FIELD\",\"selectedValues\":[]},{\"id\":\"JOB_SCHEDULE\",\"selectedValues\":[]},{\"id\":\"JOB_LEVEL\",\"selectedValues\":[]}]},\"advancedSearchFiltersSelectionParam\":{\"searchFilterSelections\":[{\"id\":\"ORGANIZATION\",\"selectedValues\":[]},{\"id\":\"LOCATION\",\"selectedValues\":[]},{\"id\":\"JOB_FIELD\",\"selectedValues\":[]},{\"id\":\"JOB_NUMBER\",\"selectedValues\":[]},{\"id\":\"URGENT_JOB\",\"selectedValues\":[]},{\"id\":\"JOB_SHIFT\",\"selectedValues\":[]}]},\"pageNo\":1}")
                .referrer("https://barclays.taleo.net/careersection/ejb/jobsearch.ftl?lang=en_gb&location=110140251674")
                .method(Connection.Method.POST);

    }
}
