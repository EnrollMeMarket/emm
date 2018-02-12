package knbit.emm.repository;

import knbit.emm.model.Market;
import knbit.emm.model.Swap;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface MarketRepository extends PagingAndSortingRepository<Market, String> {
    @Query("select sw from Markets m join m.students s join s.swaps sw where m.name like :name and sw.done = True")
    List<Swap> findAllDoneSwapsForMarkets(@Param("name") String name);

    @Override
    List<Market> findAll();
}
