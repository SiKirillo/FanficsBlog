package fanficsBlog.controllers;

import fanficsBlog.models.Message;
import fanficsBlog.models.Role;
import fanficsBlog.models.User;
import fanficsBlog.repositories.MessageRepository;
import fanficsBlog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class MessageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @RequestMapping("/message/addMessage")
    public String addMessage() {
        return "message/addMessage";
    }

    @RequestMapping(value = "/message/addMessage", method = RequestMethod.POST)
    public String addMessagePage(Principal principal, String tag, String text) {
        User user = userRepository.findByUsername(principal.getName());
        Message message = new Message(text, tag, user);
        messageRepository.save(message);
        return "redirect:/home";
    }

    @RequestMapping("/message/edit/{id}")
    public String editMessage(@PathVariable("id") int id, Principal principal, Model model) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message id:" + id));

        User author = userRepository.findByUsername(principal.getName());
        if (!author.getRoles().contains(Role.ADMIN) && author.getId() != message.getAuthor().getId()) {
            return "error/403page";
        }

        model.addAttribute("message", message);
        return "message/update";
    }

    @RequestMapping("/message/update/{id}")
    public String updateMessage(@PathVariable("id") int id, @Valid Message message) {
        Message editMessage = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message id:" + id));
        editMessage.setTag(message.getTag());
        editMessage.setText(message.getText());
        messageRepository.save(editMessage);

        return "redirect:/home";
    }

    @RequestMapping("/message/delete/{id}")
    public String deleteMessage(@PathVariable("id") int id, Principal principal) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message id:" + id));

        User author = userRepository.findByUsername(principal.getName());
        if (!author.getRoles().contains(Role.ADMIN) && author.getId() != message.getAuthor().getId()) {
            return "error/403page";
        }

        messageRepository.delete(message);

        return "redirect:/home";
    }
}
