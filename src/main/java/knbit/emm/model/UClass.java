package knbit.emm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Time;
import java.util.List;

@Entity(name = "Classes")
public class UClass {

    public UClass(){}

    public UClass(int classId, Course course, String host, Time endTime, String weekday, String week, Time begTime, boolean lecture) {
        this.classId = classId;
        this.course = course;
        this.host = host;
        this.endTime = endTime;
        this.weekday = weekday;
        this.week = week;
        this.begTime = begTime;
        this.lecture = lecture;
    }

    @Id
    @Column
    private int classId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @Column
    private String host;

    @Column
    private Time endTime;

    @Column
    private String weekday;

    @Column
    private String week;

    @Column
    private Time begTime;

    @Column
    private boolean lecture;

    @OneToMany
    @JoinColumn(name = "given_id")
    @JsonIgnore
    private List<Swap> swapsGive;

    @OneToMany
    @JoinColumn(name = "taken_id")
    @JsonIgnore
    private List<Swap> swapsTake;

    @Override
    public boolean equals(Object other) {
        return other != null && (other == this || other instanceof UClass && this.getClassId() == ((UClass) other).getClassId());
    }

    public void addSwapGive(Swap swap) {
        swapsGive.add(swap);
    }

    public void addSwapTake(Swap swap) {
        swapsTake.add(swap);
    }

    public List<Swap> getSwapsGive() {
        return swapsGive;
    }

    public List<Swap> getSwapsTake() {
        return swapsTake;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void deleteSwapGive(Swap toDelete) {
        swapsGive.remove(toDelete);
    }

    public void deleteSwapTake(Swap toDelete) {
        swapsTake.remove(toDelete);
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public Time getBegTime() {
        return begTime;
    }

    public void setBegTime(Time begTime) {
        this.begTime = begTime;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public boolean isLecture() {
        return lecture;
    }

    public void setLecture(boolean lecture) {
        this.lecture = lecture;
    }

    @JsonIgnore
    public Market.MarketState getCourseMarketState(){
        return getCourse().getMarketState();
    }

    @JsonProperty(value = "course_id")
    public int getCourseId() {
        return this.course.getCourseId();
    }

    @JsonProperty(value = "title")
    public String getCourseName() {
        return this.course.getTitle();
    }

    @JsonProperty(value = "to_swap")
    public boolean getPossibilityToSwapFromCourse() {
        return this.course.isOpenForChanges();
    }
}
