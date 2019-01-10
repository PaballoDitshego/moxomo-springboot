package co.moxomo.utils;


import co.moxomo.model.Vacancy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;



import java.util.*;



import java.util.HashMap;

/**
 * Created by paballo on 2016/11/16.
 */
public class Util {

	private static final Logger logger = Logger.getLogger(Util.class
			.getCanonicalName());



	public static void save(Vacancy vac) {
      
		//Entity entity = EntityCreator.returnEntity(vac);
		/*entity.setProperty("job_title", vac.getJob_title());

		int length = 0;
		try {
			if (vac.getDescription() != null) {
				length = vac.getDescription().getBytes("UTF-8").length;
			}
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		if (length < 1500) {
			entity.setProperty("description", vac.getDescription());
		} else {
			entity.setUnindexedProperty("description",
					new Text(vac.getDescription()));
		}
		entity.setProperty("category", vac.getCategory());
		entity.setProperty("company_name", vac.getCompany_name());
		if (vac.getCompetencies() != null) {
			entity.setUnindexedProperty("competencies",
					new Text(vac.getCompetencies()));
		}
		if (vac.getDuties() != null) {
			entity.setUnindexedProperty("duties", new Text(vac.getDuties()));
		}
		entity.setProperty("imageUrl", vac.getImageUrl());
		entity.setProperty("province", vac.getProvince());
		entity.setProperty("location", vac.getLocation());
		if (vac.getMin_qual() != null) {
			entity.setUnindexedProperty("min_qual", new Text(vac.getMin_qual()));
		}

		entity.setProperty("advertDate", vac.getAdvertDate());
		entity.setProperty("closingDate", vac.getClosingDate());
		entity.setProperty("website", vac.getWebsite());
		entity.setProperty("source", vac.getSource());
		entity.setProperty("ad_id", vac.getAd_id());
		Key key = persistEntity(entity);
		if (key != null) {
			buildVacancyDoc(entity, key.getId());
		}*/
	}

	public static void buildVacancyDoc(Object object, Long id) {

		String description;
	/*	if (entity.getProperty("description") instanceof Text) {
			Text text = (Text) entity.getProperty("description");
			description = text.getValue();
		} else {
			description = (String) entity.getProperty("description");
		}
		String qualification = null;

		if (entity.getProperty("min_qual") != null) {
			Text text = (Text) entity.getProperty("min_qual");
			qualification = text.getValue();
		}
		Document.Builder docBuilder = Document
				.newBuilder()
				.setId(id.toString())

				.addField(
						Field.newBuilder()
								.setName("job_title")
								.setText(
										(String) entity
												.getProperty("job_title")))
				.addField(
						Field.newBuilder()
								.setName("imageUrl")
								.setText(
										(String) entity.getProperty("imageUrl")))
				.addField(
						Field.newBuilder().setName("description")
								.setText(description))
				.addField(
						Field.newBuilder()
								.setName("advertDate")
								.setDate(
										(Date) entity.getProperty("advertDate")))
				.

				addField(
						Field.newBuilder().setName("min_qual")
								.setText(qualification))
				.addField(
						Field.newBuilder()
								.setName("location")
								.setText(
										((String) entity
												.getProperty("location"))));

		Document doc = docBuilder.build();
		INDEX.putAsync(doc);*/

	}

	public static void buildVacancyDoc(Vacancy vacancy) {

	/*	Document.Builder docBuilder = Document
				.newBuilder()
				.setId(vacancy.getId().toString())

				.addField(
						Field.newBuilder().setName("job_title")
								.setText(vacancy.getJob_title()))
				.addField(
						Field.newBuilder().setName("imageUrl")
								.setText((vacancy.getImageUrl())))
				.addField(
						Field.newBuilder().setName("description")
								.setText(vacancy.getDescription()))
				.addField(
						Field.newBuilder().setName("advertDate")
								.setDate(vacancy.getAdvertDate()))
				.

				addField(
						Field.newBuilder().setName("min_qual")
								.setText(vacancy.getMin_qual()))
				.addField(
						Field.newBuilder().setName("location")
								.setText((vacancy.getLocation())));

		Document doc = docBuilder.build();

		INDEX.put(doc);*/

	}

/*	public static void deleteEntity(Key key) {

		Transaction txn = datastore.beginTransaction();
		try {
			datastore.delete(key);
			txn.commit();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			} else {
				deleteFromCache(key);
			}
		}
	}

	public static Entity findEntity(Key key) {

		try {
			Entity entity = getFromCache(key);
			if (entity != null) {
				return entity;
			}

			return datastore.get(key);
		} catch (EntityNotFoundException e) {
			return null;
		}*/

/*
	public static QueryResultList<Entity> listSocialEntities() {

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1000);
		Query query = new Query("Vacancy");

		query.addProjection(new PropertyProjection("job_title", String.class));
		query.addProjection(new PropertyProjection("description", String.class));
		query.addProjection(new PropertyProjection("province", String.class));
		query.addProjection(new PropertyProjection("imageUrl", String.class));
		query.addProjection(new PropertyProjection("company_name", String.class));
		query.addProjection(new PropertyProjection("location", String.class));
		;
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 12);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String advertDate = sdf.format(cal.getTime());
		try {
			cal.setTime(sdf.parse(advertDate));
		} catch (ParseException e) {

			e.printStackTrace();
		}

		query.setFilter(new Query.FilterPredicate("advertDate",
				FilterOperator.GREATER_THAN, cal.getTime()));

		PreparedQuery pq = datastore.prepare(query);
		QueryResultList<Entity> list = pq.asQueryResultList(fetchOptions);

		return list;
	}

	public static QueryResultList<Entity> listEntities(String kind,
			String searchBy, String searchFor, int pageSize, String startCursor) {

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(25);

		if (startCursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
		}

		Query query = new Query(kind).addSort("advertDate",
				Query.SortDirection.DESCENDING).addSort("closingDate",
				Query.SortDirection.ASCENDING);

		query.addProjection(new PropertyProjection("job_title", String.class));
		query.addProjection(new PropertyProjection("description", String.class));
		query.addProjection(new PropertyProjection("province", String.class));
		query.addProjection(new PropertyProjection("advertDate", Date.class));
		query.addProjection(new PropertyProjection("imageUrl", String.class));
		query.addProjection(new PropertyProjection("company_name", String.class));
		query.addProjection(new PropertyProjection("website", String.class));

		if (searchFor != null && !"".equals(searchFor)) {

			query.setFilter(new Query.FilterPredicate(searchBy,
					FilterOperator.EQUAL, searchFor));

		}

		PreparedQuery pq = datastore.prepare(query);

		return pq.asQueryResultList(fetchOptions);
	}

	public static QueryResultList<Entity> getRecentVacancies() {

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1000);

		Query query = new Query("Vacancy");

		query.addProjection(new PropertyProjection("job_title", String.class));
		query.addProjection(new PropertyProjection("description", String.class));
		query.addProjection(new PropertyProjection("province", String.class));
		query.addProjection(new PropertyProjection("advertDate", Date.class));
		query.addProjection(new PropertyProjection("imageUrl", String.class));

		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, -2);

		query.setFilter(new Query.FilterPredicate("advertDate",
				FilterOperator.GREATER_THAN_OR_EQUAL, c.getTime()));

		PreparedQuery pq = datastore.prepare(query);

		return pq.asQueryResultList(fetchOptions);
	}

	public static boolean isAvailable(String url) {

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);

		Query query = new Query("Vacancy").setKeysOnly();

		query.setFilter(new Query.FilterPredicate("website",
				FilterOperator.EQUAL, url));

		PreparedQuery pq = datastore.prepare(query);

		if (pq.countEntities(fetchOptions) > 0) {
			return true;
		} else {
			return false;
		}
	}
*/

	public static boolean isAvailable(String ad_id) {

		/*FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1);

		Query query = new Query(type).setKeysOnly();

		query.setFilter(new Query.FilterPredicate("ad_id",
				FilterOperator.EQUAL, ad_id));

		PreparedQuery pq = datastore.prepare(query);

		if (pq.countEntities(fetchOptions) > 0) {
			return true;
		} else {
			return false;
		}*/

	return false;
	}

	public static ArrayList<String> captured(String type) {

	/*	FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10000);

		Query query = new Query(type);
		query.addProjection(new PropertyProjection("ad_id", String.class));

		PreparedQuery pq = datastore.prepare(query);*/

		ArrayList<String> list = new ArrayList<String>();
	/*	QueryResultList<Entity> entities = pq.asQueryResultList(fetchOptions);

		while (!entities.isEmpty()) {
			for (Entity entity : entities) {
				list.add((String) entity.getProperty("ad_id"));
			}
			fetchOptions.startCursor(entities.getCursor());
			entities = pq.asQueryResultList(fetchOptions);

		}*/

		return list;

	}

	public static boolean isSocial(String url) {

		/*FetchOptions fetchOptions = FetchOptions.Builder.withLimit(1000);

		Query query = new Query("SocialBase").setKeysOnly();
		query.setFilter(new Query.FilterPredicate("url", FilterOperator.EQUAL,
				url));

		PreparedQuery pq = datastore.prepare(query);

		if (pq.countEntities(fetchOptions) > 0) {
			return true;
		} else {
			return false;
		}*/

		return false;
	}


}
