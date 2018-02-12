package knbit.emm.model.dto;


import knbit.emm.model.Swap;

import java.util.LinkedList;
import java.util.List;

public class SwapSummary {
    private List<Swap> doneSwaps = new LinkedList<>();
    private List<Swap> savedSwaps = new LinkedList<>();
    private List<Swap> brokenSwaps = new LinkedList<>();
    private List<Swap> deletedSwaps = new LinkedList<>();


    public List<Swap> getDoneSwaps() {
        return doneSwaps;
    }

    public void setDoneSwaps(List<Swap> doneSwaps) {
        this.doneSwaps = doneSwaps;
    }

    public List<Swap> getSavedSwaps() {
        return savedSwaps;
    }

    public void setSavedSwaps(List<Swap> savedSwaps) {
        this.savedSwaps = savedSwaps;
    }

    public List<Swap> getBrokenSwaps() {
        return brokenSwaps;
    }

    public void setBrokenSwaps(List<Swap> brokenSwaps) {
        this.brokenSwaps = brokenSwaps;
    }

    public List<Swap> getDeletedSwaps() {
        return deletedSwaps;
    }

    public void setDeletedSwaps(List<Swap> deletedSwaps) {
        this.deletedSwaps = deletedSwaps;
    }

    public void addDeletedSwaps(List<Swap> swaps){
        deletedSwaps.addAll(swaps);
    }

    public void addDoneSwaps(List<Swap> swaps){
        doneSwaps.addAll(swaps);
    }
    public void addDoneSwap(Swap swap){
        doneSwaps.add(swap);
    }

    public void addBrokenSwap(Swap swap){
        brokenSwaps.add(swap);
    }

    public void addSavedSwap(Swap swap){
        savedSwaps.add(swap);
    }

}
