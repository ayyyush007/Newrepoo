package com.ty.movie.controller;
import com.ty.movie.model.User;
import com.ty.movie.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        // return view for templates/auth/login.html
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        // return view for templates/auth/register.html
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult br,
                           Model model) {

        // server-side validation already handled by annotations
        if (br.hasErrors()) {
            return "auth/register";
        }

        if (userService.usernameExists(user.getUsername())) {
            br.rejectValue("username", "error.user", "Username already exists");
            return "auth/register";
        }

        userService.register(user); // must encode password & set ROLE_USER
        // redirect to login with success flag so login page shows green message
        return "redirect:/login?success";
    }
}
