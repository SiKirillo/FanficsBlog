package fanficsBlog.controllers;

import fanficsBlog.models.Role;
import fanficsBlog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import fanficsBlog.models.User;
import fanficsBlog.repositories.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @RequestMapping("/allUsers")
    public String getAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        userService.sortUserById(users);
        model.addAttribute("users", users);
        return "allUsers";
    }

    @RequestMapping("/user/edit/{id}")
    public String editUser(@PathVariable("id") int id, Principal principal, Model model) {
        User admin = userRepository.findByUsername(principal.getName());
        if (!admin.getRoles().contains(Role.ADMIN) && admin.getId() != id) {
            return "error/403page";
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));

        model.addAttribute("user", user);
        return "user/update";
    }

    @RequestMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") int id, @Valid User user, String userRole, String adminRole, Principal principal, HttpServletRequest httpServletRequest) throws ServletException {
        User editUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));
        editUser.setUsername(user.getUsername());
        editUser.setEmail(user.getEmail());
        editUser.setPassword(user.getPassword());

        editUser.getRoles().clear();
        try {
            if (userRole.equals("on")) {
                editUser.getRoles().add(Role.USER);
            }
            if (adminRole.equals("on")) {
                editUser.getRoles().add(Role.ADMIN);
            }
        } catch (NullPointerException e) {
            editUser.getRoles().add(Role.USER);
        }

        try {
            if (user.getActive()) {
                editUser.setActive(false);
            }
        } catch (NullPointerException e) {
            editUser.setActive(true);
        }
        userRepository.save(editUser);

        if (principal.getName().equals(user.getUsername()) && !editUser.getActive()) {
            httpServletRequest.logout();
        }

        return "redirect:/allUsers";
    }

    @RequestMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, Principal principal, HttpServletRequest httpServletRequest) throws ServletException {
        User admin = userRepository.findByUsername(principal.getName());
        if (!admin.getRoles().contains(Role.ADMIN) && admin.getId() != id) {
            return "error/403page";
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));
        userRepository.delete(user);

        if (principal.getName().equals(user.getUsername())) {
            httpServletRequest.logout();
        }

        return "redirect:/allUsers";
    }

}