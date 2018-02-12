package knbit.emm.service;


import knbit.emm.model.Course;
import knbit.emm.model.Market;
import knbit.emm.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final MarketService marketService;

    @Autowired
    public CourseService(CourseRepository courseRepository, MarketService marketService) {
        this.courseRepository = courseRepository;
        this.marketService = marketService;
    }

    public Course saveCourse(Course course) {
        Market market = marketService.findMarket(course.getMarketName());
        course.setMarket(market);
        courseRepository.save(course);
        return course;
    }

    public Course prepareUpdatedCourse(String courseID, Course course) {
        Course newCourse = saveCourse(course);
        newCourse.setCourseId(Integer.parseInt(courseID));
        courseRepository.save(newCourse);
        return newCourse;
    }

    public Course findCourse(String id) {
        return courseRepository.findOne(Integer.valueOf(id));
    }

    public Iterable<Course> findAllCourses (){
        return courseRepository.findAll();
    }

    public List<Course> findCoursesInMarket(String name) {
        return courseRepository.findByMarket_Name(name);
    }
}
