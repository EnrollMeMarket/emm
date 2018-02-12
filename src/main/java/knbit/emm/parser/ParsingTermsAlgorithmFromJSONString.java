package knbit.emm.parser;

import knbit.emm.model.Course;
import knbit.emm.model.UClass;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class ParsingTermsAlgorithmFromJSONString implements ParsingTermsAlgorithm {
    private String terms;
    private Map<String, Course> courses;
    private Map<String, UClass> classes;

    public ParsingTermsAlgorithmFromJSONString(String terms) {
        this.terms = terms;
    }

    @Override
    public TermsHolder parse() {
        courses = new HashMap<>();
        classes = new HashMap<>();
        String[] termsLine = terms.split("\\n");
        for (String line : termsLine) analyseForCourse(line);
        return new TermsHolder(classes, courses);
    }

    private void analyseForCourse(String line) {
        String[] firstPartition = line.split(":", 2);
        String[] parts = firstPartition[1].split(";");
        String courseId = firstPartition[0];
        if (!courses.containsKey(courseId)) {
            Course course = new Course();
            course.setCourseId(Integer.valueOf(courseId));
            course.setTitle(parts[1]);
            courses.put(courseId, course);
        }
        UClass uClass = analyseForClass(parts);
        uClass.setCourse(courses.get(courseId));
    }

    private UClass analyseForClass(String[] parts) {
        UClass uClass = new UClass();
        classes.put(parts[0], uClass);
        uClass.setClassId(Integer.valueOf(parts[0]));
        if (parts[2].equals("")) uClass.setLecture(true);
        else uClass.setLecture(false);
        analyseForTime(uClass, parts[3]);
        uClass.setHost(parts[4]);
        return uClass;
    }

    private void analyseForTime(UClass uClass, String date) {
        String[] info = date.split(",");
        analyseForWeekdayAndWeek(uClass, info[0]);
        analyseForHours(uClass, info[1]);
    }

    private void analyseForHours(UClass uClass, String s) {
        String[] hours = s.substring(1).split("-");
        uClass.setBegTime(castTimeStringToSqlTime(hours[0]));
        uClass.setEndTime(castTimeStringToSqlTime(hours[1]));
    }

    private void analyseForWeekdayAndWeek(UClass uClass, String s) {
        String[] info = s.split(" ");
        uClass.setWeekday(info[0]);
        uClass.setWeek(info[1].replace("(", "").replace(")", ""));
    }

    private Time castTimeStringToSqlTime(String time) {
        String[] timeSplitByUnits = time.split(":");
        return new Time(((Long.parseLong(timeSplitByUnits[0]) * 3600) + (60 * Long.parseLong(timeSplitByUnits[1]))) * 1000);
    }


}
