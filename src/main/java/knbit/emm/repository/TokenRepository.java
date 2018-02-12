package knbit.emm.repository;

import knbit.emm.model.Token;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface TokenRepository extends PagingAndSortingRepository<Token, String> {
}

