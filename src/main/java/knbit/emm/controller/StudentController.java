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
@RequestMapping("/student")
public class StudentController {

    private static Logger log = Logger.getLogger(StudentController.class.getName());
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @CrossOrigin
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

    @CrossOrigin
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
        return new ResponseEntity<>(student.getSwaps(), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/markets")
    public ResponseEntity getStudentMarket(
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
        return new ResponseEntity<>(student.getMarkets(), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/swapsAB")
    public ResponseEntity getStudentSwapsAB (
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
        return new ResponseEntity<>(studentService.findPossibleSwapsAB(student), HttpStatus.OK);
    }

    @CrossOrigin
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
        if (student == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllStudents(
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        if(isNotValidToken.isPresent()){
            return isNotValidToken.get();
        }

        Iterable<Student> students = studentService.findAllStudents();
        if (students == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postStudent(
            @RequestBody Student newStudent,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidAdminOrStarostaToken(token);
        return isNotValidToken.orElseGet(() -> new ResponseEntity<>(studentService.createNewStudent(newStudent.getStudentId()), HttpStatus.CREATED));
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "/{studentId}/timetable/{classId}")
    public ResponseEntity getPossibleClassesToSwap(
            @PathVariable("studentId") String studentId,
            @PathVariable("classId") int classId,
            @RequestHeader(value="Authorization") String token
    ) {
        Optional<ResponseEntity> isNotValidToken = TokenUtils.prepareResponseEntityIfInvalidStudentToken(token, studentId);
        return isNotValidToken.orElseGet(() -> new ResponseEntity<>(studentService.findPossibleUClassToSwapFor(studentId, classId), HttpStatus.OK));
    }

    @CrossOrigin
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

        try {
            Boolean isChosen = studentService.checkTermChosen(studentId, classId, classToCheckId);
            if(isChosen == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(isChosen, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}