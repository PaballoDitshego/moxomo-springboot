package za.co.moxomo.crawlers;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class JobVine implements Serializable {

		private static final long serialVersionUID = 1L;
	private static HashSet<String> crawledUrls = new HashSet<String>();
	private static HashSet<String> savedJobs = new HashSet<String>();
	private static String JOBVINE = "http://www.jobvine.co.za";
	private static ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<String>();
	private static final Logger logger = Logger.getLogger(JobVine.class
			.getCanonicalName());



	public static void crawl() {

		JobVine c = new JobVine();
		c.crawl(JOBVINE);
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
									"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
							.followRedirects(true).timeout(30000).get();
					
				} catch (IOException e) {
					
					logger.log(Level.SEVERE, url + " " + e.getMessage());
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
						// urls that contain add info
						if (_link.toLowerCase().contains("jobs")
								|| _link.toLowerCase().contains("job-detail")) {

							if (!urlsToCrawl.contains(_link)
									&& !crawledUrls.contains(_link)) {

								urlsToCrawl.add(_link);

							}
						}

						if (url.contains("job-detail")) {

							String offerId = StringUtils
									.substringBetween(
											doc.getElementsByTag("title")
													.text(), "(", ")");
							String details = doc.getElementsByClass("blue")
									.get(0).text();
							String date = StringUtils.substringBetween(details,
									"Date Added:", "Applications").trim();
							SimpleDateFormat sdf = new SimpleDateFormat(
									"dd MMMM yyyy");
							Date temp = null;
							try {
								temp = sdf.parse(date);
							} catch (ParseException e) {
							}

							Calendar c = Calendar.getInstance();
							c.add(Calendar.DATE, -2);
							if (!temp.before(c.getTime())) {
								Vacancy vacancy = createVacancy(url);
								if (vacancy != null) {

								//	Util.save(vacancy);

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
		String url_ = url.substring(0, url.lastIndexOf("/"));
		String id = url_.substring(url_.lastIndexOf("/") + 1, url_.length())
				.trim();
		String website = "http://m.jobvine.co.za/#/job/" + id;

		/*if (savedJobs.contains(website) || Util.isAvailable(website)) {
			
			return null;
		}
*/
		try {

			Document doc = Jsoup
					.connect(url)
					.userAgent(// NB take note of userAgent
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
					.followRedirects(true).timeout(30000).get();

			String logo = null;
			String company = null;
			if (!doc.getElementsByTag("img").isEmpty()) {
				logo = doc.getElementsByTag("img").get(3).attr("abs:src")
						.trim();
				if (!logo.contains("Logo")) {
					logo = "http://www.jobvine.co.za/Content/images/logo_jobvine_social.png";
				}

				company = doc.getElementsByTag("img").get(3).attr("alt").trim();
			}
			String description = doc.getElementsByClass("detail-description")
					.text().trim();

			String category = doc.getElementById("railway")
					.getElementsByTag("a").get(1).text().trim();
			category = Categoriser.getCategory(category);

			String details = doc.getElementsByClass("blue").get(0).text();
			String title = StringUtils.substringBefore(details, "Salary")
					.trim();
			title = StringUtils.substringBefore(title, "-").trim();
			String location = StringUtils.substringBetween(details,
					"Location:", "Salary").trim();

			if (company.isEmpty()) {
				company = StringUtils.substringBetween(details, "Recruiter:",
						"Location").trim();
			}
			// SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

			SimpleDateFormat formatter = new SimpleDateFormat(
					"MMM dd, yyyy hh:mm:ss a");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT-2"));
			Date today = null;
			try {
				today = formatter.parse(Calendar.getInstance().getTime()
						.toLocaleString());
			} catch (ParseException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}

			Calendar c = Calendar.getInstance();
			// c.add(Calendar.DATE, -2);
		//	vacancy = new Vacancy();
		/*	vacancy.setAdvertDate(today);
			c.add(Calendar.DATE, 33);
			vacancy.setClosingDate(c.getTime());

			vacancy.setImageUrl(logo);
			vacancy.setDescription(description);
			vacancy.setLocation(location);
			vacancy.setCompany_name(company);
			vacancy.setWebsite(website);
			vacancy.setAd_id(id);
			vacancy.setCategory(category);
			vacancy.setProvince(location);
			vacancy.setJob_title(title);*/

			/*if (title == null && category == null
					&& vacancy.getAdvertDate() == null) {

				return null;
			}*/

		} catch (IOException e) {
			
			logger.log(Level.SEVERE, url);

		}
		savedJobs.add(website);
		return vacancy;

	}

}