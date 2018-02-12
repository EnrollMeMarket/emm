package knbit.emm.controller;

import knbit.emm.service.UserService;
import knbit.emm.model.User;
import knbit.emm.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    //TODO controller should return 'access denied' for not logged in user
    //but it is not done now because security mechanism will be changed

    @RequestMapping("/userRole/{index}")
    public User getUserRole(@PathVariable String index) {

        User user = userService.findUser(index);

        if (user != null) {
            return user;
        }

        return new User(index, UserRole.STUDENT);
    }
}
