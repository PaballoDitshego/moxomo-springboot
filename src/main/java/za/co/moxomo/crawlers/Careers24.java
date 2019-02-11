package za.co.moxomo.crawlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import za.co.moxomo.crawlers.model.mrprice.MrPriceResponse;
import za.co.moxomo.crawlers.model.pnet.AdditionalInfo;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.services.SearchService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Careers24 {

	private static final Logger logger = LoggerFactory.getLogger(Careers24.class
			.getCanonicalName());
	private SearchService searchService;

	@Autowired
	public Careers24(final SearchService searchService) {
		this.searchService = searchService;
	}

	@Scheduled(fixedDelay = 3600000, initialDelay = 0)
	public void crawl() {
		logger.info("Pnet crawl started at {} ", LocalDateTime.now());
		long startTime = System.currentTimeMillis();

		final HashSet<String> crawledUrls = new HashSet<>();
		final HashSet<String> capturedOffers = new HashSet<>();
		final ConcurrentLinkedQueue<String> urlsToCrawl = new ConcurrentLinkedQueue<>();


		for (int i = 1; i <10; i++) {
			String url = (i == 1) ? "https://www.careers24.com/jobs/m-true/?sort=dateposted&pagesize=100" : "https://www.careers24.com/jobs/m-true/?sort=dateposted&pagesize=100".concat("&page=").concat(String.valueOf(i));
			urlsToCrawl.add(url);

		}
		while (urlsToCrawl.iterator().hasNext()) {
			String url = urlsToCrawl.iterator().next();
			urlsToCrawl.remove();
			if (crawledUrls.contains(url)) {
				continue;
			}
			crawledUrls.add(url);
			if (Objects.nonNull(url)) {
				logger.info("Crawling Pnet {}", url);
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
							logger.info("absolute {}", _link);
							if (_link.length() < 1) {
								continue;
							}
							int index = _link.indexOf('#');
							if (index != -1) {
								_link = _link.substring(0, index);
							}
							if (urlsToCrawl.contains(_link) || crawledUrls.contains(_link)) {
								logger.debug("Contains crawled url {}", _link);
								continue;
							}
							// urls that contain add info
							if (_link.toLowerCase().contains("/jobs/adverts/")
							 && !_link.contains("?jobindex=")) {
								urlsToCrawl.add(_link);
							}
						}
						if (url.contains("/jobs/adverts/")){
							//index document
							Vacancy vacancy = createVacancy(url, doc);
							if (!capturedOffers.contains(url) && !searchService.isExists(vacancy)) {
								searchService.index(vacancy);
								capturedOffers.add(vacancy.getOfferId());
								logger.debug("Saved vacancy item with id {}", vacancy.getId());
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error {} encountered while crawling {}", e.getMessage(), url);
					continue;
				}
			}
		}
		long endTime = System.currentTimeMillis();
		long executeTime = endTime - startTime;

		logger.info("Pnet crawl ended at {} and took : {} ms ", LocalDateTime.now(), executeTime);
	}

	private Vacancy createVacancy(String url, Document doc) {
		Objects.requireNonNull(url);
		Objects.requireNonNull(doc);
		Vacancy vacancy;
		logger.info("Creating");
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			ObjectMapper mapper = new ObjectMapper();


			jobTitle = doc.getElementsByClass("listingTitle").text()
					.trim();

			logger.debug("position dd: {}", jobTitle);
			for (Element element : doc.getElementsByClass("listing__apply-now_bottom")) {
				offerId = element.select("a").first().attr("data-offerid");
				logger.debug("offerid {}", offerId);
			}
			if (Objects.nonNull(doc.getElementById("company-intro"))) {
				description = doc.getElementById("company-intro").text().trim();
			} else {
				description = jobTitle;
			}
			logger.debug("desc {}", description);
			StringBuilder builder = new StringBuilder();
			if (Objects.nonNull(doc.getElementById("job-tasks"))) {
				for (Element responsibility : doc.getElementById("job-tasks").getAllElements()) {
					builder.append(responsibility.text().trim()).append(System.getProperty("line.separator"));
				}
				responsibilities = builder.toString();
				builder.setLength(0);
				logger.debug("responsibilities {}", responsibilities);
			}

			if (Objects.nonNull(doc.getElementById("job-requim"))) {
				for (Element qualification : doc.getElementById("job-requim").getAllElements()) {
					builder.append(qualification.text().trim()).append(System.getProperty("line.separator"));
				}
				qualifications = builder.toString();
				logger.debug("qualification {}", qualifications);
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
			if (Objects.isNull(company)) {
				Elements elements = doc.getElementsByClass("at-listing-nav-company-name-link");
				if (Objects.nonNull(elements) && elements.size() > 0 && elements.first().hasAttr("title")) {
					company = elements.first().attr("title");
				}

			}
			if (Objects.isNull(imageUrl)) {
				Elements elements = doc.getElementsByClass("js-sticky-bar");
				if (Objects.nonNull(elements) && elements.first().hasAttr("data-settings")) {
					AdditionalInfo additionalInfo = mapper.readValue(elements.first().attr("data-settings"), AdditionalInfo.class);
					if (Objects.nonNull(additionalInfo.getLogoImageUrl()) && !additionalInfo.getLogoImageUrl().isEmpty()) {
						imageUrl = "https://www.pnet.co.za".concat(additionalInfo.getLogoImageUrl());
					} else {
						imageUrl = "http://media.stepstone.com/modules/tracking/resources/images/smartbanner_icon_pnet.png";
					}

					jobTitle = additionalInfo.getJobTitle();

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
					logger.debug("date {}", date);
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
			if (Objects.isNull(jobTitle) || jobTitle.isEmpty()) {
				logger.info("Else {}", url);
				jobTitle = doc.getElementsByClass("listing__job-title").first().text();
				company = doc.getElementsByClass("at-listing-nav-company-name-link").first().text();
				location = doc.getElementsByClass("listing-list at-listing__list-icons_location").first().text();
				date = doc.getElementsByClass("date-time-ago").first().attr("data-date");
				description = doc.getElementsByClass("richtext").first().text();
				advertDate = sdf.parse(date);
				offerId = doc.getElementsByAttribute("data-offerid ").first().attr("data-offerid");
				Elements elements = doc.select("a");

				for (Element e : elements) {
					if (e.hasAttr("style")) {
						String attr = e.attr("style");
						String image = (attr.substring(attr.indexOf("/"), attr.indexOf(")"))).replace("'", "").trim();
						imageUrl = (Objects.nonNull(image) && !image.isEmpty()) ? "https://www.pnet.co.za".concat(image) :
								"http://media.stepstone.com/modules/tracking/resources/images/smartbanner_icon_pnet.png";
						break;
					}
				}
				logger.info("imageUrl {]", imageUrl);


			}

			vacancy = new Vacancy(jobTitle, description, offerId, company, location,
					location, qualifications, responsibilities, advertDate,
					contractType, (Objects.nonNull(imageUrl) && !imageUrl.contentEquals("https://www.pnet.co.za")) ? imageUrl : "http://media.stepstone.com/modules/tracking/resources/images/smartbanner_icon_pnet.png", remuneration, "PNET", additionalTokens, affirmativeAction, url);
			if (vacancy.getImageUrl().contentEquals("https://www.pnet.co.za/upload_za/logo/8/5281-logo.jpg")) {
				vacancy.setImageUrl("http://media.stepstone.com/modules/tracking/resources/images/smartbanner_icon_pnet.png");
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return vacancy;
	}


}
