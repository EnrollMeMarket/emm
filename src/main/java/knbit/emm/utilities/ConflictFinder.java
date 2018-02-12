package knbit.emm.utilities;

import knbit.emm.model.UClass;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

public class ConflictFinder {

    public static boolean isTermCollidingWithStudentTimeTable(List<UClass> studentsClasses, UClass courseClass){
        for (UClass studentClass : studentsClasses) {
            if(doesTermsCollide(studentClass, courseClass)) return true;
        }
        return false;

    }

    public static boolean doesTermsCollide(UClass studentClass, UClass courseClass) {
        if (courseClass.isLecture()) return true;
        if (studentClass.getWeekday().equals(courseClass.getWeekday())) {
            if (isSameCourseAndSameDateDifferentEmployee(studentClass, courseClass)) return false;
                if (!doesWeekCollide(studentClass.getWeek(), courseClass.getWeek())) return false;
                if (isTimeConflicted(courseClass, studentClass)) return true;
            }
        return false;
    }

    private static boolean isSameCourseAndSameDateDifferentEmployee(UClass studentClass, UClass courseClass) {
        if(!isSameCourse(studentClass, courseClass)) return false;
        if(studentClass.getHost().equalsIgnoreCase(courseClass.getHost())) return false;
        if(!studentClass.getWeek().equals(courseClass.getWeek())) return false;
        return isSameTime(studentClass, courseClass);
    }

    private static boolean doesWeekCollide(String week, String weekToBeCheckedForCollision){
        if (week.equals("A") && weekToBeCheckedForCollision.equals("B"))
            return false;
        else if (week.equals("B") && weekToBeCheckedForCollision.equals("A"))
            return false;
        else if (week.equals("1") && weekToBeCheckedForCollision.equals("2"))
            return false;
        else return !(week.equals("2") && weekToBeCheckedForCollision.equals("1"));
    }

    private static boolean isTimeConflicted(UClass courseClass, UClass studentClass) {
        if(isSameTime(courseClass, studentClass)) return true;
        if(isBeginningTimeBetweenBeginningAndEndOfOther(studentClass.getBegTime(), courseClass)) return true;
        if(isEndTimeBetweenBeginningAndEndOfOtherTimeSlot(studentClass.getEndTime(), courseClass)) return true;
        if (isBetweenTimes(studentClass.getBegTime(), studentClass.getEndTime(), courseClass.getBegTime())) return true;
        if (isBetweenTimes(studentClass.getBegTime(), studentClass.getEndTime(), courseClass.getEndTime())) return true;
        return false;
    }

    private static boolean isSameTime(UClass courseClass, UClass studentClass){
        if(studentClass.getBegTime().getTime() != courseClass.getBegTime().getTime()) return false;
        if(studentClass.getEndTime().getTime() != courseClass.getEndTime().getTime()) return false;
        return true;
    }

    private static boolean isTimeBetweenBeginningAndEndOfTimeSlot(Time toCheck, UClass toBeCheckedOn){
        return (toCheck.getTime() >= toBeCheckedOn.getBegTime().getTime() && toCheck.getTime() <= toBeCheckedOn.getEndTime().getTime());
    }

    private static boolean isBetweenTimes(Time beg, Time end, Time toCheck) {
        return beg.getTime() <= toCheck.getTime() && end.getTime() >= toCheck.getTime();
    }


    private static boolean isBeginningTimeBetweenBeginningAndEndOfOther(Time beginningTime, UClass toBeCheckedOn){
        return isTimeBetweenBeginningAndEndOfTimeSlot(beginningTime,toBeCheckedOn);
    }

    private static boolean isEndTimeBetweenBeginningAndEndOfOtherTimeSlot(Time endTime, UClass toBeCheckedOn){
        return isTimeBetweenBeginningAndEndOfTimeSlot(endTime,toBeCheckedOn);
    }

    private static boolean isSameCourse(UClass courseClass, UClass studentClass) {
        return studentClass.getCourse().getCourseId() == courseClass.getCourse().getCourseId();
    }
}
