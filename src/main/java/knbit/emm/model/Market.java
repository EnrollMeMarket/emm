package knbit.emm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "Markets")
public class Market {

    public static final int NO_DATE = -1;
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "student_market",
            joinColumns = {@JoinColumn(name = "market_id")},
            inverseJoinColumns = {@JoinColumn(name = "student_id")})
    private final List<Student> students = new LinkedList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "market")
    private final List<Course> courses = new LinkedList<>();
    @JsonIgnore
    private boolean wasProccessedByAlgorithm = false;

    @Id
    @Column
    private String name;

    @Column
    @JsonIgnore
    private Timestamp beginningTime;

    @Column
    @JsonIgnore
    private Timestamp endTime;

    @Column
    @Enumerated(EnumType.STRING)
    private MarketState state = MarketState.CLOSED;

    @Column
    private String createdBy;

    public Market() {
    }

    public Market(String name, long begDate, long endDate, String createdBy) {
        this.setName(name);
        if (begDate == 0) this.setState(MarketState.OPEN);
        this.setBeginningTime(provideProperBegTimeForMarket(begDate));
        this.setEndTime(provideProperEndTimeForMarket(endDate));
        this.setCreatedBy(createdBy);
    }

    static Timestamp provideProperBegTimeForMarket(long begDate) {
        if (begDate > 0) {
            return new Timestamp(begDate);
        }

        if (begDate == 0) {
            return new Timestamp(System.currentTimeMillis());
        }

        return null;
    }

    static Timestamp provideProperEndTimeForMarket(long endDate) {
        if (endDate > 0) {
            return new Timestamp(endDate);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Timestamp getBeginningTime() {
        return beginningTime;
    }

    public void setBeginningTime(Timestamp beginningTime) {
        this.beginningTime = beginningTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public MarketState getState() {
        return state;
    }

    public void setState(MarketState state) {
        this.state = state;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty(value = "end")
    public long getProperEndTime() {

        if (this.getEndTime() == null) {
            return NO_DATE;
        }

        return this.getEndTime().getTime();
    }

    @JsonProperty(value = "beg")
    public long getProperBegTime() {
        if (this.getBeginningTime() == null) {
            return NO_DATE;
        }

        return this.getBeginningTime().getTime();
    }

    public void setWasProcessedByAlgorithm(boolean wasProccessedByAlgorithm) {
        this.wasProccessedByAlgorithm = wasProccessedByAlgorithm;
    }

    public enum MarketState {
        OPEN,
        CLOSED,
        FINISHED
    }
}
