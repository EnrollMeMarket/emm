package knbit.emm.algo.data;

import knbit.emm.model.Course;
import knbit.emm.model.Student;
import knbit.emm.model.Swap;
import knbit.emm.model.UClass;

import java.util.List;

public class MarketData {

    private final List<Course> courses;
    private final List<UClass> uClasses;
    private final List<Student> students;
    private final List<Swap> swaps;

    public MarketData(List<Course> courses, List<UClass> uClasses, List<Student> students, List<Swap> swaps) {
        this.courses = courses;
        this.uClasses = uClasses;
        this.students = students;
        this.swaps = swaps;

    }

    public List<Swap> getSwaps() {
        return swaps;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<UClass> getuClasses() {
        return uClasses;
    }

    public List<Student> getStudents() {
        return students;
    }
}
