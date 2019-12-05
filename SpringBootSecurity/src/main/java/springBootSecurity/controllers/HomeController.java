package springBootSecurity.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springBootSecurity.models.User;
import springBootSecurity.repositories.UserRepository;
import springBootSecurity.services.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/", "/home"})
    public String home(Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            user.setLastVisit(LocalDateTime.now());
            userRepository.save(user);
        }
        return "home";
    }

    @RequestMapping("/allUsers")
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        userService.sortUserById(users);
        model.addAttribute("users", users);
        return "allUsers";
    }

}