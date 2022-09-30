package com.rtb.parallelplayer.controller;

import com.rtb.parallelplayer.exception.AccessToRoomDeniedException;
import com.rtb.parallelplayer.model.Layer;
import com.rtb.parallelplayer.model.Room;
import com.rtb.parallelplayer.model.RoomRole;
import com.rtb.parallelplayer.model.User;
import com.rtb.parallelplayer.service.LayerService;
import com.rtb.parallelplayer.service.RoomService;
import com.rtb.parallelplayer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;
    private final LayerService layerService;

    private static final String CREATE_ROOM_FORM_ROOM_NAME_PARAMETER = "roomName";

    public RoomController(RoomService roomService, UserService userService, LayerService layerService) {
        this.roomService = roomService;
        this.userService = userService;
        this.layerService = layerService;
    }

    @GetMapping
    public String roomsListPage(Model model){
        User currentUser = userService.getCurrentClient();
        List<Room> myRooms = roomService.getAllRooms(currentUser.getId());
        model.addAttribute("rooms", myRooms);

        return "roomsList";
    }


    @GetMapping("/{id}")
    public String getRoom(@PathVariable(name = "id") Long roomId, Model model){
        Room room = roomService.getRoomById(roomId);
        User user = userService.getCurrentClient();

        List<Layer> layers = layerService.getAllForRoom(room);

        RoomRole role = userService.getRoleFor(user.getId(), room.getId());

        model.addAttribute("role", role);
        model.addAttribute("room", room);
        model.addAttribute("layers", layers);

        return "room";
    }

    @PostMapping("/new")
    public String createRoom(HttpServletRequest request){
        String roomName = request.getParameter(CREATE_ROOM_FORM_ROOM_NAME_PARAMETER);
        roomService.createRoom(roomName);
        return "redirect:/rooms";
    }

    @GetMapping("/join/{inviteCode}")
    public String joinRoom(@PathVariable(name = "inviteCode") String inviteCode){
        Room room = roomService.getRoomByInviteCode(inviteCode);
        User user = userService.getCurrentClient();

        try{
            userService.getRoleFor(user.getId(), room.getId());
        }catch (AccessToRoomDeniedException e) {
            userService.assignRoleToUserInRoom(user.getId(), room.getId(), RoomRole.USER);
        }

        return "redirect:/rooms/" + room.getId();
    }

}
