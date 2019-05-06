package za.co.moxomo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import za.co.moxomo.domain.GeoLocation;
import za.co.moxomo.services.GeoLocationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class MoxomoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoxomoApplication.class, args);
	}



	@Bean
	CommandLineRunner runner(GeoLocationService geoLocationService) {
		return args -> {
			// read json and write to db
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<GeoLocation>> typeReference = new TypeReference<List<GeoLocation>>(){};
			InputStream inputStream = TypeReference.class.getResourceAsStream("/json/geo-data.json");
			try {
				List<GeoLocation> geoLocations = mapper.readValue(inputStream,typeReference);
				log.info("geolocations {}", geoLocations.toString());
				if (geoLocationService.geoLocationCount() == 0) {
					geoLocationService.saveGeoLocations(geoLocations);
					log.info("Saved geolocations");

				}
			} catch (IOException e){
				log.error(e.getMessage());
			}
		};
	}
}
