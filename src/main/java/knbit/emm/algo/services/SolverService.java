package knbit.emm.algo.services;

import knbit.emm.algo.Solver;
import knbit.emm.algo.data.MarketData;
import knbit.emm.model.*;
import knbit.emm.repository.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class SolverService {

    private static final Logger logger = Logger.getLogger(SolverService.class.getName());

    @Autowired
    MarketRepository marketRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UClassRepository uClassRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    SwapRepository swapRepository;

    public void run(Market market) {
        logger.info("Solver started its work.");
        MarketData marketData = getMarketData(market);
        Solver solver = new Solver(market, marketData);
        try {
            Set<Swap> acceptedSwaps = solver.execute().get();
            for (Swap swap: acceptedSwaps) {
                swap.setDone(true);
                Student lucker = swap.getStudent();
                List<UClass> timetable = lucker.getTimetable();
                timetable.remove(swap.getGive());
                timetable.add(swap.getTake());
            }
            market.setWasProcessedByAlgorithm(true);
            marketRepository.save(market);
        } catch (InterruptedException e) {
            logger.error("Interrupted execution of algorithm!", e);
        } catch (ExecutionException e) {
            logger.error("Exception occurred during execution of algorithm!", e);
        }
    }

    @Transactional
    private MarketData getMarketData(Market market) {
        List<Course> courses = market.getCourses();
        List<UClass> uClasses = courses.stream().flatMap(course -> course.getClasses().stream()).collect(Collectors.toList());
        List<Student> students = market.getStudents();
        List<Swap> swaps = swapRepository.findByDone(false);

        return new MarketData(courses, uClasses, students, swaps);
    }
}
