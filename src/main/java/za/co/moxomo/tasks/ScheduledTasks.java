package za.co.moxomo.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import za.co.moxomo.crawlers.FirstRand;
import za.co.moxomo.crawlers.MrPrice;
import za.co.moxomo.crawlers.PNet;

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

    private PNet pnet;
    private FirstRand firstRand;
    private MrPrice mrPrice;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

   @Autowired
    public ScheduledTasks(PNet pnet, FirstRand firstRand, MrPrice mrPrice){
        this.pnet = pnet;
        this.firstRand=firstRand;
        this.mrPrice=mrPrice;
    }
    @Scheduled(fixedRate = 14400000) //runs every 4 hours
    public void crawl(){
        logger.info("Crawl started.");
          ExecutorService executor = Executors.newFixedThreadPool(5);
            executor.execute(() -> mrPrice.crawl());

          //  executor.shutdown();

        }


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
