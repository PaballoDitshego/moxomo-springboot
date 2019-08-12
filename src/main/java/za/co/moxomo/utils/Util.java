package za.co.moxomo.utils;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.moxomo.domain.Notification;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.enums.AlertRoute;
import za.co.moxomo.enums.AlertType;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by paballo on 2016/11/16.
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    private static WeakHashMap<String, String> cityMap;
    private static Pattern p = Pattern.compile("\\d+");


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
            logger.error("Invalid {}, Object {}", vacancy.getUrl(), vacancy.toString());
        }
        return valid;
    }

    public static Notification generateNotification(Vacancy vacancy, AlertPreference alertPreference) {
        Notification notification = Notification.builder().id(UUID.randomUUID().toString())
                .description(vacancy.getDescription())
                .createdDateTime(Instant.now()).
                        advertDate(vacancy.getAdvertDate())
                .url(vacancy.getUrl())
                .gcmToken(alertPreference.getGcmToken())
                .alertType(AlertType.JOB_ALERT.name())
                .imageUrl(vacancy.getImageUrl())
                .entityId(vacancy.getId())
                .title(vacancy.getJobTitle())
                .entityType(Vacancy.class.getTypeName())
                .mobileNumber(alertPreference.getMobileNumber())
                .alertTitle(alertPreference.getKeyword())
                .sms(alertPreference.isSmsAlert())
                .location(vacancy.getLocation()).route((alertPreference.isPushAlert()) ?
                        AlertRoute.FCM.getRoute() : AlertRoute.SMS.getRoute()).build();

        return notification;
    }

    public static String getCompany(String company) {
        if (company.equalsIgnoreCase("First National Bank") || company.equalsIgnoreCase("RMB") || company.equalsIgnoreCase("Rand Merchant Bank") || company.contains("FNB")) {
            return "FirstRand";
        }

        if (company.equalsIgnoreCase("bcx")) {
            return "Business Connexion";
        }
        return company;
    }


    public static String getApproximateLocation(String location) {
        //used for  brute-force fallback, terrible and very hack
        if (StringUtils.containsIgnoreCase(location, "-") && !location.equalsIgnoreCase("graaf-reinet")) {
            location = location.replace("/", ",");
        }
        if (StringUtils.containsIgnoreCase(location, "/")) {
            location = location.replace("/", ",");
        }
        if (StringUtils.containsIgnoreCase(location, " and ")) {
            location = location.replace(" and ", ",");
        }
        if (StringUtils.containsIgnoreCase(location, "Central") || StringUtils.containsIgnoreCase(location, "Sentraal")) {
            location = location.replace("Central", "").replace("Sentraal", "");
        }

        if (StringUtils.containsIgnoreCase(location, "ZA-")) {
            location = location.replace("ZA-", "").replace("za-", "");
        }

        if (StringUtils.containsIgnoreCase(location, "Region")) {

            location = location.replace("Region", "").replace("region", "");
        }

        if (StringUtils.containsIgnoreCase(location, "tambo international airport")) {
            return "O.R Tambo International Airport";
        }


        if (StringUtils.containsIgnoreCase(location, "Tshwane") || StringUtils.containsIgnoreCase(location, "PTA")
                || StringUtils.containsIgnoreCase("Pretoria", location)) {
            return "Pretoria";
        }
        if (StringUtils.containsIgnoreCase(location, "Century City") ||
                StringUtils.containsIgnoreCase(location, "Cape Town") ||
                StringUtils.containsIgnoreCase(location, "CPT") || StringUtils.containsIgnoreCase(location, "Southern Surbubs") || StringUtils.containsIgnoreCase(location, "Rondebosch")
                || StringUtils.containsIgnoreCase(location, "Kirstenbosch") || StringUtils.containsIgnoreCase(location, "West Coast")) {
            return "Cape Town";
        }
        if (StringUtils.containsIgnoreCase(location, "Centirion")) {
            return "Centurion";
        }
        if (StringUtils.containsIgnoreCase(location, "Johannesburg East") ||
                StringUtils.containsIgnoreCase(location, "JHB") ||
                StringUtils.containsIgnoreCase(location, "Johannesburg West") || StringUtils.containsIgnoreCase(location, "Johannesburg CBD")) {
            return "Johannesburg";
        }

        if (StringUtils.containsIgnoreCase(location, "Northern Surbubs")) {
            return "Johannesburg North";
        }

        if (StringUtils.containsIgnoreCase(location, "Mbombela")) {
            return "Nelspruit";
        }

        if (StringUtils.containsIgnoreCase(location, "eMalahleni") || StringUtils.containsIgnoreCase(location, "Witbank") || StringUtils.containsIgnoreCase(location, "Malahleni")) {
            return "Witbank";
        }
        if (StringUtils.containsIgnoreCase(location, "Cape Winelands")) {
            location = "Stellenbosch";
        }
        if (StringUtils.containsIgnoreCase(location, "Randpark Ridge")
                || StringUtils.containsIgnoreCase(location, "Rand park Ridge")) {
            return "Johannesburg";
        }

        if (StringUtils.containsIgnoreCase(location, "DBN") ||
                StringUtils.containsIgnoreCase(location, "Thekwini")
                || StringUtils.containsIgnoreCase(location, "eThekwini") || StringUtils.containsIgnoreCase(location, "Durban")) {
            return "DURBAN";
        }

        if (StringUtils.containsIgnoreCase(location, "Kempton") || StringUtils.containsIgnoreCase(location, "Airport Industria")) {
            return "Kempton Park";
        }

        if (StringUtils.contains(location, "Kloof")) {
            return "Durban";
        }
        if (StringUtils.contains(location, "ekurhuleni")) {
            return "East Rand";
        }
        if (StringUtils.contains(location, "sedibeng")) {
            return "Vaal";
        }
        return location;
    }

    public static boolean isProvinceName(String location) {
        return (location.matches("gauteng|western cape|mpumalanga|limpopo|north west|eastern cape|kwazulu natal|kwazulu-natal|northern cape|free state"));
    }

    public static boolean isCountryName(String location) {
        return (location.matches("south africa|za|suid-afrika|south-africa|suid afrika"));
    }


    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (round(dist, 0));
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getPnetOfferIdFromUrl(String url) {
        String offerId = null;
        Matcher m = p.matcher(url);
        while (m.find()) {
            offerId = m.group();
        }
        if (offerId.equals("")) return null;
        return offerId;
    }

}
