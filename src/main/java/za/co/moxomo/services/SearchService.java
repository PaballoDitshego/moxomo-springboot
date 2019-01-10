package co.moxomo.services;

import co.moxomo.model.Vacancy;
//import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by paballo on 2017/02/20.
 */
public interface SearchService {

    void index(String id, Vacancy vacancy);

    void removeDocument(String id);

   // void removeDocuments(List<ObjectId> ids);
}
