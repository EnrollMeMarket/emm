package knbit.emm.model.dto;

import knbit.emm.model.UClass;

public class UClassForStudent extends UClass {
    private boolean isChosen;
    private boolean isAvailable;

    public UClassForStudent(UClass uClass, boolean isChosen, boolean isAvailable){
        super(uClass.getClassId(),uClass.getCourse(),uClass.getHost(),uClass.getEndTime(),uClass.getWeekday(),uClass.getWeek(),uClass.getBegTime(),uClass.isLecture());
        this.isChosen = isChosen;
        this.isAvailable = isAvailable;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
