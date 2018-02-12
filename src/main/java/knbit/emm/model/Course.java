package knbit.emm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "Courses")
public class Course {

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private final List<UClass> classes = new ArrayList<>(20);
    @Id
    @Column
    private int courseId;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "market_id")
    private Market market;

    @Column
    private int semester;
    @Column
    private String title;

    @Column
    private boolean openForChanges = false;

    @Override
    public boolean equals(Object other) {
        return other != null && (other == this || other instanceof Course && this.getCourseId() == ((Course) other).getCourseId());
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseID) {
        this.courseId = courseID;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<UClass> getClasses() {
        return classes;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    @JsonProperty(value = "market_name")
    public String getMarketName() {
        return this.market.getName();
    }

    @JsonSetter
    public void setMarketName(String marketName){
        Market market = new Market();
        market.setName(marketName);
        this.market = market;
    }

    public boolean isOpenForChanges() {
        return openForChanges;
    }

    public void setOpenForChanges(boolean openForChanges) {
        this.openForChanges = openForChanges;
    }

    @JsonIgnore
    public Market.MarketState getMarketState() {
        return getMarket().getState();
    }
}


