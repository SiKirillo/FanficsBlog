package fanficsBlog.controllers;

import fanficsBlog.models.Message;
import fanficsBlog.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import fanficsBlog.models.User;
import fanficsBlog.repositories.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @RequestMapping(value = {"/", "/home"})
    public String home(Principal principal, String filter, Model model) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            user.setLastVisit(LocalDateTime.now());
            userRepository.save(user);
        }

        List<Message> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepository.findByTag(filter);
        } else {
            messages = messageRepository.findAll();
        }

        model.addAttribute("messages", messages);

        return "home";
    }

}