package za.co.moxomo.crawlers;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import za.co.moxomo.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @author Paballo
 */
public class CareerJunction {
    //crawls careerjunction.co.za

    private static HashSet<String> crawledUrls = new HashSet<String>();


    private static String CAREER = "http://www.careerjunction.co.za";

    private static ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<String>();

    public static void crawl() {
        CareerJunction c = new CareerJunction();
        c.crawl(CAREER);
    }


    public void crawl(final String startUrl) {

        // Add the start URL to the list of URLs to crawl
        urlsToCrawl.add(startUrl);

        // Search until the number of found URLs reaches 2000
        while (!urlsToCrawl.isEmpty() && crawledUrls.size() < 2000) {

            // Get the URL
            String url = urlsToCrawl.iterator().next();

            // Remove the URL from the list of URLs to crawl
            urlsToCrawl.remove(url);

            // Verify and convert the URL string to the URL object


            if (url != null) {

                // Download the page at the URL
                // String pageContent = fetchPageContent(verifiedUrl);
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).timeout(0).get();
                } catch (Exception e) {
                }

                if (doc != null) {

                    Elements links = doc.select("a[href]");

                    for (Element link : links) {
                        String _link = link.attr("abs:href");
                        if (_link.length() < 1) {
                            continue;
                        }

                        int index = _link.indexOf('#');
                        if (index != -1) {
                            _link = _link.substring(0, index);
                        }

                        if (crawledUrls.contains(_link)) {
                            continue;
                        }
                        if (urlsToCrawl.contains(_link)) {
                            continue;
                        }
                        if (_link.toLowerCase().contains("/my/account/")) {
                            continue;
                            // urlsToCrawl.
                        }
                        if (_link.toLowerCase().contains("-jobs")
                                || _link.toLowerCase().contains("/jobs/")) {

                            urlsToCrawl.add(_link);
                            // urlsToCrawl.
                        }

                    }

                    if (url.contains("/jobs/")) {
                        if (url.contains("?searchkey")) {
                            String ref = StringUtils.substringBetween(url, "-",
                                    "?searchkey").trim();
                            String mobi_url = "http://m.careerjunction.co.za/advert/view.htm?ref=" + ref;
                            if (!Util.isAvailable(mobi_url)) {

                            }
                        }

                    }

                    crawledUrls.add(url);

                }
            }
        }

    }

}
