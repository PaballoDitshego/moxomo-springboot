package za.co.moxomo.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import za.co.moxomo.domain.GeoLocation;

import java.util.List;

public interface GeoLocationRepository extends MongoRepository<GeoLocation, String> {

    @Query("{ 'AccentCity' : { '$regex' : ?0 , $options: 'i'}}")
    List<GeoLocation> findAllByAccentCityIgnoreCase(String accentCity);
}
