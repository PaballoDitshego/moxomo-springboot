package za.co.moxomo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.moxomo.domain.GeoLocation;
import za.co.moxomo.repository.mongodb.GeoLocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    private GeoLocationRepository geoLocationRepository;

    @Autowired
    public GeoLocationServiceImpl(GeoLocationRepository geoLocationRepository){
        this.geoLocationRepository=geoLocationRepository;

    }

    @Override
    public void saveGeoLocation(GeoLocation geoLocation) {
        geoLocationRepository.save(geoLocation);

    }

    @Override
    public void saveGeoLocations(List<GeoLocation> geoLocations) {
        geoLocationRepository.saveAll(geoLocations);

    }

    @Override
    public long geoLocationCount() {
        return geoLocationRepository.count();
    }

    @Override
    public List<String> getLocationsSuggestions(String term){
        return geoLocationRepository.findAllByAccentCityIgnoreCase(term).stream()
                .map(geoLocation->geoLocation.accentCity.concat(",").concat(geoLocation.provinceName)).collect(Collectors.toList());
    }
}
