package co.moxomo.services;

import co.moxomo.model.Vacancy;
//import org.bson.types.ObjectId;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.Client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by paballo on 2017/02/20.
 */
//@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    private Client elasticSearchClient;

    private static final String INDEX = "vacancies";
    private static final String DOCUMENT_TYPE = "vacancy";

    @Autowired
    public SearchServiceImpl(Client elasticSearchClient){
        this.elasticSearchClient = elasticSearchClient;
    }

    @PostConstruct
    public void init() {
        boolean exists = elasticSearchClient.admin().indices()
                .prepareExists(INDEX)
                .execute().actionGet().isExists();
        if (!exists) {
            CreateIndexRequestBuilder createIndexRequestBuilder = elasticSearchClient.admin().indices().prepareCreate(INDEX);
            createIndexRequestBuilder.execute().actionGet();
        }
    }


    @Override
    public void index(String id, Vacancy vacancy) {
        IndexRequestBuilder indexRequestBuilder = elasticSearchClient.prepareIndex(INDEX, DOCUMENT_TYPE, id);
        XContentBuilder contentBuilder;
        try {
            contentBuilder = jsonBuilder().startObject().prettyPrint();
            contentBuilder.field("job_title", vacancy.getJob_title())
                    .field("description", vacancy.getDescription())
                    .field("duties", vacancy.getDuties())
                    .field("min_qual", vacancy.getMin_qual())
                    .field("advertDate", vacancy.getAdvertDate())
                    .field("closingDate", vacancy.getClosingDate())
                    .field("competencies", vacancy.getCompetencies())
                    .field("category", vacancy.getCategory())
                    .field("company", vacancy.getCompany_name());

            contentBuilder.endObject();
            indexRequestBuilder.setSource(contentBuilder);
            indexRequestBuilder.execute().actionGet();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public void removeDocument(String id) {
        elasticSearchClient.prepareDelete(INDEX, DOCUMENT_TYPE, id).execute(new ActionListener<DeleteResponse>() {
            @Override
            public void onResponse(DeleteResponse deleteResponse) {
                logger.debug("Document with id {} deleted", id);
            }
            @Override
            public void onFailure(Exception e) {
                logger.debug("Failed to remove Document with id {}", id);
            }
        });
    }

   /* @Override
    public void removeDocuments(List<ObjectId> ids) {
        for(ObjectId objectId: ids){
            elasticSearchClient.prepareDelete(INDEX, DOCUMENT_TYPE, objectId.toHexString()).execute(new ActionListener<DeleteResponse>() {
                @Override
                public void onResponse(DeleteResponse deleteResponse) {
                    logger.debug("Document with id {} deleted", objectId.toHexString());
                }
                @Override
                public void onFailure(Exception e) {
                    logger.debug("Failed to remove Document with id {}",objectId.toHexString());
                }
            });
        }
    }*/

    @PreDestroy
    private void shutDown() {
        elasticSearchClient.close();
    }


}
