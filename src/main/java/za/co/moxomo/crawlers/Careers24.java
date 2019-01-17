package za.co.moxomo.crawlers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

import za.co.moxomo.model.Vacancy;
import za.co.moxomo.utils.Categoriser;
import za.co.moxomo.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Paballo Ditshego
 */
public class Careers24 {

	private static HashSet<String> crawledUrls = new HashSet<String>();
	private static String CAREERS24 = "http://www.careers24.com/jobs/job-search-results/?sort=dateposted&pagesize=100";
	private static ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();
	private static HashSet<String> savedJobs = new HashSet<String>();

/*	public static void crawl() {

		Careers24 c = new Careers24();
		c.crawl(CAREERS24);
	}

	public void crawl(final String startUrl) {

		// Add the start URL to the list of URLs to crawl
		urlsToCrawl.add(startUrl);

		// Search until the number of found URLs reaches 4000
		while (urlsToCrawl.iterator().hasNext() && crawledUrls.size() < 15000) {

			// Get the URL
			String url = urlsToCrawl.iterator().next();

			// Remove the URL from the list of URLs to crawl
			urlsToCrawl.remove(url);

			if (url != null) {

				Document doc = null;
				try {
					doc = Jsoup
							.connect(url)
							.userAgent(// NB take note of userAgent
									"Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19")
							.followRedirects(true).timeout(0).get();
				} catch (IOException e) {
				}

				if (doc != null) {

					Elements links = doc.select("a");

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
						if (_link.toLowerCase().contains("search-results")) {
							_link = _link.concat("&sort=dateposted");
						}

						// urls that contain add info
						if (_link.toLowerCase().contains("search-results")
								|| _link.toLowerCase().contains("adverts")) {

							if (!urlsToCrawl.contains(_link)
									&& !crawledUrls.contains(_link)) {

								urlsToCrawl.add(_link);

							}
						}

						if (url.contains("adverts")) {
							String id = StringUtils.substringBetween(url,
									"adverts/", "-").trim();
							if (!savedJobs
									.contains("http://m.careers24.com/job-detail/?vacancyid="
											.concat(id))) {
								Vacancy vacancy = createVacancy("http://m.careers24.com/job-detail/?vacancyid="
										.concat(id));
								if (vacancy != null) {
									Util.save(vacancy);
									savedJobs.add("http://m.careers24.com/job-detail/?vacancyid="
											.concat(id));
								}

							}
						}
					}

				}
			}
			crawledUrls.add(url);

		}
	}

	private Vacancy createVacancy(String url) {

		Vacancy vacancy = null;

		Document document = null;
		try {
			document = Jsoup
					.connect(url)
					.userAgent(// NB take note of userAgent
							"Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19")
					.followRedirects(false).timeout(0).get();
		} catch (IOException e) {
		}
		
		String description = document
				.getElementsByClass("job_detail_description").text().trim();
		description = StringUtils.substringBefore(description, "Apply before");
		String details = document.getElementsByClass("job_summary").text()
				.trim();
		String location = document.getElementsByClass("more_related_jobs")
				.select("span[id=ContentPlaceHolder1_lblRelatedJobs]").text();

		String category = StringUtils.substringBetween(details,
				"position in the", "sector");
		if (category != null) {
			category = category.trim();
			if (category.contains(",")) {
				category = StringUtils.substringBefore(category, ",");
			}
		} else {
			return null;
		}

		String company = StringUtils.substringBetween(details, "Posted by",
				"on");
		if (company != null) {
			company = company.trim();
		} else {
			return null;
		}
		String title = document.getElementsByClass("vm_job_detail").text()
				.trim();
		String logo = null;
		Elements pic_elements = document.getElementsByTag("img");
		for (Element element : pic_elements) {
			if (element.attr("abs:src").contains("imageresource")) {
				logo = element.attr("abs:src");
			}
		}

		if (logo == null || !logo.contains("imageresource")) {
			logo = "http://m.careers24.com/images/logo.png";
		}
		

		String date = StringUtils.substringBetween(
				document.getElementsByClass("posted_by").text().trim(), "on",
				"Reference").trim();

		if (StringUtils.containsIgnoreCase(title, "learnership")) {
			category = "Learnerships/Bursaries";
		}
		if (StringUtils.contains(title, "internship")
				|| ((StringUtils.containsIgnoreCase(title, "intern") && !(StringUtils
						.containsIgnoreCase(title, "internal")
						|| !StringUtils.containsIgnoreCase(title,
								"International") || !StringUtils
							.containsIgnoreCase(title, "international"))))) {
			category = "Internships";
		}
		if (StringUtils.containsIgnoreCase(title, "customer")) {
			category = "Customer Service";
		}

		

	//	vacancy = new Vacancy();

		//vacancy.setAd_id(id);

		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
		Date temp = null;
		try {
			temp = sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMM dd, yyyy hh:mm:ss a");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT-2"));
		Date today = null;
		try {
			today = formatter.parse(Calendar.getInstance().getTime()
					.toLocaleString());
		} catch (ParseException e) {
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);

		if (temp.before(c.getTime()) || Util.isAvailable(url)) {
			savedJobs.add(url);
			return null;

		}
		*//*vacancy.setAdvertDate(today);
		c.add(Calendar.DATE, 33);
		vacancy.setClosingDate(c.getTime());
*//*
		return vacancy;
		}
		*/
	
	

}
