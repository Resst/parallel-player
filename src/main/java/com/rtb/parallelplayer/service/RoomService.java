package com.rtb.parallelplayer.service;

import com.rtb.parallelplayer.exception.InvalidInvitationLinkException;
import com.rtb.parallelplayer.exception.NoSuchRoomException;
import com.rtb.parallelplayer.model.Room;
import com.rtb.parallelplayer.repository.RoomRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class RoomService {

    private final RoomRepo roomRepo;

    public RoomService(RoomRepo roomRepo) {
        this.roomRepo = roomRepo;
    }

    public List<Room> getAllRooms(Long userId){
        return roomRepo.findAllByUser(userId);
    }

    public Room getRoomById(Long roomId){
        return roomRepo.findById(roomId).orElseThrow(NoSuchRoomException::new);
    }

    public Room getRoomByInviteCode(String inviteCode){
        if (inviteCode == null || inviteCode.equals(""))
            throw new InvalidInvitationLinkException();
        return roomRepo.findByInvitationLink(inviteCode).orElseThrow(InvalidInvitationLinkException::new);
    }

    public Room createRoom(String roomName){
        Room room = new Room(roomName, createUniqueInvitationLink());
        return roomRepo.saveAndFlush(room);
    }

    public Room setNewInvitationLink(Long roomId){
        Room room = getRoomById(roomId);
        room.setInvitationLink(createUniqueInvitationLink());
        return roomRepo.saveAndFlush(room);
    }

    private String createUniqueInvitationLink(){
        String link;
        do {
            link = InvitationLinkGenerator.get();
        }while (roomRepo.findByInvitationLink(link).isPresent());
        return link;
    }

    public static class InvitationLinkGenerator{
        private static final char[] str =
                {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        private static final int size = 8;

        public static String get(){
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < size; i++){
                sb.append(str[random.nextInt(str.length)]);
            }
            return sb.toString();
        }
    }
}

