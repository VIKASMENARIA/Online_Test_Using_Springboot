package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("index")
    public String home(){
        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
    @GetMapping("/quiz")
    public String quizPage() {
        return "quiz";
    }
    @GetMapping("/submitScore")
    public String submitScorePage() {
        return "submitScore"; // Corresponds to submitScore.html
    }
    @PostMapping("/api/users/{userId}/updateScore")
    @ResponseBody
    public ResponseEntity<String> updateScoreAndStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload) {
        int score = (int) payload.get("score");
        int totalQuestions = (int) payload.get("totalQuestions");
        boolean appeared = (boolean) payload.get("appeared");

        // Update the user's status and result in the database
        userService.updateUserScore(userId, score, totalQuestions);

        if (appeared) {
            userService.updateUserStatusToAppeared(userId); // Ensure status is updated to "Appeared"
        }

        return ResponseEntity.ok("Score and status updated successfully");
    }
    @PostMapping("/submit-score")
    public ResponseEntity<String> submitScore(@RequestBody Map<String, Object> payload) {
        int score = (int) payload.get("score");
        int totalQuestions = (int) payload.get("totalQuestions");

        // Save score to database (mock logic here)
        System.out.println("Score: " + score + ", Total Questions: " + totalQuestions);

        return ResponseEntity.ok("Score saved successfully");
    }
    @PostMapping("/api/users/updateQuiz")
    @ResponseBody
    public ResponseEntity<String> updateQuiz(@RequestBody Map<String, Object> payload) {
        Long userId = Long.parseLong(payload.get("userId").toString());
        int score = (int) payload.get("score");
        int totalQuestions = (int) payload.get("totalQuestions");
        boolean appeared = (boolean) payload.get("appeared");

        // Update user's status and score
        userService.updateUserScore(userId, score, totalQuestions);
        userService.updateUserStatusToAppeared(userId);

        return ResponseEntity.ok("Quiz submitted successfully!");
    }

    @GetMapping("/api/users/current")
    @ResponseBody
    public ResponseEntity<UserDto> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setStatus(user.getStatus());
        return ResponseEntity.ok(userDto);
    }
   



}
