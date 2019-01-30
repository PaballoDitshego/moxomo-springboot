package za.co.moxomo.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.moxomo.model.Vacancy;




import java.util.*;

/**
 * Created by paballo on 2016/11/16.
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static boolean validate(Vacancy vacancy) {
        boolean valid = (Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getJobTitle())
                && Objects.nonNull(vacancy.getDescription())
                && Objects.nonNull(vacancy.getLocation())
                && Objects.nonNull(vacancy.getAdvertDate())
                && Objects.nonNull(vacancy.getImageUrl()))
                && Objects.nonNull(vacancy.getUrl())
                && Objects.nonNull(vacancy.getCompany())
                && Objects.nonNull(vacancy.getOfferId());
        if (!valid) {
            logger.info("Invalid {}", vacancy.getUrl());
        }
        return valid;
    }
}
