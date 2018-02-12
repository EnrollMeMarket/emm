package knbit.emm.parser;


import knbit.emm.model.Course;
import knbit.emm.model.Market;
import knbit.emm.model.Student;
import knbit.emm.repository.CourseRepository;
import knbit.emm.repository.StudentRepository;
import knbit.emm.repository.UClassRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EnrollOutputParser {

    private static final Logger log = Logger.getLogger(EnrollOutputParser.class.getName());

    private final CourseRepository courseRepository;

    private final UClassRepository uClassRepository;

    private final StudentRepository studentRepository;

    private ParsingTermsAlgorithm termsAlgorithm;
    private ParsingTimetablesAlgorithm timetableAlgorithm;

    @Autowired
    public EnrollOutputParser(CourseRepository courseRepository, UClassRepository uClassRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.uClassRepository = uClassRepository;
        this.studentRepository = studentRepository;
    }

    public void parse(Market market, List<String> activeCourses) {
        TermsHolder holder = termsAlgorithm.parse();
        List<Student> students = timetableAlgorithm.parse(holder);
        Collection<Course> courses = holder.getCourses().values();
        courses.forEach(c -> c.setMarket(market));
        courses.stream().filter(c -> activeCourses.contains(c.getTitle())).forEach(c -> c.setOpenForChanges(true));
        courseRepository.save(courses);
        uClassRepository.save(holder.getClasses().values());
        students.forEach(c -> {
            Student s = studentRepository.findByStudentId(c.getStudentId());
            if(s!=null){
                s.getMarkets().add(market);
                s.getTimetable().addAll(c.getTimetable());
                market.getStudents().add(s);
                studentRepository.save(s);
            }
            else{
                c.getMarkets().add(market);
                market.getStudents().add(c);
                studentRepository.save(c);
            }
        });
        log.info("The parser has finished its work.");
    }

    public void setAlgorithms(ParsingTermsAlgorithm termsAlgorithm, ParsingTimetablesAlgorithm timetableAlgorithm) {
        this.termsAlgorithm = termsAlgorithm;
        this.timetableAlgorithm = timetableAlgorithm;
    }

    public void setTermsAlgorithm(ParsingTermsAlgorithm termsAlgorithm) {
        this.termsAlgorithm = termsAlgorithm;
    }

    public void setTimetableAlgorithm(ParsingTimetablesAlgorithm timetableAlgorithm) {
        this.timetableAlgorithm = timetableAlgorithm;
    }

}
