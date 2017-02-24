package co.moxomo.crawlers;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
;

import co.moxomo.model.Vacancy;
import co.moxomo.utils.Categoriser;
import co.moxomo.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Paballo
 * 
 *         Crawls Pnet.co.za CSS selectors are used to retrieve values
 * 
 */

public class PNet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static HashSet<String> crawledUrls = new HashSet<String>();
	private static HashSet<String> savedJobs = new HashSet<String>();

	 private static String PNET = "http://www.pnet.co.za/jobs/all-jobs.html";
	//private static String PNET = "http://www.pnet.co.za/5/jobs-list.html";
	private static ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();

	private static final Logger logger = LoggerFactory.getLogger(PNet.class
			.getCanonicalName());

	//private static DatastoreMutationPool ds = DatastoreMutationPool.create();

	public static void crawl() {

		PNet c = new PNet();
		c.crawl(PNET);
	}

	public void crawl(final String startUrl) {

		// Add the start URL to the list of URLs to crawl


		urlsToCrawl.add(startUrl);
		//urlsToCrawl.add("http://www.pnet.co.za/5/employer-search.html");
		//urlsToCrawl.add("http://www.pnet.co.za/jobs/all-jobs.html");

		// Search until the number of found URLs reaches 2000
		while (urlsToCrawl.iterator().hasNext() && crawledUrls.size() < 20000) {

			// Get the URL
			String url = urlsToCrawl.iterator().next();
			logger.info("url to  crawl {}", url);

			// Remove the URL from the list of URLs to crawl
			urlsToCrawl.remove(url);

			if (url != null) {

				Document doc = null;
				try {

					/*
					 * Connection.Response response = Jsoup .connect(url)
					 * .userAgent(
					 * "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19"
					 * ) .timeout(60000).execute();
					 */
					Connection.Response response = Jsoup
							.connect(url)
							.userAgent(
									"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
							.timeout(60000).execute();

					if (response.statusCode() == 500) {
						continue;
					}
					doc = response.parse();

				} catch (IOException e) {

					logger.error("Error {} encountered while crawling {}", e.getMessage(),url);
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
						if (_link.toLowerCase().contains("jobs-in--")
								|| _link.toLowerCase().contains("jobs")
								&& !_link.toLowerCase().contains("/m/")) {

							if (!urlsToCrawl.contains(_link)) {

								urlsToCrawl.add(_link);
							}

						}

						if (url.contains("jobs--") && url.contains("inline")) {

							String category = null;

							if (doc.getElementById("ApplyNowLink") != null) {
								if (doc.getElementById("topCategoryLink") != null) {
									category = doc
											.getElementById("topCategoryLink")
											.text().trim();
								}

							}

							String offerId = null;
							if (doc.getElementById("ApplyNowLink") != null) {
								if (doc.getElementById("ApplyNowLink").hasAttr(
										"href")) {
									offerId = doc
											.getElementById("ApplyNowLink")
											.attr("href");
									if (offerId != null) {
										offerId = StringUtils.substringBetween(
												offerId, "offerid=", "&")
												.trim();
									}
								}
							}

							if (offerId != null && category != null) {

								Vacancy vacancy = new Vacancy();
								vacancy.setCategory(category);
								vacancy.setAd_id(offerId);

								// mobile page to be scraped
								String address = "http://www.pnet.co.za/m/?event=OfferView&id="
										+ offerId;
								vacancy.setWebsite(address);
								Vacancy vac = createVacancy(vacancy);
								if (vac != null && vac.getAdvertDate() != null) {
									// Entity entity = EntityCreator
									// / .returnEntity(vac);
									savedJobs.add(vac.getWebsite());
									// ds.put(entity);
									Util.save(vac);

								}

							}
						}

					}
				}
				crawledUrls.add(url);

			}

		}
		// ds.flush();

	}

	private Vacancy createVacancy(Vacancy vac) {
		logger.info("creating vavancy");

		if (savedJobs.contains(vac.getWebsite())) {
			return null;

		}
		// Extract data from pnet.co.za

		try {

			Connection.Response response = Jsoup
					.connect(vac.getWebsite())
					.userAgent(
							"Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
					.timeout(60000).execute();

			if (response.statusCode() == 500) {
				savedJobs.add(vac.getWebsite());
				logger.error("Error  crawling {}", vac.getWebsite());
				return null;
			}

			Document doc = response.parse();
			if (doc.hasClass("info-window error-page-not-found")) {
				savedJobs.add(vac.getWebsite());
				return null;
			}

			String logo = null;
			if (doc != null) {

				if (!doc.getElementsByTag("img").isEmpty()) {
					logo = doc.getElementsByTag("img").first().attr("abs:src")
							.trim();
				}
				if (logo == null) {
					logo = "http://www.pnet.co.za/5/resources/images/pnet-logo.gif";
				}

				String company = null;

				String category = vac.getCategory();
				Elements elements = doc.getElementsByTag("p");
				String location = null;
				String date = null;
				for (Element element : elements) {
					if (element
							.className()
							.equals("col col-xs-6 col-offset-reset listing-footer-location")) {
						location = element.text();
					}
					if (element
							.className()
							.equals("col col-xs-6 col-offset-reset text-right listing-footer-date")) {
						date = element.text();
					}
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				Date temp = null;
				if (date == null) {
					logger.error("Error  crawling {}", vac.getWebsite());


					return null;
				}

				try {
					temp = sdf.parse(date);
				} catch (ParseException e) {
					logger.error("Error  crawling {}", vac.getWebsite());
					logger.error(e.getMessage());

				}
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -1);

				if (temp == null || temp.before(c.getTime())
						|| Util.isAvailable(vac.getWebsite())) {
					return null;

				}
				SimpleDateFormat formatter = new SimpleDateFormat(
						"MMM dd, yyyy hh:mm:ss a");
				formatter.setTimeZone(TimeZone.getTimeZone("GMT-2"));
				Date today = null;
				try {
					today = formatter.parse(Calendar.getInstance().getTime().toString());
				} catch (ParseException e) {
					logger.error("Error  crawling {}", vac.getWebsite());
					logger.error(e.getMessage());
				}

				Calendar m = Calendar.getInstance();
				vac.setAdvertDate(today);
				m.add(Calendar.DATE, 33);
				vac.setClosingDate(m.getTime());

				String position = null;
				Elements listings = doc.getAllElements();
				String description = null;

				if (listings.hasClass("listing-title")) {
					position = doc.getElementsByClass("listing-title").text()
							.trim();
				}

				if (listings.hasClass("listing-company")) {
					company = doc.getElementsByClass("listing-company").first()
							.text();
				}

				if (listings.hasClass("job_introduction")) {

					description = doc.getElementsByClass("job_introduction")
							.first().text();

				}

				String spec = null;
				if (listings.hasClass("job_description")) {
					spec = doc.getElementsByClass("job_description").first()
							.text();

					if (description == null) {
						if (spec.length() > 200) {
							description = spec.substring(0, 200);
						} else {
							description = spec;
						}
					}
				}

				String qual = null;
				if (listings.hasClass("job_profile")) {
					qual = doc.getElementsByClass("job_profile").text().trim();
					if (description == null) {
						if (spec.length() > 200) {
							description = qual.substring(0, 200);
						} else {
							description = qual;
						}

					}
				}

				vac.setImageUrl(logo);
				vac.setSource("Pnet");

				vac.setJob_title(position);

				vac.setDescription(description);

				vac.setCompany_name(company);

				vac.setProvince(location);
				vac.setLocation(location);

				vac.setMin_qual(qual);

				// add ad_id to loaded to ensure that entity is not captured
				// again
				// Entity entity = new Entity("Loaded");
				// entity.setProperty("ad_id", vac.getAd_id());

				// set category
				if (!category.equals(null) || category.trim().length() > 1) {
					category = category.trim();
					if (StringUtils.containsIgnoreCase(position, "learnership")) {
						category = "Learnerships/Bursaries";
					}
					if (StringUtils.contains(position, "internship")
							|| ((StringUtils.containsIgnoreCase(position,
									"intern") && !(StringUtils
									.containsIgnoreCase(position, "internal")
									|| !StringUtils.containsIgnoreCase(
											position, "International") || !StringUtils
										.containsIgnoreCase(position,
												"international"))))) {
						category = "Internships";
					}
					if (StringUtils.containsIgnoreCase(position, "customer")) {
						category = "Customer Service";
					}

					category = Categoriser.getCategory(category);
					category = category.trim();
				}

				vac.setCategory(category);
				vac.setDuties(spec);

				if (position == null || company == null
						|| vac.getAdvertDate() == null
						|| vac.getDescription() == null) {

					return null;

				}

			}
		} catch (IOException e) {
			logger.error("Error  crawling {}", vac.getWebsite());
		}

		return vac;
	}
}
