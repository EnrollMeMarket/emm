package knbit.emm.repository;

import knbit.emm.model.Course;
import knbit.emm.model.DoneSwap;
import knbit.emm.model.Market;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface DoneSwapRepository extends CrudRepository<DoneSwap, Long>{

    List<DoneSwap> findByCourse(Course course);

    int countByCourse_Market(Market market);

}
