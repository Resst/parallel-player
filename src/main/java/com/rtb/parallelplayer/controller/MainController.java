package com.rtb.parallelplayer.controller;

import com.rtb.parallelplayer.model.Room;
import com.rtb.parallelplayer.model.RoomRole;
import com.rtb.parallelplayer.model.User;
import com.rtb.parallelplayer.service.RoomService;
import com.rtb.parallelplayer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    private final UserService userService;
    private final RoomService roomService;

    public MainController(UserService userService, RoomService roomService) {
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping
    public String index(){
        return "index";
    }

}
