package co.moxomo.crawlers;

import co.moxomo.model.Vacancy;
import co.moxomo.services.VacancyPersistenceService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@Component
public class PNet{

	private static HashSet<String> crawledUrls = new HashSet<String>();
	private static HashSet<String> savedJobs = new HashSet<String>();
	private static String PNET = "http://www.pnet.co.za/jobs/all-jobs.html";
	private static ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();
	private static final Logger logger = LoggerFactory.getLogger(PNet.class
			.getCanonicalName());

	private VacancyPersistenceService vacancyPersistenceService;

	@Autowired
	public  PNet(VacancyPersistenceService vacancyPersistenceService){
		this.vacancyPersistenceService = vacancyPersistenceService;
	}

	public void crawl() {
		crawl(PNET);
	}

	private void crawl(final String startUrl) {

		// Add the start URL to the list of URLs to crawl

		urlsToCrawl.add(startUrl);
		//urlsToCrawl.add("http://www.pnet.co.za/5/employer-search.html");
		//urlsToCrawl.add("http://www.pnet.co.za/jobs/all-jobs.html");

		while (urlsToCrawl.iterator().hasNext() && crawledUrls.size() < 20000) {
			String url = urlsToCrawl.iterator().next();
			urlsToCrawl.remove(url);

			if (url != null) {

				Document doc = null;
				try {
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
									savedJobs.add(vac.getWebsite());
									vacancyPersistenceService.persistVacancy(vac);
								}
							}
						}
					}
				}
				crawledUrls.add(url);
			}
		}
	}

	private Vacancy createVacancy(Vacancy vac) {
		if (savedJobs.contains(vac.getWebsite())) {
			return null;
		}
		try {
			Connection.Response response = Jsoup
					.connect(vac.getWebsite())
					.userAgent(
							"Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) " +
									"AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30")
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

				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
				Date temp = null;
				if (date == null) {
					logger.error("Error  crawling {}", vac.getWebsite());
					return null;
				}

				try {
					temp = sdf.parse(date);
				} catch (ParseException e) {
					sdf = new SimpleDateFormat("dd.MM.yyyy");
					try {
						temp = sdf.parse(date);
					} catch (ParseException e1) {
						logger.error(e1.getMessage());
					}

				}
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -1);

				if (temp == null || temp.before(c.getTime())
						|| Util.isAvailable(vac.getWebsite())) {
					return null;

				}
				//SimpleDateFormat formatter = new SimpleDateFormat(
					//	"MMM dd, yyyy hh:mm:ss a");
				SimpleDateFormat formatter = new SimpleDateFormat(
						"EEE MMM d HH:mm:ss zzz yyyy");
				formatter.setTimeZone(TimeZone.getTimeZone("GMT-2"));
				Date today = null;
				try {
					logger.info("today "+ Calendar.getInstance().getTime().toString());
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
				//todo use constructor initialisation
				// setting instead of setters
				vac.setImageUrl(logo);
				vac.setSource("Pnet");
				vac.setJob_title(position);
				vac.setDescription(description);
				vac.setCompany_name(company);
				vac.setProvince(location);
				vac.setLocation(location);

				vac.setMin_qual(qual);

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
