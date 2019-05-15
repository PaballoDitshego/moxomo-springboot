package za.co.moxomo.services;

import za.co.moxomo.domain.GeoLocation;
import za.co.moxomo.domain.Vacancy;

import java.util.List;

public interface GeoService {

    void saveGeoLocation(GeoLocation geoLocation);

    void saveGeoLocations(List<GeoLocation> geoLocations);

    long geoLocationCount();

    List<String> getLocationsSuggestions(String term);

    List<GeoLocation> getAll();

    GeoLocation getByCityAndProvince(String city, String province);

    Vacancy geoCode(Vacancy vacancy);

    /* the code below is terrible and brittle, go ahead and modify if you think you can improve it, if not ignore because its that bad*/
    GeoLocation getGeoLocation(String loc) throws Exception;
}
