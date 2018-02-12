package knbit.emm.init.dev;

import knbit.emm.model.User;
import knbit.emm.model.UserRole;
import knbit.emm.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * There are test users added only in development profile.
 * It should be done by liquibase, like other data in db but for now
 * we don't have liquibase
 */
@Profile({"dev", "integ"})
@Component
public class SpecialUserInsertRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private static final Logger logger = Logger.getLogger(SpecialUserInsertRunner.class);


    @Autowired
    public SpecialUserInsertRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... strings) throws Exception {

        List<User> users = Arrays.asList(
                new User("3", UserRole.ADMIN),
                new User("2", UserRole.STAROSTA),
                new User("1", UserRole.STAROSTA)
        );

        userRepository.save(users);

        logger.info("Inserted test users");
        logger.info(users);
    }
}
