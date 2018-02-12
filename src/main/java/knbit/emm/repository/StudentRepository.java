package knbit.emm.repository;

import knbit.emm.model.Student;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, String> {
    Student findByStudentId(@Param("studentId") String studentId);
    Set<Student> findAll();
}
