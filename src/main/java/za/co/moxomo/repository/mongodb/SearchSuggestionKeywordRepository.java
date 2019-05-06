package za.co.moxomo.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import za.co.moxomo.domain.SearchSuggestionKeyword;

import java.util.List;

public interface SearchSuggestionKeywordRepository extends MongoRepository<SearchSuggestionKeyword, String> {

    @Query("{ 'keyword' : { '$regex' : ?0 , $options: 'i'}}")
    List<SearchSuggestionKeywordRepository> findAllByKeywordIgnoreCase(String keyword);

    SearchSuggestionKeywordRepository findOneByKeyword(String keyword);


}
