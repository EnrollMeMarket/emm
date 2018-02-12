package knbit.emm.repository;

import knbit.emm.model.Swap;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface SwapRepository extends PagingAndSortingRepository<Swap, Integer> {
    List<Swap> findByDone (boolean done);
    List<Swap> findByStudent_studentIdAndTake_classIdAndGive_classIdAndDone(String studentId, int takeId, int giveId, boolean done);
    List<Swap> findAllByGive_classIdAndTake_classIdAndDone(int giveId, int takeId, boolean done);

}
