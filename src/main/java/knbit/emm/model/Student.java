package knbit.emm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "Students")
public class Student {

    @Id
    @Column
    private String studentId;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "student")
    private List<Swap> swaps;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private List<Market> markets = new LinkedList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "timetable",
            joinColumns = {@JoinColumn(name = "student_id")},
            inverseJoinColumns = {@JoinColumn(name = "class_id")})
    private List<UClass> timetable;

    @JsonIgnore
    @OneToOne
    private Token token;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<Swap> getSwaps() {
        return swaps;
    }

    @JsonIgnore
    public List<UClass> getTimetable() {
        return timetable;
    }

    public void setTimetable(List<UClass> timetable) {
        this.timetable = timetable;
    }

    public void deleteSwap(List<Swap> toDelete) {
        swaps.removeAll(toDelete);
    }

    public void deleteSwap(Swap toDelete) {
        swaps.remove(toDelete);
    }

    public void setSwaps(List<Swap> swaps) {
        this.swaps = swaps;
    }

    public List<Market> getMarkets() {
        return markets;
    }

    public void setMarkets(List<Market> markets) {
        this.markets = markets;
    }

    @JsonIgnore
    public List<UClass> getClassesToTake() {
        return swaps.stream().map(Swap::getTake).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return studentId.equals(student.studentId);
    }

    @Override
    public int hashCode() {
        return studentId.hashCode();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
