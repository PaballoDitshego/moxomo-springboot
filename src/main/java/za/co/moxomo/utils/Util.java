package za.co.moxomo.utils;


import za.co.moxomo.model.Vacancy;

import java.util.logging.Logger;


import java.util.*;

/**
 * Created by paballo on 2016/11/16.
 */
public class Util {

    private static final Logger logger = Logger.getLogger(Util.class
            .getCanonicalName());

    public static boolean validate(Vacancy vacancy) {
        return (Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getJobTitle())
                && Objects.nonNull(vacancy.getDescription())
                && Objects.nonNull(vacancy.getLocation())
                && Objects.nonNull(vacancy.getAdvertDate())
                && Objects.nonNull(vacancy.getImageUrl()))
                && Objects.nonNull(vacancy.getUrl())
                && Objects.nonNull(vacancy.getCompany())
                && Objects.nonNull(vacancy.getOfferId());
    }


}
