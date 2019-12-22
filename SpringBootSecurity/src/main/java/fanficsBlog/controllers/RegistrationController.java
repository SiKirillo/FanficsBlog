package fanficsBlog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import fanficsBlog.models.Role;
import fanficsBlog.models.User;
import fanficsBlog.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/registration")
    public String registration() {
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationPage(User user, String rePassword, Model model) {
        if (user.getUsername() == null) {
            return "registration";
        }

        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.addAttribute("message", "User already exists!");
            return "registration";
        }

        if (!user.getPassword().equals(rePassword)) {
            model.addAttribute("message", "Passwords do not match!");
            return "registration";
        }

        user.setActive(true);
        user.setFirstVisit(LocalDateTime.now());
        user.setLastVisit(LocalDateTime.now());
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);

        return "redirect:/login";
    }
}
