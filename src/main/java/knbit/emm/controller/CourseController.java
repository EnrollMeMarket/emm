package knbit.emm.controller;

import knbit.emm.service.CourseService;
import knbit.emm.utilities.TokenUtils;
import knbit.emm.model.Course;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    private static Logger log = Logger.getLogger(CourseController.class.getName());
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCourse(
            @RequestBody Course newCourse,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }
        Course course = courseService.saveCourse(newCourse);
        return ResponseUtils.getCreatedOrError(course);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/{courseID}")
    public ResponseEntity getCourseDetails(
            @PathVariable("courseID") String courseID,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Course course = courseService.findCourse(courseID);
        return ResponseUtils.getValueOrNotFound(course);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{courseID}/terms")
    public ResponseEntity getCourseTerms(
            @PathVariable("courseID") String courseID,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }
        Course course = courseService.findCourse(courseID);
        return ResponseUtils.getValueOrNotFound(course);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllCourses(
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<Course> courses = courseService.findAllCourses();
        return ResponseUtils.getValueOrError(courses);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{courseID}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCourse(
            @PathVariable("courseID") String courseID,
            @RequestBody Course course,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        if (courseService.findCourse(courseID) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(courseService.prepareUpdatedCourse(courseID, course), HttpStatus.OK);
    }
}