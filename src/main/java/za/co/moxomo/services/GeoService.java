package za.co.moxomo.services;

import za.co.moxomo.domain.GeoLocation;

import java.util.List;

public interface GeoLocationService {

    void saveGeoLocation(GeoLocation geoLocation);

    void saveGeoLocations(List<GeoLocation> geoLocations);

    long geoLocationCount();

    List<String> getLocationsSuggestions(String term);

    GeoLocation getByCityAndProvince(String city, String province);
}
