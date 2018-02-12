package knbit.emm.repository;

import knbit.emm.model.UClass;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.List;

@Transactional
@Repository
public interface UClassRepository extends PagingAndSortingRepository<UClass, Integer> {
   List<UClass> findByWeekNotLikeAndCourse_courseIdAndWeekdayAndBegTime(String week, int courseId, String weekday, Time begTime);
}
