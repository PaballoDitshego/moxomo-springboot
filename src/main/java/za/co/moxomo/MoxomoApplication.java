package za.co.moxomo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import za.co.moxomo.domain.GeoLocation;
import za.co.moxomo.domain.SearchSuggestionKeyword;
import za.co.moxomo.repository.mongodb.SearchSuggestionKeywordRepository;
import za.co.moxomo.services.GeoService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@Slf4j
public class MoxomoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoxomoApplication.class, args);
    }


   @Bean
    CommandLineRunner runner(GeoService geoService, SearchSuggestionKeywordRepository searchSuggestionKeywordRepository) {
       return args -> {
           updateGeolocations(geoService);
           updateKeywordSearchSuggetions(searchSuggestionKeywordRepository);

       };
   }

    private static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }


    private void updateGeolocations(GeoService geoService){
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<GeoLocation>> typeReference = new TypeReference<List<GeoLocation>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/geo-data.json");
        try {
            List<GeoLocation> geoLocations = mapper.readValue(inputStream, typeReference);
            List<GeoLocation> existing = geoService.getAll();

            List<GeoLocation> deltas = geoLocations.stream()
                    .filter(not(new HashSet<>(existing)::contains))
                    .collect(Collectors.toList());
            geoService.saveGeoLocations(deltas);

            deltas = existing.stream()
                    .filter(not(new HashSet<>(geoLocations)::contains))
                    .collect(Collectors.toList());
            geoService.deleteGeoLocations(deltas);



        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private void updateKeywordSearchSuggetions(SearchSuggestionKeywordRepository searchSuggestionKeywordRepository){
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<SearchSuggestionKeyword>> typeReference = new TypeReference<List<SearchSuggestionKeyword>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/keywords.json");
        try {
            List<SearchSuggestionKeyword> searchSuggestionKeywords = mapper.readValue(inputStream, typeReference);
            List<SearchSuggestionKeyword> existing = searchSuggestionKeywordRepository.findAll();

            List<SearchSuggestionKeyword> deltas = searchSuggestionKeywords.stream()
                    .filter(not(new HashSet<>(existing)::contains))
                    .collect(Collectors.toList());
            searchSuggestionKeywordRepository.saveAll(deltas);

            deltas = existing.stream()
                    .filter(not(new HashSet<>(searchSuggestionKeywords)::contains))
                    .collect(Collectors.toList());
            searchSuggestionKeywordRepository.deleteAll(deltas);


        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    }

