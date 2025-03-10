package com.example.apartmentmanagement.controller;

import com.example.apartmentmanagement.dto.UserDTO;
import com.example.apartmentmanagement.entities.User;
import com.example.apartmentmanagement.util.AESUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.apartmentmanagement.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class HomeController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam String userName, @RequestParam String password, HttpSession session) {
        User user = userService.getUserByName(userName);
        if (user != null) {
            String decryptedPassword = AESUtil.decrypt(user.getPassword());
            if (password.equals(decryptedPassword)) {
                session.setAttribute("user", user);
                System.out.println("Session ID after login: " + session.getId());
                return "Login successful";
            }
        }
        return "Invalid credentials";
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUser(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok("Logged in as: " + user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
    }


    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out";
    }

}
