package knbit.emm.parser;

import knbit.emm.model.Student;
import knbit.emm.model.UClass;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ParsingTimetablesAlgorithmFromJSONString implements ParsingTimetablesAlgorithm {

    private final String timetableFile;
    private TermsHolder currentTerms;

    public ParsingTimetablesAlgorithmFromJSONString(String timetableFile) {
        this.timetableFile = timetableFile;
    }

    @Override
    public List<Student> parse(TermsHolder currentTerms) {
        this.currentTerms = currentTerms;
        List<Student> students = new LinkedList<>();
        String[] oneStudentsData = timetableFile.split("\\[");
        for (String t : oneStudentsData) {
            if (t.equals("")) continue;
            Student s = analyseForStudentAndTimetable(t);
            students.add(s);
        }
        return students;
    }

    private Student analyseForStudentAndTimetable(String oneStudentsData) {
        Student s = new Student();
        String[] partsOfData = oneStudentsData.split("]");
        String studentId = partsOfData[0];
        String timetableData = partsOfData[1];
        s.setStudentId(studentId);
        List<UClass> timetable = analyseForStudentsTimetable(timetableData);
        s.setTimetable(timetable);
        return s;
    }

    private List<UClass> analyseForStudentsTimetable(String timetableData) {
        List<UClass> timetable = new LinkedList<>();
        String[] terms = timetableData.split("\\n");
        Collection<UClass> classes = currentTerms.getClasses().values();
        for (String t : terms) {
            if (t.equals("")) continue;
            String classId = t.split(":")[1];
            UClass uclass = currentTerms.getClasses().get(classId);
            timetable.add(uclass);
            timetable.addAll(classes.
                    stream().
                    filter(c -> c.getCourse().getCourseId() == uclass.getCourse().getCourseId() && c.isLecture()).
                    collect(Collectors.toList()));
        }
        return timetable;
    }

}
