package knbit.emm.parser;

import knbit.emm.model.Course;
import knbit.emm.model.UClass;

import java.util.Map;

public class TermsHolder {
    private Map<String, UClass> classes;
    private Map<String, Course> courses;

    public TermsHolder(Map<String, UClass> classes, Map<String, Course> courses) {
        this.classes = classes;
        this.courses = courses;
    }

    public Map<String, UClass> getClasses() {
        return classes;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }
}
