package za.co.moxomo.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.moxomo.domain.Notification;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.enums.AlertRoute;
import za.co.moxomo.enums.AlertType;


import java.time.Instant;
import java.util.*;

/**
 * Created by paballo on 2016/11/16.
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    private static WeakHashMap<String, String> cityMap;


    public static boolean validate(Vacancy vacancy) {
        boolean valid = (Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getJobTitle()) && !vacancy.getJobTitle().isEmpty()
                && Objects.nonNull(vacancy.getDescription()) && !vacancy.getDescription().isEmpty()
                && Objects.nonNull(vacancy.getLocation()) && !vacancy.getLocation().isEmpty()
                && Objects.nonNull(vacancy.getAdvertDate())
                && Objects.nonNull(vacancy.getImageUrl())) && !vacancy.getImageUrl().isEmpty()
                && Objects.nonNull(vacancy.getUrl()) && !vacancy.getUrl().isEmpty()
                && Objects.nonNull(vacancy.getCompany()) && !vacancy.getCompany().isEmpty()
                && Objects.nonNull(vacancy.getOfferId()) && !vacancy.getOfferId().isEmpty();
        if (!valid) {
            logger.info("Invalid {}, Object {}", vacancy.getUrl(), vacancy.toString());
        }
        return valid;
    }

    public static Notification generateNotification(Vacancy vacancy, AlertPreference alertPreference) {
        Notification notification = Notification.builder().id(UUID.randomUUID().toString())
                .description(vacancy.getDescription())
                .createdDateTime(Instant.now()).advertDate(vacancy.getAdvertDate()).url(vacancy.getUrl()).gcmToken(alertPreference.getGcmToken()).alertType(AlertType.JOB_ALERT.name())
                .imageUrl(vacancy.getImageUrl()).entityId(vacancy.getId()).entityType(Vacancy.class.getTypeName()).mobileNumber(alertPreference.getMobileNumber()).sms(alertPreference.isSmsAlert())
                .location(vacancy.getLocation()).route((alertPreference.isPushAlert()) ?
                        AlertRoute.FCM.getRoute() : AlertRoute.SMS.getRoute()).build();

        return notification;
    }

    public static void main(String[] args){
        String city = "Montague Gardens - Cape Town";
        String aproximateLocation = getApproximateLocation(city);
        logger.info("approximte {}", aproximateLocation);
    }

    public static String getApproximateLocation(String location) {
        //used for  brute-force fallback, terrible and very hack
        if(StringUtils.containsIgnoreCase(location, "-") && !location.equalsIgnoreCase("graaf-reinet")){
            location = location.replace("/", ",");
        }
        if(StringUtils.containsIgnoreCase(location, "/")){
            location = location.replace("/", ",");
        }
        if(StringUtils.containsIgnoreCase(location, " and ")){
            location = location.replace(" and ", ",");
        }
        if(StringUtils.containsIgnoreCase(location, "Central") || StringUtils.containsIgnoreCase(location, "Sentraal")){
            location = location.replace("Central", "").replace("Sentraal", "");
        }

        if(StringUtils.containsIgnoreCase(location, "ZA-")){
            location = location.replace("ZA-", "").replace("za-", "");
        }

        if(StringUtils.containsIgnoreCase(location, "Region")){

            location = location.replace("Region", "").replace("region", "");
        }

        if(StringUtils.containsIgnoreCase(location,"tambo international airport")){
            return "O.R Tambo International Airport";
        }



        if (StringUtils.containsIgnoreCase(location, "Tshwane") || StringUtils.containsIgnoreCase(location, "PTA")
                || StringUtils.containsIgnoreCase("Pretoria", location)) {
            return  "Pretoria";
        }
        if (StringUtils.containsIgnoreCase(location, "Century City" ) ||
                StringUtils.containsIgnoreCase( location, "Cape Town") ||
                StringUtils.containsIgnoreCase(location, "CPT") || StringUtils.containsIgnoreCase(location, "Southern Surbubs") || StringUtils.containsIgnoreCase(location, "Rondebosch")
                || StringUtils.containsIgnoreCase(location, "Kirstenbosch") || StringUtils.containsIgnoreCase(location, "West Coast")) {
            return "Cape Town";
        }
        if (StringUtils.containsIgnoreCase(location, "Centirion")) {
            return  "Centurion";
        }
        if (StringUtils.containsIgnoreCase(location, "Johannesburg") ||
                StringUtils.containsIgnoreCase(location, "JHB") ||
                StringUtils.containsIgnoreCase(location, "Northern Surburbs")) {
            return "Johannesburg";
        }

        if (StringUtils.containsIgnoreCase(location, "Mbombela")) {
            return "Nelspruit";
        }

        if (StringUtils.containsIgnoreCase(location, "eMalahleni") || StringUtils.containsIgnoreCase(location, "Witbank") || StringUtils.containsIgnoreCase(location, "Malahleni")) {
            return  "Witbank";
        }
        if (StringUtils.containsIgnoreCase(location, "Cape Winelands")) {
            location = "Stellenbosch";
        }
        if (StringUtils.containsIgnoreCase( location, "Randpark Ridge")
                || StringUtils.containsIgnoreCase( location, "Rand park Ridge")) {
            return  "Johannesburg";
        }

        if (StringUtils.containsIgnoreCase( location, "DBN") ||
                StringUtils.containsIgnoreCase(location, "Thekwini")
                || StringUtils.containsIgnoreCase(location, "eThekwini") || StringUtils.containsIgnoreCase( location, "Durban") ) {
            return "DURBAN";
        }

        if (StringUtils.containsIgnoreCase( location, "Kempton") ||  StringUtils.containsIgnoreCase(location, "Airport Industria")) {
            return "Kempton Park";
        }

        if (StringUtils.contains(location, "Kloof")) {
            return "Durban";
        }
        if (StringUtils.contains(location, "ekurhuleni")) {
            return "East Rand";
        }
        if (StringUtils.contains( location, "sedibeng")) {
            return  "Vaal";
        }
        return location;
    }

    public static boolean isProvinceName(String location){
        return (location.matches("gauteng|western cape|mpumalanga|limpopo|north west|eastern cape|kwazulu natal|kwazulu-natal|northern cape|free state"));
    }

    public static boolean isCountryName(String location){
        return (location.matches("south africa|za|suid-afrika|south-africa|suid afrika"));
    }

}
