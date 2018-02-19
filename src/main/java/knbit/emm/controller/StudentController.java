package knbit.emm.controller;

import knbit.emm.service.StudentService;
import knbit.emm.utilities.TokenUtils;
import knbit.emm.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private static Logger log = Logger.getLogger(StudentController.class.getName());
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/timetable")
    public ResponseEntity getStudentTimetable(
            @PathVariable("studentId") String studentId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, studentId);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Student student = studentService.findStudent(studentId);
        if (student == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(studentService.findStudentTimetable(student), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/swaps")
    public ResponseEntity getStudentSwaps(
            @PathVariable("studentId") String studentId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, studentId);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Student student = studentService.findStudent(studentId);

        if (student == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseUtils.getValueOrError(student.getSwaps());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}")
    public ResponseEntity getStudent(
            @PathVariable("studentId") String studentId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, studentId);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Student student = studentService.findStudent(studentId);
        return ResponseUtils.getValueOrError(student);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllStudents(
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<Student> students = studentService.findAllStudents();
        return ResponseUtils.getValueOrError(students);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postStudent(
            @RequestBody Student newStudent,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        return isNotValidToken.orElseGet(() -> new ResponseEntity<>(studentService.createNewStudent(newStudent.getStudentId()), HttpStatus.CREATED));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/timetable/{classId}")
    public ResponseEntity getPossibleClassesToSwap(
            @PathVariable("studentId") String studentId,
            @PathVariable("classId") int classId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, studentId);
        return isNotValidToken.orElseGet(() -> new ResponseEntity<>(studentService.findPossibleUClassToSwapFor(studentId, classId), HttpStatus.OK));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/timetable/{classId}/{classToCheckId}/chosen")
    public ResponseEntity checkTermIfChosen(
            @PathVariable("studentId") String studentId,
            @PathVariable("classId") int classId,
            @PathVariable int classToCheckId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, studentId);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }
        Boolean isChosen = studentService.checkTermChosen(studentId, classId, classToCheckId);
        return ResponseUtils.getValueOrBadRequest(isChosen);
    }
}