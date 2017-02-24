package co.moxomo.tasks;

import co.moxomo.crawlers.CareerJunction;
import co.moxomo.crawlers.Careers24;
import co.moxomo.crawlers.JobVine;
import co.moxomo.crawlers.PNet;
import co.moxomo.services.VacancyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by paballo on 2016/11/16.
 */
@Component
public class ScheduledTasks {

    @Autowired
    private VacancyService vacancyService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 14400000) //runs every 4 hours
    public void crawl(){
        logger.info("Crawl started.");
        PNet.crawl();
        CareerJunction.crawl();
        Careers24.crawl();
        JobVine.crawl();

    }

    @Scheduled(fixedRate = 14400000)
    public void deleteExpired(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String advertDate = sdf.format(cal.getTime());
        try {
            cal.setTime(sdf.parse(advertDate));
            vacancyService.deleteVacancies(String.valueOf(cal.getTime()));
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
    }

}
