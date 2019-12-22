package fanficsBlog.controllers;

import fanficsBlog.models.Message;
import fanficsBlog.models.User;
import fanficsBlog.repositories.MessageRepository;
import fanficsBlog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Value("${upload.path}")
    private String uploadPath;


    @RequestMapping("/user/profile")
    public String profile(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        List<Message> messages = messageRepository.findByAuthorId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("messages", messages);
        return "user/profile";
    }

    @RequestMapping(value = "/user/profile", method = RequestMethod.POST)
    public String profileName(@RequestParam("file") MultipartFile file, Principal principal, Model model) throws IOException {
        User user = userRepository.findByUsername(principal.getName());
        List<Message> messages = messageRepository.findByAuthorId(user.getId());

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadFile = new File(uploadPath);

            if (!uploadFile.exists()) {
                uploadFile.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));
            user.setPhotoName(resultFileName);
            userRepository.save(user);
        }

        model.addAttribute("user", user);
        model.addAttribute("messages", messages);

        return "user/profile";
    }

}
