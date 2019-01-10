package co.moxomo.tasks;

import co.moxomo.crawlers.*;
import co.moxomo.services.VacancyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    private VacancyService vacancyService;
    private PNet pnet;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

   @Autowired
    public ScheduledTasks( PNet pnet){
       // this.vacancyService = vacancyService;
        this.pnet = pnet;
    }
    @Scheduled(fixedRate = 14400000) //runs every 4 hours
    public void crawl(){
        logger.info("Crawl started.");

            ExecutorService executor = Executors.newFixedThreadPool(5);
            for (int i = 0; i < 3; i++) {
                executor.execute(() -> pnet.crawl());
            }
            executor.shutdown();

        }
      //  pnet.crawl();
    //    CareerJunction.crawl();
    //    Careers24.crawl();
    //    JobVine.crawl();

    @Scheduled(fixedRate = 14400000)
    public void deleteExpired(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String advertDate = sdf.format(cal.getTime());
        try {
            cal.setTime(sdf.parse(advertDate));
        //    vacancyService.deleteVacancies(String.valueOf(cal.getTime()));
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
    }

}
