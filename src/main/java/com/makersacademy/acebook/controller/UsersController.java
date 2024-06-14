package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Authority;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.model.Friend;
import com.makersacademy.acebook.repository.AuthoritiesRepository;
import com.makersacademy.acebook.repository.UserRepository;
import com.makersacademy.acebook.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;





import java.util.Objects;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class UsersController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepository friendRepository;

    @Autowired
    AuthoritiesRepository authoritiesRepository;
    @Autowired
    EmailService emailService;

    @GetMapping("/register")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping("/users")
    public String signup(@ModelAttribute User user, Model model) {
        try {
            userRepository.save(user);
            Authority authority = new Authority(user.getUsername(), "ROLE_USER");
            authoritiesRepository.save(authority);

            // Send confirmation email
            emailService.sendSignUpConfirmation(user.getEmailAddress(), user.getUsername());

            return "redirect:/login";
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                if (Objects.requireNonNull(e.getMessage()).contains("users.username")) {
                    model.addAttribute("errorMessage", "Username already exists. Please choose a different username.");
                } else if (e.getMessage().contains("users.emailAddress")) {
                    model.addAttribute("errorMessage", "Email address already exists. Please use a different email.");
                } else {
                    model.addAttribute("errorMessage", "A database error occurred. Please try again.");
                }
            } else {
                model.addAttribute("errorMessage", "A database error occurred. Please try again.");
            }
            return "users/new";
        }
    }


    @GetMapping("/profile")
    public String showProfile(Model model) {
        User currentUser = getCurrentUser();
        model.addAttribute("user", currentUser);
        return "users/profile";
    }

    @PostMapping("/profile")
    public RedirectView updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        currentUser.setMobileNumber(user.getMobileNumber());
        currentUser.setEmailAddress(user.getEmailAddress());
        currentUser.setGender(user.getGender());
        currentUser.setCountry(user.getCountry());
        currentUser.setLanguage(user.getLanguage());
        userRepository.save(currentUser);
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");


        return new RedirectView("/profile");
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username);
    }

//    @GetMapping("/all")
//    public String getAllUsers(@RequestParam Long currentUserId, Model model) {
//        List<User> users = (List<User>) userRepository.findAll();
//
//        // Fetch friends of the current user
//        Arrays friendEntities = friendRepository.findByUserId(currentUserId);
//        Set<Long> friendIds = friendEntities.stream()
//                .map(friend -> friend.getFriend().getId())
//                .collect(Collectors.toSet());
//
//        // Add attributes to the model
//        model.addAttribute("users", users);
//        model.addAttribute("friendIds", friendIds);
//        model.addAttribute("currentUserId", currentUserId);
//
//        return "all-users";
//    }

}
