package knbit.emm.service;


import knbit.emm.model.*;
import knbit.emm.model.dto.SwapSummary;
import knbit.emm.repository.DoneSwapRepository;
import knbit.emm.repository.StudentRepository;
import knbit.emm.repository.SwapRepository;
import knbit.emm.repository.UClassRepository;
import knbit.emm.utilities.ConflictFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SwapService {

    private final SwapRepository swapRepository;
    private final StudentRepository studentRepository;
    private final UClassRepository uClassRepository;
    private final DoneSwapRepository doneSwapRepository;

    @Autowired
    public SwapService(SwapRepository swapRepository, StudentRepository studentRepository, UClassRepository uClassRepository, DoneSwapRepository doneSwapRepository) {
        this.swapRepository = swapRepository;
        this.studentRepository = studentRepository;
        this.uClassRepository = uClassRepository;
        this.doneSwapRepository = doneSwapRepository;
    }

    public Swap findSwap(int swapId) {
        return swapRepository.findOne(swapId);
    }

    public Iterable<Swap> findAllSwaps() {
        return swapRepository.findAll();
    }

    public Iterable<Swap> getAllDone() {
        return swapRepository.findByDone(true);
    }

    public void deleteSwap(Swap swap) {
        swapRepository.delete(swap);
    }

    private void deleteSwaps (List<Swap> swaps){
        swapRepository.delete(swaps);
    }

    List<Swap> findPossibleSwaps(int giveId, int takeId) {
        return swapRepository.findAllByGive_classIdAndTake_classIdAndDone(giveId, takeId, false);
    }

    public boolean isPossibleSwap(Swap swapCheck){
        return findMatchingSwap(swapCheck).isPresent();
    }

    public SwapSummary handleManySwapsAndCreateResponse(List<Swap> swaps) {
        SwapSummary swapSummary = new SwapSummary();
        for(Swap postedSwap: swaps){
            Optional<Swap> swap = handleSingleSwap(postedSwap);
            if (!swap.isPresent()) {
                swapSummary.addBrokenSwap(postedSwap);
            } else if(swap.get().isDone()){
                swapSummary.addDoneSwap(swap.get());
            } else{
                swapSummary.addSavedSwap(swap.get());
            }
        }
        return swapSummary;
    }

    public Optional<Swap> handleSingleSwap(Swap postSwaps){
        Swap swap = prepareSwapFromDatabase(postSwaps);
        if (swap.isImproper()) {
            return Optional.empty();
        }else {
            operateOnSwap(swap);
        }
        return Optional.of(swap);
    }

    private Swap prepareSwapFromDatabase(Swap swap) {
        Student student = studentRepository.findByStudentId(swap.getStudentsId());
        UClass take = uClassRepository.findOne(swap.getTake().getClassId());
        UClass give = uClassRepository.findOne(swap.getGive().getClassId());
        return new Swap(student, take, give);
    }

    private void operateOnSwap(Swap swap) {
        Optional<Swap> optSwap = findMatchingSwap(swap);
        if (optSwap.isPresent()) {
            handleDoneSwap(swap, optSwap.get());
        }
        else{
            swap.setDone(false);
            swapRepository.save(swap);
        }
    }

    private Optional<Swap> findMatchingSwap(Swap swapCheck) {
        List<Swap> toGive = swapCheck.getTake().getSwapsGive();
        for (Swap swap: toGive) {
            if (swap.getTake().equals(swapCheck.getGive()) && !swap.isDone()) {
                return Optional.of(swap);
            }
        }
        return Optional.empty();
    }

    private void handleDoneSwap(Swap swapOne, Swap swapTwo) {
        updateSwapWhenDone(swapOne);
        updateSwapWhenDone(swapTwo);
        DoneSwap doneSwap = new DoneSwap(swapOne, swapTwo);
        doneSwapRepository.save(doneSwap);
    }

    private void updateSwapWhenDone(Swap swap){
        swap.setDone(true);
        removeNeedlessSwapsAndUpdateTimetable(swap);
        swapRepository.save(swap);
    }

    private void removeNeedlessSwapsAndUpdateTimetable(Swap swap){
        changeTimetable(swap);
        studentRepository.save(swap.getStudent());
    }

    private void changeTimetable(Swap swap) {
        List <UClass> allClass = swap.getStudent().getTimetable();
        allClass.remove(swap.getGive());
        allClass.add(swap.getTake());
        deleteSwapWithThisTerm(swap);
    }

    private void deleteSwapWithThisTerm(Swap swap) {
        List<Swap> toDelete = createSwapToDelete(swap.getStudent(), swap.getGive(), swap.getTake());
        swap.getStudent().deleteSwap(toDelete);
        swapRepository.delete(toDelete);
    }

    private List<Swap> createSwapToDelete(Student student, UClass classGiven, UClass classTaken){
        List<Swap> swaps = student.getSwaps();
        Set<Swap> toDelete = new HashSet<>();
        swaps.stream().filter(swap -> swap.getGive().equals(classGiven) && !swap.isDone()).forEach(toDelete::add);
        swaps.stream().filter(swap -> ConflictFinder.doesTermsCollide(swap.getTake(), classTaken) && !swap.isDone()).forEach(toDelete::add);
        return new ArrayList<>(toDelete);
    }

    public SwapSummary deleteManySwapsAndCreateResponse(List<Swap> swapList) {
        SwapSummary swapSummary = new SwapSummary();
        for (Swap toDelete : swapList) {
            swapSummary.addDoneSwaps(getMatchingSwaps(toDelete, true));
            List <Swap> swaps = getMatchingSwaps(toDelete, false);
            deleteSwaps(swaps);
            swapSummary.addDeletedSwaps(swaps);
        }
        return swapSummary;
    }

    private List<Swap> getMatchingSwaps(Swap swap, boolean done) {
        return swapRepository.findByStudent_studentIdAndTake_classIdAndGive_classIdAndDone(swap.getStudentsId(), swap.getUClassTakeId(), swap.getUClassGiveId(), done);
    }
}
