package knbit.emm.service;

import knbit.emm.model.User;
import knbit.emm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(String index) {
        return userRepository.findOne(index);
    }
}
