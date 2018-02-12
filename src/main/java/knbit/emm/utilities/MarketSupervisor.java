package knbit.emm.utilities;

import knbit.emm.model.Market;
import knbit.emm.repository.MarketRepository;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import static knbit.emm.model.Market.MarketState;

@Component
public class MarketSupervisor {

    private static final Logger log = Logger.getLogger(MarketSupervisor.class.getName());
    @Autowired
    private MarketRepository marketRepository;

    @Value("${emm.tmp-folder}")
    private String tmpFolder;

    @Scheduled(cron = "2 * * * * *")
    public void controlMarketState(){
        Iterable<Market> markets = marketRepository.findAll();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        for(Market market: markets){
            switch (market.getState()){
                case CLOSED:
                    if (market.getBeginningTime() != null && currentTime.after(market.getBeginningTime())) {
                        market.setState(MarketState.OPEN);
                    } else if(market.getEndTime() != null && currentTime.after(market.getEndTime())) {
                        market.setState(MarketState.FINISHED);
                    }
                    break;
                case OPEN:
                    if (market.getEndTime() != null && currentTime.after(market.getEndTime())) {
                        market.setState(MarketState.FINISHED);
                    }
                    break;
                case FINISHED:
                    break;
            }
            marketRepository.save(market);
        }
        log.info("Scheduled control of the markets' states is finished");
    }

    @Scheduled(cron = "5 * * * * *")
    public void controlTmpFolder() throws IOException {
        final int oneMegaByteInBytes = 1048576;

        File folder = new File(tmpFolder);

        if (!folder.exists()) {
            folder.mkdirs();
            log.info("Created folder: " + folder.getAbsolutePath());
        }

        if (folder.listFiles().length > 0) {
            log.info(tmpFolder + " files: " + folder.listFiles().length);
            log.info("Files to be removed:");

            for (File file : folder.listFiles()) {
                log.info("File name: " + file.getName() + ", size: " + file.length() / oneMegaByteInBytes + " megabytes");
            }

            FileUtils.cleanDirectory(folder);
            log.info(tmpFolder + " has been cleaned");
        }
    }
}