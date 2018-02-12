package knbit.emm.service;

import knbit.emm.model.*;
import knbit.emm.model.dto.UClassForStudent;
import knbit.emm.repository.StudentRepository;
import knbit.emm.utilities.ConflictFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService{
    private final SwapService swapService;
    private final StudentRepository studentRepository;
    private final UClassService uClassService;

    @Autowired
    public StudentService(SwapService swapService, StudentRepository studentRepository, UClassService uClassService) {
        this.swapService = swapService;
        this.studentRepository = studentRepository;
        this.uClassService = uClassService;
    }

    public Student createNewStudent(String id){
        Student student = new Student();
        student.setStudentId(id);
        return save(student);
    }

    public Student findStudent(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public Iterable<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public List<UClass> findStudentTimetable(Student student) {
        return  student.getTimetable().stream()
                .filter(uClass -> uClass.getCourseMarketState() == Market.MarketState.OPEN)
                .collect(Collectors.toList());
    }

    public Boolean checkTermChosen(String studentId, int classId, int classToCheckId){
        Student s = findStudent(studentId);
        UClass studentsClass = uClassService.findClass(classId);
        UClass toCheckClass = uClassService.findClass(classToCheckId);
        if (studentsClass.getCourseId() != toCheckClass.getCourseId()) {
            return null;
        }
        return s.getClassesToTake().contains(toCheckClass);
    }

    public List<UClassForStudent> findPossibleUClassToSwapFor (String studentId, int classId){
        Student student = findStudent(studentId);
        UClass uclass = uClassService.findClass(classId);
        if (student == null || uclass == null) {
            return new ArrayList<>();
        }
        Course course = uclass.getCourse();
        return findPossibleToChangeCourseTermsForStudent(course, student);
    }

    private List<UClassForStudent> findPossibleToChangeCourseTermsForStudent(Course course, Student s) {
        return course
                .getClasses()
                .stream()
                .filter(courseClass -> !ConflictFinder.isTermCollidingWithStudentTimeTable(s.getTimetable(), courseClass))
                .map(e -> new UClassForStudent(e, isClassAlreadyInSwaps(s, e), isAvailable(s.getTimetable(), e)))
                .collect(Collectors.toList());
    }

    private boolean isClassAlreadyInSwaps(Student s, UClass e) {
        List<UClass> uClasses = s.getSwaps().stream().
                filter(swap -> !swap.isDone()).
                map(Swap::getTake).collect(Collectors.toList());
        return uClasses.contains(e);
    }

    private boolean isAvailable(List<UClass> studentTimetable, UClass c) {
        List<UClass> uClasses = studentTimetable
                .stream()
                .filter(e -> !e.isLecture() && e.getCourseId() == c.getCourseId())
                .collect(Collectors.toList());
        if(uClasses.isEmpty()) {
            return false;
        }
        List<Swap> swaps = swapService.findPossibleSwaps(c.getClassId(), uClasses.get(0).getClassId());
        return !swaps.isEmpty();
    }

    public List<Swap> findPossibleSwapsAB (Student student){
        List <UClass> studentClassAB = student.getTimetable().stream().filter(e -> !e.getWeek().equals("all")).collect(Collectors.toList());
        return findAllUClassToSwapAB(studentClassAB);
    }

    private List<Swap> findAllUClassToSwapAB(List<UClass> studentPossibleClassAB) {
        List <Swap> possibleSwaps = new ArrayList<>();
        List <UClass> possibleClassesToSwap;
        for (UClass studentUClass: studentPossibleClassAB) {
            possibleClassesToSwap = getUClassToSwap(studentPossibleClassAB, studentUClass);
            for (UClass possibleClass: possibleClassesToSwap) {
                possibleSwaps.addAll(swapService.findPossibleSwaps(possibleClass.getClassId(), studentUClass.getClassId()));
            }
        }
        return possibleSwaps;
    }

    private List<UClass> getUClassToSwap(List<UClass> studentPossibleClassAB, UClass uclass){
        return uclass.getCourse()
                .getClasses()
                .stream()
                .filter(courseClass -> checkPossibilityToSwap(studentPossibleClassAB, uclass))
                .collect(Collectors.toList());
    }

    private boolean checkPossibilityToSwap(List<UClass> studentPossibleClassAB, UClass uclass) {
        return !ConflictFinder.isTermCollidingWithStudentTimeTable(studentPossibleClassAB, uclass) && isAvailable (studentPossibleClassAB, uclass);
    }


}
