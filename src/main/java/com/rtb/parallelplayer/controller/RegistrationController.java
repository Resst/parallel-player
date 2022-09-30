package com.rtb.parallelplayer.controller;

import com.rtb.parallelplayer.model.UserForm;
import com.rtb.parallelplayer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String registerPage(Model model) {
        model.addAttribute("userForm", new UserForm());

        return "signup";
    }

    @PostMapping("/signup")
    public String registerNewUser(@ModelAttribute @Valid UserForm userForm,
                                  BindingResult bindingResult, Model model,
                                  HttpServletRequest request) {
        if (bindingResult.hasErrors())
            return "signup";

        if (!userService.saveNewUser(userForm)) {
            model.addAttribute("emailError", "Email is already taken");
            return "signup";
        }
        System.out.println(userForm);
        try {
            request.login(userForm.getEmail(), userForm.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }



    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
