package za.co.moxomo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;
import za.co.moxomo.domain.GeoLocation;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.repository.mongodb.GeoLocationRepository;
import za.co.moxomo.utils.Util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeoServiceImpl implements GeoService {


    private GeoLocationRepository geoLocationRepository;
    public static double defaultLat = -28.4793;
    private static double defaultLon = 24.6727;

    @Autowired
    public GeoServiceImpl(GeoLocationRepository geoLocationRepository) {
        this.geoLocationRepository = geoLocationRepository;
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
    public void deleteGeoLocations(List<GeoLocation> geoLocations) {
       geoLocationRepository.deleteAll(geoLocations);
    }

    @Override
    public long geoLocationCount() {
        return geoLocationRepository.count();
    }

    @Override
    public List<String> getLocationsSuggestions(String term) {
        return geoLocationRepository.findAllByAccentCityStartsWithIgnoreCase(term).stream()
                .map(geoLocation -> geoLocation.accentCity.concat(", ").concat(geoLocation.provinceName)).collect(Collectors.toList());
    }

    @Override
    public List<GeoLocation> getAll() {
        return geoLocationRepository.findAll();
    }

    @Override
    public GeoLocation getByCityAndProvince(String city, String province) {
        return geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(city, province);
    }

    @Override
    public Vacancy geoCode(Vacancy vacancy) {
        Objects.requireNonNull(vacancy);
        try {
            vacancy = doGeoCoding(vacancy);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return vacancy;
    }


    /* the code below is terrible and brittle, go ahead and modify if you think you can improve it, if not ignore because its that bad*/
    @Override
    public GeoLocation getGeoLocation(String loc) throws Exception {
        Objects.requireNonNull(loc);
        GeoLocation geoLocation;
        String location = loc;
        String[] locality = location.split(",");
        log.info("Locality split {}", String.valueOf(locality));
        int arrayLength = locality.length;
        log.info("Locality split length {}", arrayLength);

        if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase(locality[0]))) {
            return geoLocationRepository.findByAccentCityIgnoreCase(locality[0]);

        } else {
            if (arrayLength == 1) {
                log.debug("Finding approximate location for loc {}", location);
                String approximateLocation = Util.getApproximateLocation(locality[0]);
                log.debug("Approximate locale for location {} is {}", locality[0], approximateLocation);
                if (!approximateLocation.equals(location)) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                    log.debug(" found location {} for Approximate locale  {}", geoLocation.getAccentCity(), approximateLocation);
                    if (Objects.nonNull(geoLocation)) {
                        return geoLocation;
                    }
                }
            } else if (arrayLength == 2) {
                if (Util.isProvinceName(locality[1]) && Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[0], locality[1]))) {
                    return geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[0], locality[1]);

                } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase((locality[0])))) {
                    return geoLocationRepository.findByAccentCityIgnoreCase((locality[0]));

                } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase((locality[1])))) {
                    return geoLocationRepository.findByAccentCityIgnoreCase((locality[1]));

                } else {
                    String approximateLocation = Util.getApproximateLocation(locality[0]);
                    if (!approximateLocation.equals(location)) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                        if (Objects.nonNull(geoLocation)) {
                            return geoLocation;
                        } else {
                            approximateLocation = Util.getApproximateLocation(locality[1]);
                            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                            if (Objects.nonNull(geoLocation)) {
                                return geoLocation;
                            }
                        }
                    }
                }
            } else if (arrayLength == 3) {
                if (Util.isCountryName(locality[2])) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[0], locality[1]);
                    if (Objects.nonNull(geoLocation)) {
                        return geoLocation;
                    } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase(locality[0]))) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[0]);
                        return geoLocation;
                    } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase(locality[1]))) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[1]);
                        return geoLocation;
                    }
                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[0]))) {
                    return geoLocation;

                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[1]))) {
                    return geoLocation;
                } else {
                    String approximateLocation = Util.getApproximateLocation(locality[0]);
                    if (!approximateLocation.equals(location)) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                        if (Objects.nonNull(geoLocation)) {
                            return geoLocation;
                        } else {
                            approximateLocation = Util.getApproximateLocation(locality[1]);
                            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                            if (Objects.nonNull(geoLocation)) {
                                return geoLocation;
                            } else {
                                approximateLocation = Util.getApproximateLocation(locality[2]);
                                geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                                if (Objects.nonNull(geoLocation)) {
                                    return geoLocation;
                                }
                            }
                        }
                    }
                }
            } else if (arrayLength > 3) {
                if (Util.isProvinceName(locality[2]) && Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[1], locality[2]))) {
                    return geoLocation;
                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase((locality[2])))) {
                    return geoLocation;
                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase((locality[3])))) {
                    return geoLocation;
                } else {
                    String approximateLocation = Util.getApproximateLocation(locality[0]);
                    if (!approximateLocation.equals(location)) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                        if (Objects.nonNull(geoLocation)) {
                            return geoLocation;
                        } else {
                            approximateLocation = Util.getApproximateLocation(locality[1]);
                            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                            if (Objects.nonNull(geoLocation)) {
                                return geoLocation;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    private Vacancy doGeoCoding(Vacancy vacancy) throws Exception {
        Objects.requireNonNull(vacancy);
        log.info("Getting geoData for vacancy id {}, location {}", vacancy.getId(), vacancy.getLocation());
        GeoLocation geoLocation;
        String location = vacancy.getLocation();
        String[] locality = location.split(",");
        log.info("Locality split {}", String.valueOf(locality));
        int arrayLength = locality.length;
        log.info("Locality split length {}", arrayLength);


        if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase(locality[0]))) {
            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[0]);
            log.info("Found location {}", geoLocation.toString());
            vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
        } else {
            if (arrayLength == 1) {
                log.info("Finding approximate location for loc {}", location);
                String approximateLocation = Util.getApproximateLocation(locality[0]);
                log.info("Approximate locale for location {} is {}", locality[0], approximateLocation);
                if (!approximateLocation.equals(location)) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                    log.info(" found location {} for Approximate locale  {}", geoLocation.getAccentCity(), approximateLocation);
                    if (Objects.nonNull(geoLocation)) {
                        vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                    } else {
                        vacancy.setGeoPoint(new GeoPoint(defaultLat, defaultLon));
                    }

                }
            } else if (arrayLength == 2) {
                if (Util.isProvinceName(locality[1]) && Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[0], locality[1]))) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[0], locality[1]);
                    log.info("Found location {}", geoLocation.toString());
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase((locality[0])))) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCase((locality[0]));
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase((locality[1])))) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCase((locality[1]));
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else {
                    String approximateLocation = Util.getApproximateLocation(locality[0]);
                    if (!approximateLocation.equals(location)) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                        if (Objects.nonNull(geoLocation)) {
                            vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                        } else {
                            approximateLocation = Util.getApproximateLocation(locality[1]);
                            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                            if (Objects.nonNull(geoLocation)) {
                                vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                            } else {
                                vacancy.setGeoPoint(new GeoPoint(defaultLat, defaultLon));
                            }
                        }
                    }
                }
            } else if (arrayLength == 3) {
                if (Util.isCountryName(locality[2])) {
                    geoLocation = geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[0], locality[1]);
                    if (Objects.nonNull(geoLocation)) {
                        vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                    } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase(locality[0]))) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[0]);
                        vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                    } else if (Objects.nonNull(geoLocationRepository.findByAccentCityIgnoreCase(locality[1]))) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[1]);
                        vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                    }
                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[0]))) {
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));

                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(locality[1]))) {
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else {
                    String approximateLocation = Util.getApproximateLocation(locality[0]);
                    if (!approximateLocation.equals(location)) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                        if (Objects.nonNull(geoLocation)) {
                            vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                        } else {
                            approximateLocation = Util.getApproximateLocation(locality[1]);
                            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                            if (Objects.nonNull(geoLocation)) {
                                vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                            } else {
                                approximateLocation = Util.getApproximateLocation(locality[2]);
                                geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                                if (Objects.nonNull(geoLocation)) {
                                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                                } else {
                                    vacancy.setGeoPoint(new GeoPoint(defaultLat, defaultLon));
                                }
                            }
                        }
                    }
                }
            } else if (arrayLength > 3)
                if (Util.isProvinceName(locality[2]) && Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCaseAndProvinceNameIgnoreCase(locality[1], locality[2]))) {
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase((locality[2])))) {
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else if (Objects.nonNull(geoLocation = geoLocationRepository.findByAccentCityIgnoreCase((locality[3])))) {
                    vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                } else {
                    String approximateLocation = Util.getApproximateLocation(locality[0]);
                    if (!approximateLocation.equals(location)) {
                        geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                        if (Objects.nonNull(geoLocation)) {
                            vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                        } else {
                            approximateLocation = Util.getApproximateLocation(locality[1]);
                            geoLocation = geoLocationRepository.findByAccentCityIgnoreCase(approximateLocation);
                            if (Objects.nonNull(geoLocation)) {
                                vacancy.setGeoPoint(new GeoPoint(geoLocation.latitude, geoLocation.longitude));
                            } else {
                                vacancy.setGeoPoint(new GeoPoint(defaultLat, defaultLon));
                            }
                        }
                    }
                }
        }
        if(Objects.isNull(vacancy.getGeoPoint())){
            vacancy.setGeoPoint(new GeoPoint(defaultLat, defaultLon));
        }
        log.debug("returning vacancy with the locality {}", vacancy.getGeoPoint().toString());
        return vacancy;
    }


}