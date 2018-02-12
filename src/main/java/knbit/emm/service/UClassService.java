package knbit.emm.service;

import knbit.emm.model.UClass;
import knbit.emm.repository.UClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UClassService {
    private final UClassRepository uClassRepository;

    @Autowired
    public UClassService(UClassRepository uClassRepository) {
        this.uClassRepository = uClassRepository;
    }


    public UClass findClass(int classId) {
        return uClassRepository.findOne(classId);
    }

    public Iterable<UClass> findAllClass() {
        return uClassRepository.findAll();
    }
}
