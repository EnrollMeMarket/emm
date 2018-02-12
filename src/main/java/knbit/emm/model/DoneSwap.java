package knbit.emm.model;

import javax.persistence.*;

@Entity(name = "Done_Swaps")
public class DoneSwap {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(cascade = CascadeType.DETACH)
    private Swap swapOne;

    @OneToOne(cascade = CascadeType.DETACH)
    private Swap swapTwo;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Course course;

    public DoneSwap() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Swap getSwapOne() {
        return swapOne;
    }

    public void setSwapOne(Swap swapOne) {
        this.swapOne = swapOne;
    }

    public Swap getSwapTwo() {
        return swapTwo;
    }

    public void setSwapTwo(Swap swapTwo) {
        this.swapTwo = swapTwo;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public DoneSwap (Swap first, Swap second){
        this.setSwapOne(first);
        this.setSwapTwo(second);
        this.setCourse(first.getGive().getCourse());
    }
}
