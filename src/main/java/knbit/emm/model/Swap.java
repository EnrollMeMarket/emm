package knbit.emm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;

@Entity(name = "Swaps")
public class Swap {

    Swap (){}

    @Id
    @GeneratedValue
    @Column
    private int swapId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;

    @ManyToOne
    @JoinColumn(name = "taken_id")
    private UClass take;

    @ManyToOne
    @JoinColumn(name = "given_id")
    private UClass give;

    @Column
    private boolean done;

    public Swap(Student student, UClass take, UClass give) {
        this.setStudent(student);
        this.setGive(give);
        this.setTake(take);
    }

    public int getSwapId() {
        return swapId;
    }

    public void setSwapId(int swapId) {
        this.swapId = swapId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public UClass getTake() {
        return take;
    }

    public void setTake(UClass take) {
        this.take = take;
    }

    public UClass getGive() {
        return give;
    }

    public void setGive(UClass give) {
        this.give = give;
    }

    @JsonIgnore
    public int getUClassGiveId(){
        return give.getClassId();
    }

    @JsonIgnore
    public int getUClassTakeId(){
        return take.getClassId();
    }

    @JsonIgnore
    public boolean isImproper(){
        if(getTake().getCourseMarketState() != Market.MarketState.OPEN) return true;
        if(!getTake().getPossibilityToSwapFromCourse()) return true;
        Student student = getStudent();
        return !student.getTimetable().contains(getGive());

    }

    @JsonSetter
    public void setId(String id){
        Student student = new Student();
        student.setStudentId(id);
        this.student = student;
    }

    @JsonSetter
    public void setGive(String id){
        UClass give = new UClass();
        give.setClassId(Integer.valueOf(id));
        this.give = give;
    }


    @JsonSetter
    public void setTake(String id){
        UClass take = new UClass();
        take.setClassId(Integer.valueOf(id));
        this.take = take;
    }

    /*@JsonSetter
    public void setSemester(long id){
        Semester sem = new Semester();
        sem.setId(id);
        this.semester = sem;
    }*/
    @JsonProperty(value = "student_id")
    public String getStudentsId() {
        return this.student.getStudentId();
    }

    @Override
    public String toString(){
        return student.getStudentId() + " " + take.getClassId() + " " + give.getClassId();
    }
}
