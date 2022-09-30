package com.rtb.parallelplayer.service;

import com.rtb.parallelplayer.exception.AccessToRoomDeniedException;
import com.rtb.parallelplayer.model.RoomRole;
import com.rtb.parallelplayer.model.User;
import com.rtb.parallelplayer.model.UserForm;
import com.rtb.parallelplayer.repository.UserRepo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken)
            throw new RuntimeException("Unauthenticated");

        String currentUserEmail = authentication.getName();
        return getClientByEmail(currentUserEmail);
    }

    public User getClientByEmail(String email) {
        return userRepo.findClientByEmail(email)
                .orElseThrow(() -> new RuntimeException("There are no clients with email: " + email));
    }

    public User getClientById(Long id){
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("There are no clients with id: " + id));
    }

    public boolean saveNewUser(User userForm) {
        if (userRepo.findClientByEmail(userForm.getEmail()).isPresent())
            return false;
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        userRepo.save(userForm);
        return true;
    }

    // Converts UserForm to User and saves it
    public boolean saveNewUser(UserForm userForm) {
        User user = new User();
        user.setEmail(userForm.getEmail());
        user.setName(userForm.getName());
        user.setPassword(userForm.getPassword());
        return saveNewUser(user);
    }

    public RoomRole getRoleFor(Long userId, Long roomId){
        return userRepo.getRole(userId, roomId).orElseThrow(AccessToRoomDeniedException::new);
    }

    public void assignRoleToUserInRoom(Long userId, Long roomId, RoomRole role){
        Optional<RoomRole> savedRole = userRepo.getRole(userId, roomId);



        if (savedRole.isPresent()){
            userRepo.saveRole(userId, roomId, role.name());
        }else {
            userRepo.addRole(userId, roomId, role.name());
        }
    }
}
