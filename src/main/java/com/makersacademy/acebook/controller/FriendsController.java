package com.makersacademy.acebook.controller;


import com.makersacademy.acebook.model.Friend;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.FriendRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class FriendsController {

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/friends")
    public String showFriends(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<User> friends = friendRepository.findFriendsByUser(currentUser);
            model.addAttribute("friends", friends);
        }
        return "users/friendList";
    }

    // Method to add friends
    @PostMapping("/friends/add")
    public RedirectView addFriend(@RequestParam Long friendId) {
        User currentUser = getCurrentUser();
        User friend = userRepository.findById(friendId).orElse(null);

        if (friend != null && !currentUser.equals(friend)) {
            currentUser.getFriends().add(friend);
            friend.getFriends().add(currentUser);
            userRepository.save(currentUser);
            userRepository.save(friend);
        }

        return new RedirectView("/friends");
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

    // Endpoint to list all users
    @GetMapping("/all-users")
    public String listAllUsers(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        List<User> allUsers = (List<User>) userRepository.findAll();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allUsers", allUsers);
        return "users/all-users"; // Returns the all-users.html view
    }
    }