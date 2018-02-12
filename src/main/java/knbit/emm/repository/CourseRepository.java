package knbit.emm.repository;

import knbit.emm.model.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional()
@Repository
public interface CourseRepository extends CrudRepository<Course, Integer> {
    Course findByTitle(String title);


    Course findByCourseId(int id);
    List<Course> findByMarket_Name(String marketName);
}
