package springBootSecurity.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springBootSecurity.models.User;
import springBootSecurity.repositories.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));
        model.addAttribute("user", user);
        return "update";
    }

    @RequestMapping("/update/{id}")
    public String update(@PathVariable("id") int id, @Valid User user, Principal principal, HttpServletRequest httpServletRequest) throws ServletException {
        User editUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));
        editUser.setUsername(user.getUsername());
        editUser.setEmail(user.getEmail());
        editUser.setPassword(user.getPassword());

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

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, Principal principal, HttpServletRequest httpServletRequest) throws ServletException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));
        userRepository.delete(user);

        if (principal.getName().equals(user.getUsername())) {
            httpServletRequest.logout();
        }

        return "redirect:/allUsers";
    }

    @RequestMapping("/deleteUsers}")
    public String deleteUsers(Principal principal, HttpServletRequest httpServletRequest, int... id) throws ServletException {

        for (int i : id) {
            User user = userRepository.findById(i)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + i));
            userRepository.delete(user);
        }

        User principalUser = userRepository.findByUsername(principal.getName());

        if (id.equals(principalUser.getId())) {
            httpServletRequest.logout();
        }

        return "redirect:/allUsers";
    }

}