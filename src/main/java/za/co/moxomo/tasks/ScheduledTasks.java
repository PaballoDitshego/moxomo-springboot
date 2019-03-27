package za.co.moxomo.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.crawlers.FirstRand;
import za.co.moxomo.crawlers.MrPrice;
import za.co.moxomo.crawlers.PNet;
import za.co.moxomo.services.VacancySearchService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by paballo on 2016/11/16.
 */
@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final VacancySearchService vacancySearchService;

    @Autowired
    public ScheduledTasks(VacancySearchService vacancySearchService) {
        this.vacancySearchService = vacancySearchService;
    }

    @Scheduled(cron = "0 1 1 * * ?")
    public void deleteExpired() {
        logger.info("Vacancy deletion job triggered");
        vacancySearchService.deleteOldVacancies();
    }

}
