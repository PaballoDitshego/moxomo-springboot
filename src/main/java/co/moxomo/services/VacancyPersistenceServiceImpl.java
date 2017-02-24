package co.moxomo.services;

import co.moxomo.model.Vacancy;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by paballo on 2017/02/20.
 */

@Service
public class VacancyPersistenceServiceImpl implements VacancyPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(VacancyPersistenceService.class);

    @Value("${db.name}")
    private String DB_NAME;

    @Value("{db.collection}")
    private String DB_TABLE;

    private final String ID = "_id";

    private MongoClient mongoClient;


    private SearchService searchService;

    private MongoDatabase database;

    @Autowired
    public VacancyPersistenceServiceImpl(MongoClient mongoClient, SearchService searchService){
        this.mongoClient = mongoClient;
        this.searchService = searchService;
    }


    @PostConstruct
    public void init(){
        database = mongoClient.getDatabase(DB_NAME);

    }

    @Override
    public void persistVacancy(Vacancy vacancy) {
        logger.info("Saving vacancy");
        Objects.requireNonNull(vacancy);
        if(!documentExists(vacancy.getWebsite())) {
            Document dbObject = createDBObject(vacancy);
            database.getCollection(DB_TABLE).insertOne(dbObject);
            ObjectId id = dbObject.getObjectId(ID);

            if (Objects.nonNull(id)) {
                searchService.index(id.toHexString(), vacancy);
            }
        }
    }

    @Override
    public void deleteVacancy(String vacancyId) {
        Objects.requireNonNull(vacancyId);

        MongoCollection<Document> collection = database.getCollection(DB_TABLE);
        DeleteResult result = collection.deleteOne(new Document("_id", new ObjectId(vacancyId)));
        if(result.wasAcknowledged() && result.getDeletedCount() >0){
            searchService.removeDocument(vacancyId);
        }
    }

    @Override
    public void deleteVacancies(String criteriaValue){
        Objects.requireNonNull("advertDate", criteriaValue);

        Bson filter = Filters.lt("closingDate", criteriaValue);
        FindIterable<Document> collection = database.getCollection(DB_TABLE).find(filter);
        List<ObjectId> toRemoveIds = new ArrayList<>();
        Iterator<Document> iterator = collection.iterator();
        while(iterator.hasNext()) {
            toRemoveIds.add(iterator.next().getObjectId(ID));
        }

        Bson filterToRemove = Filters.in(ID, toRemoveIds);
        long deleteCount = database.getCollection(DB_TABLE).
                deleteMany(filterToRemove).getDeletedCount();
        searchService.removeDocuments(toRemoveIds);
        logger.info("Deleted {} entries",deleteCount );
    }



    private static Document createDBObject(Vacancy vacancy) {
        Objects.requireNonNull(vacancy);

        Document document = new Document();
        document.put("advertDate", vacancy.getAdvertDate());
        document.put("agent_id", vacancy.getAgent_id());
        document.put("category", vacancy.getCategory());
        document.put("closingDate", vacancy.getClosingDate());
        document.put("company_id", vacancy.getCompany_id());
        document.put("description", vacancy.getDescription());
        document.put("imageUrl", vacancy.getImageUrl());
        document.put("duties", vacancy.getDuties());
        document.put("company_name", vacancy.getCompany_name());
        document.put("location", vacancy.getLocation());
        document.put("job_title", vacancy.getJob_title());
        document.put("key_competencies", vacancy.getCompetencies());
        document.put("min_qual", vacancy.getMin_qual());
        document.put("website", vacancy.getWebsite());

        return document;
    }

    private boolean documentExists(String website){
        Objects.requireNonNull(website);

        MongoCollection<Document> collection = database.getCollection(DB_TABLE);
        Bson filter = Filters.eq("website", website);
        return Objects.nonNull(collection.find(filter).first());
    }

    @PreDestroy
    public void shutDown(){
        mongoClient.close();
    }






}
