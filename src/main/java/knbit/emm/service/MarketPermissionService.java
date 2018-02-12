package knbit.emm.service;

import knbit.emm.model.Market;
import knbit.emm.model.User;
import knbit.emm.model.UserRole;
import knbit.emm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketPermissionService {

    private UserRepository userRepository;

    @Autowired
    public MarketPermissionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasUserPermissionToSeeOrEditMarket(Market market, String userId) {

        if (market.getCreatedBy().equals(userId)) {
            return true;
        }

        User user = userRepository.findOne(userId);

        if (user.getUserRole() == UserRole.ADMIN) {
            return true;
        }
        return false;
    }
}
